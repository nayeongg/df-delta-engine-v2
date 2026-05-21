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
public class DeltaCommitRequest {

    @NotBlank
    private String spCode;

    @NotBlank
    private String datasetCode;
}
