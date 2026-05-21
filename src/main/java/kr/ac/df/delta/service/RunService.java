package kr.ac.df.delta.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
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
    public DeltaRun createRun(String spCode) {
        DeltaRun run = DeltaRun.builder()
                .runId(UUID.randomUUID().toString())
                .spCode(spCode)
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
}
