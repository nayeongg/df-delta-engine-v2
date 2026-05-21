package kr.ac.df.delta.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import kr.ac.df.delta.dto.CreateRunRequest;
import kr.ac.df.delta.entity.DeltaRun;
import kr.ac.df.delta.repository.RunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RunService {

    private final RunRepository runRepository;

    @Transactional
    public DeltaRun createRun(CreateRunRequest request) {
        DeltaRun run = DeltaRun.builder()
                .runId(UUID.randomUUID().toString())
                .spCode(request.getSpCode())
                .jobId(blankToNull(request.getJobId()))
                .datasetCode(blankToNull(request.getDatasetCode()))
                .requestType(blankToNull(request.getRequestType()))
                .triggerType(blankToNull(request.getTriggerType()))
                .requestedAt(blankToNull(request.getRequestedAt()))
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
        return runRepository.save(run);
    }

    @Transactional(readOnly = true)
    public DeltaRun getRun(String runId) {
        return runRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
    }

    @Transactional
    public DeltaRun markCommitted(String runId) {
        DeltaRun run = getRun(runId);
        run.setStatus("COMMITTED");
        run.setCommittedAt(LocalDateTime.now());
        return runRepository.save(run);
    }

    public void validateRunOwnership(DeltaRun run, String spCode) {
        Objects.requireNonNull(run, "run must not be null");
        if (!run.getSpCode().equals(spCode)) {
            throw new IllegalArgumentException("Run/spCode mismatch");
        }
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
