package com.rumortown.clue;

public record UpdateCollectedClueRequest(ClueImportance importance, CollectedClueStatus status) {
}