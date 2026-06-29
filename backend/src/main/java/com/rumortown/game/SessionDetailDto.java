package com.rumortown.game;

import java.util.List;

public record SessionDetailDto(
        Long id,
        Long userId,
        Long caseId,
        int currentStage,
        SessionStatus status,
        List<AvailableLocationDto> availableLocations,
        List<AvailableNpcDto> availableNpcs
) {
    public static SessionDetailDto from(
            GameSession session,
            List<AvailableLocationDto> availableLocations,
            List<AvailableNpcDto> availableNpcs
    ) {
        return new SessionDetailDto(
                session.getId(),
                session.getUser().getId(),
                session.getCaseFile().getId(),
                session.getCurrentStage(),
                session.getStatus(),
                availableLocations,
                availableNpcs);
    }
}