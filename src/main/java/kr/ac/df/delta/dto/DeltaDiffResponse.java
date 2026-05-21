package kr.ac.df.delta.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeltaDiffResponse {
    private String runId;
    private String spCode;
    private String datasetCode;
    private List<String> newRecordKeys;
    private List<String> changedRecordKeys;
    private List<String> revokedRecordKeys;
}
