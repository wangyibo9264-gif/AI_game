package com.rumortown.clue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseClueRepository extends JpaRepository<CaseClue, Long> {
    List<CaseClue> findByCaseFileIdAndClueCodeIn(Long caseId, Collection<String> clueCodes);

    Optional<CaseClue> findByIdAndCaseFileId(Long id, Long caseId);

    long countByCaseFileIdAndCriticalTrue(Long caseId);
}