package com.rumortown.game;

import com.rumortown.casefile.CaseNpc;

public record AvailableNpcDto(Long id, String code, String name, String roleName, Long locationId, Integer unlockStage) {
    public static AvailableNpcDto from(CaseNpc npc) {
        return new AvailableNpcDto(
                npc.getId(),
                npc.getCode(),
                npc.getName(),
                npc.getRoleName(),
                npc.getLocation().getId(),
                npc.getUnlockStage());
    }
}