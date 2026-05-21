package kr.ac.df.delta.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kr.ac.df.common.enums.DatasetCode;
import kr.ac.df.common.hash.RecordHashUtil;
import kr.ac.df.common.validation.DatasetRecordValidator;
import kr.ac.df.delta.dto.DeltaIncomingRequest;
import kr.ac.df.delta.dto.DeltaIncomingResponse;
import kr.ac.df.delta.entity.DeltaRun;
import kr.ac.df.delta.entity.IncomingRecord;
import kr.ac.df.delta.repository.IncomingRepository;
import kr.ac.df.delta.support.DatasetCodeNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IncomingService {

    private final RunService runService;
    private final IncomingRepository incomingRepository;

    @Transactional
    public DeltaIncomingResponse processIncoming(String runId, DeltaIncomingRequest request) {
        DeltaRun run = runService.getRun(runId);
        runService.validateRunOwnership(run, request.getSpCode());

        String normalizedDatasetCode = normalizeDatasetCode(request.getDatasetCode());
        DatasetCode datasetCode = DatasetCode.valueOf(normalizedDatasetCode);

        List<IncomingRecord> resolvedRecords = buildIncomingRecords(runId, request.getSpCode(), normalizedDatasetCode, datasetCode, request.getRecords());
        upsertIncomingRecords(runId, request.getSpCode(), normalizedDatasetCode, resolvedRecords);

        return DeltaIncomingResponse.builder()
                .runId(runId)
                .spCode(request.getSpCode())
                .datasetCode(normalizedDatasetCode)
                .ingestedCount(resolvedRecords.size())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    private List<IncomingRecord> buildIncomingRecords(
            String runId,
            String spCode,
            String datasetCode,
            DatasetCode dataset,
            List<Map<String, Object>> records
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        List<IncomingRecord> recordsToSave = new ArrayList<>(records.size());

        for (Map<String, Object> record : records) {
            DatasetRecordValidator.validateRecord(dataset, record, true);
            String recordKey = RecordHashUtil.generateRecordKey(record, dataset);
            String hash = RecordHashUtil.generateHash(record, dataset);

            recordsToSave.add(IncomingRecord.builder()
                    .runId(runId)
                    .spCode(spCode)
                    .datasetCode(datasetCode)
                    .recordKey(recordKey)
                    .currentHash(hash)
                    .loadedAt(now)
                    .build());
        }
        return recordsToSave;
    }

    private void upsertIncomingRecords(String runId, String spCode, String datasetCode, List<IncomingRecord> resolvedRecords) {
        if (resolvedRecords.isEmpty()) {
            return;
        }

        List<String> recordKeys = resolvedRecords.stream().map(IncomingRecord::getRecordKey).toList();
        List<IncomingRecord> existing = incomingRepository.findByRunIdAndSpCodeAndDatasetCodeAndRecordKeyIn(
                runId, spCode, datasetCode, recordKeys);

        Map<String, IncomingRecord> existingByKey = new HashMap<>();
        for (IncomingRecord row : existing) {
            existingByKey.put(row.getRecordKey(), row);
        }

        List<IncomingRecord> upserts = new ArrayList<>(resolvedRecords.size());
        for (IncomingRecord incoming : resolvedRecords) {
            IncomingRecord existingRow = existingByKey.get(incoming.getRecordKey());
            if (existingRow != null) {
                existingRow.setCurrentHash(incoming.getCurrentHash());
                upserts.add(existingRow);
            } else {
                upserts.add(incoming);
            }
        }

        incomingRepository.saveAll(upserts);
    }

    private String normalizeDatasetCode(String datasetCode) {
        return DatasetCodeNormalizer.normalize(datasetCode);
    }

}
