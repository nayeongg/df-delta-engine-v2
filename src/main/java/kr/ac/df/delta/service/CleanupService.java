package kr.ac.df.delta.service;

import kr.ac.df.delta.repository.IncomingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CleanupService {

    private final IncomingRepository incomingRepository;

    @Transactional
    public void cleanupIncoming(String runId) {
        incomingRepository.deleteByRunId(runId);
    }
}
