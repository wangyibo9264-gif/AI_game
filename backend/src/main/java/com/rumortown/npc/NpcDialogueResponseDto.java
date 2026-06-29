package com.rumortown.npc;

import com.rumortown.clue.CollectedClueDto;
import java.util.List;

public record NpcDialogueResponseDto(
        String reply,
        String mood,
        List<String> ruleHints,
        int suspicionDelta,
        List<CollectedClueDto> newlyCollectedClues
) {
}