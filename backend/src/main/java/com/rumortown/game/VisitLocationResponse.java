package com.rumortown.game;

import java.time.LocalDateTime;

public record VisitLocationResponse(
        Long id,
        Long sessionId,
        Long locationId,
        String locationCode,
        LocalDateTime visitedAt
) {
    public static VisitLocationResponse from(LocationVisit visit) {
        return new VisitLocationResponse(
                visit.getId(),
                visit.getSession().getId(),
                visit.getLocation().getId(),
                visit.getLocation().getCode(),
                visit.getVisitedAt());
    }
}