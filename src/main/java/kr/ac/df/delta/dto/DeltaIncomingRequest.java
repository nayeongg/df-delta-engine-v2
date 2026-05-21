package kr.ac.df.delta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeltaIncomingRequest {

    @NotBlank
    private String spCode;

    @NotBlank
    private String datasetCode;

    @NotNull
    @Size(min = 1, max = 1000)
    private List<Map<String, Object>> records;
}
