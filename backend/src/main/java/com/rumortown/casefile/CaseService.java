package com.rumortown.casefile;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CaseService {
    private final CaseFileRepository caseFileRepository;
    private final CaseRuleRepository caseRuleRepository;
    private final CaseLocationRepository caseLocationRepository;

    public CaseService(
            CaseFileRepository caseFileRepository,
            CaseRuleRepository caseRuleRepository,
            CaseLocationRepository caseLocationRepository
    ) {
        this.caseFileRepository = caseFileRepository;
        this.caseRuleRepository = caseRuleRepository;
        this.caseLocationRepository = caseLocationRepository;
    }

    @Transactional(readOnly = true)
    public List<CaseSummaryDto> listCases() {
        return caseFileRepository.findAllByOrderByIdAsc().stream()
                .map(CaseSummaryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CaseDetailDto getCaseDetail(Long caseId) {
        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        List<CaseRuleDto> rules = caseRuleRepository.findByCaseFileIdOrderByDisplayOrderAsc(caseId).stream()
                .map(CaseRuleDto::from)
                .toList();
        List<CaseLocationDto> locations = caseLocationRepository.findByCaseFileIdOrderByUnlockStageAscCodeAsc(caseId).stream()
                .map(CaseLocationDto::from)
                .toList();
        return CaseDetailDto.from(caseFile, rules, locations);
    }
}
