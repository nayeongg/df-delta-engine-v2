package kr.ac.df.delta.controller;

import jakarta.validation.Valid;
import kr.ac.df.delta.dto.DeltaIncomingRequest;
import kr.ac.df.delta.dto.DeltaIncomingResponse;
import kr.ac.df.delta.service.IncomingService;
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
public class IncomingController {

    private final IncomingService incomingService;

    @PostMapping("/{runId}/incoming")
    public ResponseEntity<DeltaIncomingResponse> processIncoming(
            @PathVariable String runId,
            @Valid @RequestBody DeltaIncomingRequest request
    ) {
        return ResponseEntity.ok(incomingService.processIncoming(runId, request));
    }
}
