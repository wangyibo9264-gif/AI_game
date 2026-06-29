package com.rumortown.casefile;

public record CaseLocationDto(Long id, String code, String name, String description, Integer unlockStage) {
    public static CaseLocationDto from(CaseLocation location) {
        return new CaseLocationDto(
                location.getId(),
                location.getCode(),
                location.getName(),
                location.getDescription(),
                location.getUnlockStage());
    }
}
