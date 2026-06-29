package com.rumortown.report;

public record TruthReportResultDto(
        Long reportId,
        Integer truthScore,
        Integer clueScore,
        Integer ruleScore,
        Integer totalScore,
        String ending,
        String summary,
        String missedPoints
) {
    public static TruthReportResultDto from(TruthReport report, ReportScore score) {
        return new TruthReportResultDto(
                report.getId(),
                score.getTruthScore(),
                score.getClueScore(),
                score.getRuleScore(),
                score.getTruthScore() + score.getClueScore() + score.getRuleScore(),
                score.getEnding(),
                score.getSummary(),
                score.getMissedPoints());
    }
}