package kr.ac.df.delta.controller;

import jakarta.validation.Valid;
import kr.ac.df.delta.dto.CreateRunRequest;
import kr.ac.df.delta.entity.DeltaRun;
import kr.ac.df.delta.service.RunService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class RunController {

    private final RunService runService;

    @PostMapping
    public ResponseEntity<DeltaRun> createRun(@Valid @RequestBody CreateRunRequest request) {
        return ResponseEntity.ok(runService.createRun(request.getSpCode()));
    }
}
