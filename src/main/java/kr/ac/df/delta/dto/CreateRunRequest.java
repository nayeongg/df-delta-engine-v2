package kr.ac.df.delta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRunRequest {

    @NotBlank
    private String spCode;

    private String jobId;

    private String datasetCode;

    private String requestType;

    private String triggerType;

    private String requestedAt;
}
