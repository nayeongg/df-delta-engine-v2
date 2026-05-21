package kr.ac.df.delta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeltaIncomingResponse {
    private String runId;
    private String spCode;
    private String datasetCode;
    private int ingestedCount;
    private long timestamp;
}
