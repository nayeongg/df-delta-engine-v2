package kr.ac.df.delta.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingRecordId implements Serializable {
    private String runId;
    private String spCode;
    private String datasetCode;
    private String recordKey;
}
