package com.rumortown.game;

import com.rumortown.casefile.CaseLocation;

public record AvailableLocationDto(Long id, String code, String name, String description, Integer unlockStage) {
    public static AvailableLocationDto from(CaseLocation location) {
        return new AvailableLocationDto(
                location.getId(),
                location.getCode(),
                location.getName(),
                location.getDescription(),
                location.getUnlockStage());
    }
}