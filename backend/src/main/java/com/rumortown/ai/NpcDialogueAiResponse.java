package com.rumortown.ai;

import java.util.List;

public record NpcDialogueAiResponse(
        String reply,
        String mood,
        List<String> revealedClueCodes,
        List<String> ruleHints,
        int suspicionDelta
) {
    public static NpcDialogueAiResponse safe(String reply) {
        return new NpcDialogueAiResponse(reply, "谨慎", List.of(), List.of(), 0);
    }

    public NpcDialogueAiResponse sanitized() {
        return new NpcDialogueAiResponse(
                isBlank(reply) ? "对方沉默片刻，只给出含糊的回应。" : reply,
                isBlank(mood) ? "谨慎" : mood,
                revealedClueCodes == null ? List.of() : List.copyOf(revealedClueCodes),
                ruleHints == null ? List.of() : List.copyOf(ruleHints),
                Math.max(-5, Math.min(5, suspicionDelta)));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}