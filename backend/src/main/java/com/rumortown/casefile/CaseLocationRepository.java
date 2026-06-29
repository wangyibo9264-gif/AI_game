package com.rumortown.casefile;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseLocationRepository extends JpaRepository<CaseLocation, Long> {
    List<CaseLocation> findByCaseFileIdOrderByUnlockStageAscCodeAsc(Long caseId);

    List<CaseLocation> findByCaseFileIdAndUnlockStageLessThanEqualOrderByUnlockStageAscCodeAsc(Long caseId, Integer stage);
}