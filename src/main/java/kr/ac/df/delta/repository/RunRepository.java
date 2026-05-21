package kr.ac.df.delta.repository;

import kr.ac.df.delta.entity.DeltaRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunRepository extends JpaRepository<DeltaRun, String> {
}
