package kr.ac.df.delta.controller;

import kr.ac.df.delta.dto.DeltaDiffResponse;
import kr.ac.df.delta.service.DiffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class DiffController {

    private final DiffService diffService;

    @GetMapping("/{runId}/diff")
    public ResponseEntity<DeltaDiffResponse> diff(
            @PathVariable String runId,
            @RequestParam String spCode,
            @RequestParam String datasetCode
    ) {
        return ResponseEntity.ok(diffService.diff(runId, spCode, datasetCode));
    }
}
