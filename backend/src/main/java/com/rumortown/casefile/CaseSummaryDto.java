package com.rumortown.casefile;

public record CaseSummaryDto(
        Long id,
        String code,
        String title,
        String summary,
        String difficulty,
        Integer estimatedMinutes,
        CaseStatus status
) {
    public static CaseSummaryDto from(CaseFile caseFile) {
        return new CaseSummaryDto(
                caseFile.getId(),
                caseFile.getCode(),
                caseFile.getTitle(),
                caseFile.getSummary(),
                caseFile.getDifficulty(),
                caseFile.getEstimatedMinutes(),
                caseFile.getStatus());
    }
}
