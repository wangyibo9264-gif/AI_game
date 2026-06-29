package com.rumortown.casefile;

import java.util.List;

public record CaseDetailDto(
        Long id,
        String code,
        String title,
        String summary,
        String difficulty,
        Integer estimatedMinutes,
        CaseStatus status,
        List<CaseRuleDto> rules,
        List<CaseLocationDto> locations
) {
    public static CaseDetailDto from(
            CaseFile caseFile,
            List<CaseRuleDto> rules,
            List<CaseLocationDto> locations
    ) {
        return new CaseDetailDto(
                caseFile.getId(),
                caseFile.getCode(),
                caseFile.getTitle(),
                caseFile.getSummary(),
                caseFile.getDifficulty(),
                caseFile.getEstimatedMinutes(),
                caseFile.getStatus(),
                rules,
                locations);
    }
}
