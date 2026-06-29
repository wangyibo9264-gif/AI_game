package com.rumortown.game;

import com.rumortown.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {
    private final GameSessionService gameSessionService;

    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping
    public ApiResponse<SessionDetailDto> createSession(@RequestBody CreateSessionRequest request) {
        return ApiResponse.data(gameSessionService.createSession(request));
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<SessionDetailDto> getSession(@PathVariable Long sessionId) {
        return ApiResponse.data(gameSessionService.getSession(sessionId));
    }

    @PostMapping("/{sessionId}/locations/{locationId}/visit")
    public ApiResponse<VisitLocationResponse> visitLocation(
            @PathVariable Long sessionId,
            @PathVariable Long locationId
    ) {
        return ApiResponse.data(gameSessionService.visitLocation(sessionId, locationId));
    }

    @PostMapping("/{sessionId}/advance")
    public ApiResponse<SessionDetailDto> advance(@PathVariable Long sessionId) {
        return ApiResponse.data(gameSessionService.advance(sessionId));
    }
}