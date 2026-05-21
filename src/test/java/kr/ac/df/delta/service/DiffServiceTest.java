package kr.ac.df.delta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import kr.ac.df.delta.dto.DeltaDiffResponse;
import kr.ac.df.delta.entity.DeltaRun;
import kr.ac.df.delta.entity.IncomingRecord;
import kr.ac.df.delta.entity.SnapshotRecord;
import kr.ac.df.delta.repository.IncomingRepository;
import kr.ac.df.delta.repository.SnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiffServiceTest {

    @Mock
    private RunService runService;

    @Mock
    private IncomingRepository incomingRepository;

    @Mock
    private SnapshotRepository snapshotRepository;

    private DiffService diffService;

    @BeforeEach
    void setUp() {
        diffService = new DiffService(runService, incomingRepository, snapshotRepository);
    }

    @Test
    void diff_shouldClassifyNewChangedRevoked() {
        String runId = "run-1";
        String spCode = "SP001";
        String datasetCode = "STDNT_BASIC";

        DeltaRun run = DeltaRun.builder()
                .runId(runId)
                .spCode(spCode)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        when(runService.getRun(runId)).thenReturn(run);

        List<IncomingRecord> incoming = List.of(
                incoming(runId, spCode, datasetCode, "k1", "h1"),
                incoming(runId, spCode, datasetCode, "k2", "h2-new"),
                incoming(runId, spCode, datasetCode, "k4", "h4")
        );

        List<SnapshotRecord> snapshot = List.of(
                snapshot(spCode, datasetCode, "k2", "h2-old"),
                snapshot(spCode, datasetCode, "k3", "h3"),
                snapshot(spCode, datasetCode, "k4", "h4")
        );

        when(incomingRepository.findByRunIdAndSpCodeAndDatasetCode(runId, spCode, datasetCode)).thenReturn(incoming);
        when(snapshotRepository.findBySpCodeAndDatasetCode(spCode, datasetCode)).thenReturn(snapshot);

        DeltaDiffResponse response = diffService.diff(runId, spCode, datasetCode);

        assertThat(response.getNewRecordKeys()).containsExactlyInAnyOrder("k1");
        assertThat(response.getChangedRecordKeys()).containsExactlyInAnyOrder("k2");
        assertThat(response.getRevokedRecordKeys()).containsExactlyInAnyOrder("k3");
    }

    @Test
    void diff_shouldRejectRunOwnershipMismatch() {
        String runId = "run-1";
        String spCode = "SP001";
        String datasetCode = "STDNT_BASIC";

        DeltaRun run = DeltaRun.builder()
                .runId(runId)
                .spCode("SP999")
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        when(runService.getRun(runId)).thenReturn(run);
        doThrow(new IllegalArgumentException("Run/spCode mismatch"))
                .when(runService).validateRunOwnership(run, spCode);

        assertThatThrownBy(() -> diffService.diff(runId, spCode, datasetCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Run/spCode mismatch");
    }

    private IncomingRecord incoming(String runId, String spCode, String datasetCode, String key, String hash) {
        return IncomingRecord.builder()
                .runId(runId)
                .spCode(spCode)
                .datasetCode(datasetCode)
                .recordKey(key)
                .currentHash(hash)
                .loadedAt(OffsetDateTime.now())
                .build();
    }

    private SnapshotRecord snapshot(String spCode, String datasetCode, String key, String hash) {
        return SnapshotRecord.builder()
                .spCode(spCode)
                .datasetCode(datasetCode)
                .recordKey(key)
                .lastHash(hash)
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
