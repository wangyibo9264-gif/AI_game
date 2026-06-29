package com.rumortown.ai;

import java.util.List;

public record ReportReviewAiRequest(
        String caseCode,
        String eventSummary,
        String responsiblePerson,
        String keyEvidence,
        String ruleExplanation,
        String npcLies,
        String conclusion,
        List<String> collectedClueCodes,
        List<String> canonicalAnswerPoints
) {
}