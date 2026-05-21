package kr.ac.df.delta.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.df.delta.dto.DeltaDiffResponse;
import kr.ac.df.delta.entity.DeltaRun;
import kr.ac.df.delta.entity.IncomingRecord;
import kr.ac.df.delta.entity.SnapshotRecord;
import kr.ac.df.delta.repository.IncomingRepository;
import kr.ac.df.delta.repository.SnapshotRepository;
import kr.ac.df.delta.support.DatasetCodeNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiffService {

    private final RunService runService;
    private final IncomingRepository incomingRepository;
    private final SnapshotRepository snapshotRepository;

    @Transactional(readOnly = true)
    public DeltaDiffResponse diff(String runId, String spCode, String datasetCode) {
        DeltaRun run = runService.getRun(runId);
        runService.validateRunOwnership(run, spCode);
        String normalizedDatasetCode = normalizeDatasetCode(datasetCode);

        List<IncomingRecord> incoming = incomingRepository.findByRunIdAndSpCodeAndDatasetCode(runId, spCode, normalizedDatasetCode);
        List<SnapshotRecord> snapshot = snapshotRepository.findBySpCodeAndDatasetCode(spCode, normalizedDatasetCode);

        Map<String, String> incomingMap = toIncomingMap(incoming);
        Map<String, String> snapshotMap = toSnapshotMap(snapshot);

        List<String> newKeys = new ArrayList<>();
        List<String> changedKeys = new ArrayList<>();

        for (Map.Entry<String, String> entry : incomingMap.entrySet()) {
            String key = entry.getKey();
            String incomingHash = entry.getValue();
            if (!snapshotMap.containsKey(key)) {
                newKeys.add(key);
            } else if (!incomingHash.equals(snapshotMap.get(key))) {
                changedKeys.add(key);
            }
        }

        Set<String> incomingKeys = new HashSet<>(incomingMap.keySet());
        List<String> revokedKeys = new ArrayList<>();
        for (String snapshotKey : snapshotMap.keySet()) {
            if (!incomingKeys.contains(snapshotKey)) {
                revokedKeys.add(snapshotKey);
            }
        }

        return DeltaDiffResponse.builder()
                .runId(runId)
                .spCode(spCode)
                .datasetCode(normalizedDatasetCode)
                .newRecordKeys(newKeys)
                .changedRecordKeys(changedKeys)
                .revokedRecordKeys(revokedKeys)
                .build();
    }

    private Map<String, String> toIncomingMap(List<IncomingRecord> incoming) {
        Map<String, String> map = new HashMap<>();
        for (IncomingRecord record : incoming) {
            map.put(record.getRecordKey(), record.getCurrentHash());
        }
        return map;
    }

    private Map<String, String> toSnapshotMap(List<SnapshotRecord> snapshot) {
        Map<String, String> map = new HashMap<>();
        for (SnapshotRecord record : snapshot) {
            map.put(record.getRecordKey(), record.getLastHash());
        }
        return map;
    }

    private String normalizeDatasetCode(String datasetCode) {
        return DatasetCodeNormalizer.normalize(datasetCode);
    }
}
