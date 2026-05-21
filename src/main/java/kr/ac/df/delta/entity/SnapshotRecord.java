package kr.ac.df.delta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(SnapshotRecordId.class)
@Table(name = "delta_record_store")
public class SnapshotRecord {

    @Id
    @Column(name = "sp_code", columnDefinition = "text", nullable = false)
    private String spCode;

    @Id
    @Column(name = "dataset_code", columnDefinition = "text", nullable = false)
    private String datasetCode;

    @Id
    @Column(name = "record_key", columnDefinition = "text", nullable = false)
    private String recordKey;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "last_hash", length = 64, nullable = false)
    private String lastHash;

    @Column(name = "last_sent_at")
    private OffsetDateTime lastSentAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
