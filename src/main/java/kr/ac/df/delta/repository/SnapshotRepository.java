package kr.ac.df.delta.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.df.delta.entity.SnapshotRecord;
import kr.ac.df.delta.entity.SnapshotRecordId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<SnapshotRecord, SnapshotRecordId> {

    List<SnapshotRecord> findBySpCodeAndDatasetCode(String spCode, String datasetCode);

    List<SnapshotRecord> findBySpCodeAndDatasetCodeAndRecordKeyIn(String spCode, String datasetCode, List<String> recordKeys);

    Optional<SnapshotRecord> findBySpCodeAndDatasetCodeAndRecordKey(String spCode, String datasetCode, String recordKey);

    void deleteBySpCodeAndDatasetCodeAndRecordKey(String spCode, String datasetCode, String recordKey);

    void deleteBySpCodeAndDatasetCodeAndRecordKeyIn(String spCode, String datasetCode, List<String> recordKeys);
}
