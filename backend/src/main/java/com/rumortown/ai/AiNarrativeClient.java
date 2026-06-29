package com.rumortown.ai;

public interface AiNarrativeClient {
    NpcDialogueAiResponse generateNpcReply(NpcDialogueAiRequest request);

    ReportReviewAiResponse reviewTruthReport(ReportReviewAiRequest request);
}