package com.rumortown.report;

public record SubmitTruthReportRequest(
        String eventSummary,
        String responsiblePerson,
        String keyEvidence,
        String ruleExplanation,
        String npcLies,
        String conclusion
) {
}