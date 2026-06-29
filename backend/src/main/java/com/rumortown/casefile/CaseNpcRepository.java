package com.rumortown.casefile;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseNpcRepository extends JpaRepository<CaseNpc, Long> {
    List<CaseNpc> findByCaseFileIdAndUnlockStageLessThanEqualOrderByUnlockStageAscCodeAsc(Long caseId, Integer stage);
}