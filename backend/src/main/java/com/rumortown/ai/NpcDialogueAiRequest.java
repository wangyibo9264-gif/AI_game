package com.rumortown.ai;

import java.util.List;

public record NpcDialogueAiRequest(
        String caseCode,
        String npcCode,
        String npcName,
        String roleName,
        int currentStage,
        String playerQuestion,
        String knownFacts,
        List<String> collectedClueCodes,
        List<String> allowedRevealClueCodes,
        List<String> rules
) {
}