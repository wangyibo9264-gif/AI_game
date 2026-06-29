package com.rumortown.npc;

import com.rumortown.common.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions/{sessionId}/npcs/{npcId}/messages")
public class NpcDialogueController {
    private final NpcDialogueService npcDialogueService;

    public NpcDialogueController(NpcDialogueService npcDialogueService) {
        this.npcDialogueService = npcDialogueService;
    }

    @PostMapping
    public ApiResponse<NpcDialogueResponseDto> sendMessage(
            @PathVariable Long sessionId,
            @PathVariable Long npcId,
            @RequestBody NpcMessageRequest request
    ) {
        return ApiResponse.data(npcDialogueService.sendMessage(sessionId, npcId, request));
    }
}