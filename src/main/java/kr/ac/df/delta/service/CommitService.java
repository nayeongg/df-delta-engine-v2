package kr.ac.df.delta.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.df.delta.dto.DeltaCommitRequest;
import kr.ac.df.delta.dto.DeltaCommitResponse;
import kr.ac.df.delta.dto.DeltaDiffResponse;
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
public class CommitService {

    private final DiffService diffService;
    private final RunService runService;
    private final IncomingRepository incomingRepository;
    private final SnapshotRepository snapshotRepository;

    @Transactional
    public DeltaCommitResponse commit(String runId, DeltaCommitRequest request) {
        String normalizedDatasetCode = normalizeDatasetCode(request.getDatasetCode());

        DeltaDiffResponse diff = diffService.diff(runId, request.getSpCode(), normalizedDatasetCode);
        List<IncomingRecord> incoming = incomingRepository.findByRunIdAndSpCodeAndDatasetCode(
                runId, request.getSpCode(), normalizedDatasetCode);

        upsertNewAndChanged(request.getSpCode(), normalizedDatasetCode, diff, incoming);
        deleteRevoked(request.getSpCode(), normalizedDatasetCode, diff);

        runService.markCommitted(runId);

        return DeltaCommitResponse.builder()
                .runId(runId)
                .spCode(request.getSpCode())
                .datasetCode(normalizedDatasetCode)
                .newCount(diff.getNewRecordKeys().size())
                .changedCount(diff.getChangedRecordKeys().size())
                .revokedCount(diff.getRevokedRecordKeys().size())
                .committedAt(System.currentTimeMillis())
                .build();
    }

    private void upsertNewAndChanged(String spCode, String datasetCode, DeltaDiffResponse diff, List<IncomingRecord> incoming) {
        Set<String> upsertKeys = new HashSet<>();
        upsertKeys.addAll(diff.getNewRecordKeys());
        upsertKeys.addAll(diff.getChangedRecordKeys());

        if (upsertKeys.isEmpty()) {
            return;
        }

        List<String> upsertKeyList = new ArrayList<>(upsertKeys);
        List<SnapshotRecord> existing = snapshotRepository.findBySpCodeAndDatasetCodeAndRecordKeyIn(spCode, datasetCode, upsertKeyList);
        Map<String, SnapshotRecord> existingByKey = new HashMap<>();
        for (SnapshotRecord row : existing) {
            existingByKey.put(row.getRecordKey(), row);
        }

        List<SnapshotRecord> upserts = new ArrayList<>();
        for (IncomingRecord in : incoming) {
            if (!upsertKeys.contains(in.getRecordKey())) {
                continue;
            }

            SnapshotRecord snapshot = existingByKey.get(in.getRecordKey());
            if (snapshot == null) {
                snapshot = SnapshotRecord.builder()
                        .spCode(spCode)
                        .datasetCode(datasetCode)
                        .recordKey(in.getRecordKey())
                        .build();
            }
            OffsetDateTime now = OffsetDateTime.now();
            snapshot.setLastHash(in.getCurrentHash());
            snapshot.setLastSentAt(now);
            snapshot.setUpdatedAt(now);
            upserts.add(snapshot);
        }

        if (!upserts.isEmpty()) {
            snapshotRepository.saveAll(upserts);
        }
    }

    private void deleteRevoked(String spCode, String datasetCode, DeltaDiffResponse diff) {
        if (!diff.getRevokedRecordKeys().isEmpty()) {
            snapshotRepository.deleteBySpCodeAndDatasetCodeAndRecordKeyIn(spCode, datasetCode, diff.getRevokedRecordKeys());
        }
    }

    private String normalizeDatasetCode(String datasetCode) {
        return DatasetCodeNormalizer.normalize(datasetCode);
    }
}
