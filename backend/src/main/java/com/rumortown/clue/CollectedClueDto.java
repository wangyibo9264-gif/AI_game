package com.rumortown.clue;

import java.time.LocalDateTime;

public record CollectedClueDto(
        Long id,
        Long clueId,
        String clueCode,
        String title,
        String content,
        ClueCategory category,
        boolean critical,
        ClueImportance importance,
        CollectedClueStatus status,
        LocalDateTime collectedAt
) {
    public static CollectedClueDto from(CollectedClue collectedClue) {
        CaseClue clue = collectedClue.getClue();
        return new CollectedClueDto(
                collectedClue.getId(),
                clue.getId(),
                clue.getClueCode(),
                clue.getTitle(),
                clue.getContent(),
                clue.getCategory(),
                clue.isCritical(),
                collectedClue.getImportance(),
                collectedClue.getStatus(),
                collectedClue.getCollectedAt());
    }
}