package com.rumortown.ai;

import java.util.List;

public record ReportReviewAiResponse(
        String summary,
        List<String> missedPoints,
        String endingSuggestion
) {
    public static ReportReviewAiResponse safe() {
        return new ReportReviewAiResponse("报告已记录，等待调查局复核。", List.of(), "未定结局");
    }

    public ReportReviewAiResponse sanitized() {
        return new ReportReviewAiResponse(
                summary == null || summary.isBlank() ? "报告已记录，等待调查局复核。" : summary,
                missedPoints == null ? List.of() : List.copyOf(missedPoints),
                endingSuggestion == null || endingSuggestion.isBlank() ? "未定结局" : endingSuggestion);
    }
}