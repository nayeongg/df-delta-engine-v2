package kr.ac.df.delta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delta_runs")
public class DeltaRun {

    @Id
    @Column(name = "run_id", length = 50, nullable = false)
    private String runId;

    @Column(name = "sp_code", length = 20, nullable = false)
    private String spCode;

    @Column(name = "job_id", length = 100)
    private String jobId;

    @Column(name = "dataset_code", length = 50)
    private String datasetCode;

    @Column(name = "request_type", length = 30)
    private String requestType;

    @Column(name = "trigger_type", length = 30)
    private String triggerType;

    @Column(name = "requested_at", length = 50)
    private String requestedAt;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "committed_at")
    private LocalDateTime committedAt;
}
