package kr.ac.df.delta.controller;

import kr.ac.df.delta.service.CleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class CleanupController {

    private final CleanupService cleanupService;

    @DeleteMapping("/{runId}/incoming")
    public ResponseEntity<Void> cleanupIncoming(@PathVariable String runId) {
        cleanupService.cleanupIncoming(runId);
        return ResponseEntity.noContent().build();
    }
}
