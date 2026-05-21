package kr.ac.df.delta.controller;

import jakarta.validation.Valid;
import kr.ac.df.delta.dto.DeltaCommitRequest;
import kr.ac.df.delta.dto.DeltaCommitResponse;
import kr.ac.df.delta.service.CommitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class CommitController {

    private final CommitService commitService;

    @PostMapping("/{runId}/commit")
    public ResponseEntity<DeltaCommitResponse> commit(
            @PathVariable String runId,
            @Valid @RequestBody DeltaCommitRequest request
    ) {
        return ResponseEntity.ok(commitService.commit(runId, request));
    }
}
