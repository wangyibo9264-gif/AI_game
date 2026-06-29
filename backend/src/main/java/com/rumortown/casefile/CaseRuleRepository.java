package com.rumortown.casefile;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRuleRepository extends JpaRepository<CaseRule, Long> {
    List<CaseRule> findByCaseFileIdOrderByDisplayOrderAsc(Long caseId);
}
