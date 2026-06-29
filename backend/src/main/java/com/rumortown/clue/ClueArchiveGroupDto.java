package com.rumortown.clue;

import java.util.List;

public record ClueArchiveGroupDto(ClueCategory category, List<CollectedClueDto> clues) {
}