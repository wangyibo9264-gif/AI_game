package com.rumortown.clue;

import com.rumortown.common.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions/{sessionId}/clues")
public class ClueController {
    private final ClueService clueService;

    public ClueController(ClueService clueService) {
        this.clueService = clueService;
    }

    @GetMapping
    public ApiResponse<List<ClueArchiveGroupDto>> listArchive(@PathVariable Long sessionId) {
        return ApiResponse.data(clueService.listArchive(sessionId));
    }

    @PatchMapping("/{clueId}")
    public ApiResponse<CollectedClueDto> updateClue(
            @PathVariable Long sessionId,
            @PathVariable Long clueId,
            @RequestBody UpdateCollectedClueRequest request
    ) {
        return ApiResponse.data(clueService.updateCollectedClue(sessionId, clueId, request));
    }
}