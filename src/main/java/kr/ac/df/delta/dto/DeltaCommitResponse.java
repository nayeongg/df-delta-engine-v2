package kr.ac.df.delta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeltaCommitResponse {
    private String runId;
    private String spCode;
    private String datasetCode;
    private int newCount;
    private int changedCount;
    private int revokedCount;
    private long committedAt;
}
