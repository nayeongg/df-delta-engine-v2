package kr.ac.df.delta.repository;

import java.util.List;
import kr.ac.df.delta.entity.IncomingRecord;
import kr.ac.df.delta.entity.IncomingRecordId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomingRepository extends JpaRepository<IncomingRecord, IncomingRecordId> {

    List<IncomingRecord> findByRunIdAndSpCodeAndDatasetCode(String runId, String spCode, String datasetCode);

    List<IncomingRecord> findByRunIdAndSpCodeAndDatasetCodeAndRecordKeyIn(
            String runId,
            String spCode,
            String datasetCode,
            List<String> recordKeys
    );

    void deleteByRunId(String runId);

    void deleteByRunIdAndSpCodeAndDatasetCode(String runId, String spCode, String datasetCode);
}
