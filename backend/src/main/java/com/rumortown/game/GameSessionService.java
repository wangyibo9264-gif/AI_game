package com.rumortown.game;

import com.rumortown.casefile.CaseFile;
import com.rumortown.casefile.CaseFileRepository;
import com.rumortown.casefile.CaseLocation;
import com.rumortown.casefile.CaseLocationRepository;
import com.rumortown.casefile.CaseNpcRepository;
import com.rumortown.clue.CollectedClueRepository;
import com.rumortown.user.User;
import com.rumortown.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final LocationVisitRepository locationVisitRepository;
    private final UserRepository userRepository;
    private final CaseFileRepository caseFileRepository;
    private final CaseLocationRepository caseLocationRepository;
    private final CaseNpcRepository caseNpcRepository;
    private final CollectedClueRepository collectedClueRepository;

    public GameSessionService(
            GameSessionRepository gameSessionRepository,
            LocationVisitRepository locationVisitRepository,
            UserRepository userRepository,
            CaseFileRepository caseFileRepository,
            CaseLocationRepository caseLocationRepository,
            CaseNpcRepository caseNpcRepository,
            CollectedClueRepository collectedClueRepository
    ) {
        this.gameSessionRepository = gameSessionRepository;
        this.locationVisitRepository = locationVisitRepository;
        this.userRepository = userRepository;
        this.caseFileRepository = caseFileRepository;
        this.caseLocationRepository = caseLocationRepository;
        this.caseNpcRepository = caseNpcRepository;
        this.collectedClueRepository = collectedClueRepository;
    }

    @Transactional
    public SessionDetailDto createSession(CreateSessionRequest request) {
        if (request.userId() == null || request.caseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and caseId are required");
        }
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        CaseFile caseFile = caseFileRepository.findById(request.caseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        GameSession session = gameSessionRepository.save(GameSession.start(user, caseFile, LocalDateTime.now()));
        return toDetail(session);
    }

    @Transactional(readOnly = true)
    public SessionDetailDto getSession(Long sessionId) {
        return toDetail(findSession(sessionId));
    }

    @Transactional
    public VisitLocationResponse visitLocation(Long sessionId, Long locationId) {
        GameSession session = findSession(sessionId);
        CaseLocation location = caseLocationRepository.findById(locationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
        if (!location.getCaseFile().getId().equals(session.getCaseFile().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location does not belong to this case");
        }
        if (location.getUnlockStage() > session.getCurrentStage()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is locked for current stage");
        }
        LocationVisit visit = locationVisitRepository.save(LocationVisit.record(session, location, LocalDateTime.now()));
        return VisitLocationResponse.from(visit);
    }

    @Transactional
    public SessionDetailDto advance(Long sessionId) {
        GameSession session = findSession(sessionId);
        int currentStage = session.getCurrentStage();
        if (currentStage >= 4) {
            return toDetail(session);
        }
        long collectedCount = collectedClueRepository.countBySessionId(sessionId);
        long criticalCount = collectedClueRepository.countCriticalBySessionId(sessionId);
        int nextStage = currentStage + 1;
        if (!meetsThreshold(nextStage, collectedCount, criticalCount)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough clues to advance");
        }
        session.advanceTo(nextStage, LocalDateTime.now());
        return toDetail(session);
    }

    private boolean meetsThreshold(int targetStage, long collectedCount, long criticalCount) {
        return switch (targetStage) {
            case 2 -> collectedCount >= 3 && criticalCount >= 1;
            case 3 -> collectedCount >= 6 && criticalCount >= 2;
            case 4 -> collectedCount >= 9 && criticalCount >= 3;
            default -> false;
        };
    }

    private GameSession findSession(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
    }

    private SessionDetailDto toDetail(GameSession session) {
        Long caseId = session.getCaseFile().getId();
        int stage = session.getCurrentStage();
        List<AvailableLocationDto> locations = caseLocationRepository
                .findByCaseFileIdAndUnlockStageLessThanEqualOrderByUnlockStageAscCodeAsc(caseId, stage)
                .stream()
                .map(AvailableLocationDto::from)
                .toList();
        List<AvailableNpcDto> npcs = caseNpcRepository
                .findByCaseFileIdAndUnlockStageLessThanEqualOrderByUnlockStageAscCodeAsc(caseId, stage)
                .stream()
                .map(AvailableNpcDto::from)
                .toList();
        return SessionDetailDto.from(session, locations, npcs);
    }
}