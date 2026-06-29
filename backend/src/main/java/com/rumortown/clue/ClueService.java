package com.rumortown.clue;

import com.rumortown.game.GameSession;
import com.rumortown.game.GameSessionRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClueService {
    private final GameSessionRepository gameSessionRepository;
    private final CaseClueRepository caseClueRepository;
    private final CollectedClueRepository collectedClueRepository;

    public ClueService(
            GameSessionRepository gameSessionRepository,
            CaseClueRepository caseClueRepository,
            CollectedClueRepository collectedClueRepository
    ) {
        this.gameSessionRepository = gameSessionRepository;
        this.caseClueRepository = caseClueRepository;
        this.collectedClueRepository = collectedClueRepository;
    }

    @Transactional(readOnly = true)
    public List<ClueArchiveGroupDto> listArchive(Long sessionId) {
        requireSession(sessionId);
        Map<ClueCategory, List<CollectedClueDto>> grouped = new LinkedHashMap<>();
        for (CollectedClue collectedClue : collectedClueRepository.findBySessionIdOrderByClueCategoryAscClueIdAsc(sessionId)) {
            grouped.computeIfAbsent(collectedClue.getClue().getCategory(), ignored -> new ArrayList<>())
                    .add(CollectedClueDto.from(collectedClue));
        }
        return grouped.entrySet().stream()
                .map(entry -> new ClueArchiveGroupDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional
    public CollectedClueDto updateCollectedClue(Long sessionId, Long clueId, UpdateCollectedClueRequest request) {
        GameSession session = requireSession(sessionId);
        caseClueRepository.findByIdAndCaseFileId(clueId, session.getCaseFile().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clue not found"));
        CollectedClue collectedClue = collectedClueRepository.findBySessionIdAndClueId(sessionId, clueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collected clue not found"));
        collectedClue.update(request.importance(), request.status());
        return CollectedClueDto.from(collectedClue);
    }

    private GameSession requireSession(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
    }
}