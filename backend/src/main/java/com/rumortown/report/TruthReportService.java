package com.rumortown.report;

import com.rumortown.ai.AiNarrativeClient;
import com.rumortown.ai.ReportReviewAiRequest;
import com.rumortown.ai.ReportReviewAiResponse;
import com.rumortown.casefile.CaseRule;
import com.rumortown.casefile.CaseRuleRepository;
import com.rumortown.clue.CaseClueRepository;
import com.rumortown.clue.CollectedClue;
import com.rumortown.clue.CollectedClueRepository;
import com.rumortown.game.GameSession;
import com.rumortown.game.GameSessionRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TruthReportService {
    private final GameSessionRepository gameSessionRepository;
    private final TruthReportRepository truthReportRepository;
    private final ReportScoreRepository reportScoreRepository;
    private final CaseRuleRepository caseRuleRepository;
    private final CaseClueRepository caseClueRepository;
    private final CollectedClueRepository collectedClueRepository;
    private final AiNarrativeClient aiNarrativeClient;

    public TruthReportService(
            GameSessionRepository gameSessionRepository,
            TruthReportRepository truthReportRepository,
            ReportScoreRepository reportScoreRepository,
            CaseRuleRepository caseRuleRepository,
            CaseClueRepository caseClueRepository,
            CollectedClueRepository collectedClueRepository,
            AiNarrativeClient aiNarrativeClient
    ) {
        this.gameSessionRepository = gameSessionRepository;
        this.truthReportRepository = truthReportRepository;
        this.reportScoreRepository = reportScoreRepository;
        this.caseRuleRepository = caseRuleRepository;
        this.caseClueRepository = caseClueRepository;
        this.collectedClueRepository = collectedClueRepository;
        this.aiNarrativeClient = aiNarrativeClient;
    }

    @Transactional
    public TruthReportResultDto submitReport(Long sessionId, SubmitTruthReportRequest request) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        if (session.getCurrentStage() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report can only be submitted at stage 4");
        }
        TruthReport report = truthReportRepository.save(TruthReport.submit(session, request, LocalDateTime.now()));
        List<String> answerPoints = caseRuleRepository.findByCaseFileIdOrderByDisplayOrderAsc(session.getCaseFile().getId())
                .stream()
                .map(CaseRule::getTruthMeaning)
                .filter(value -> value != null && !value.isBlank())
                .toList();
        List<CollectedClue> collectedClues = collectedClueRepository.findBySessionIdOrderByClueCategoryAscClueIdAsc(sessionId);

        int truthScore = scoreTextMatches(reportText(request), answerPoints, 40);
        int clueScore = scoreCollectedCriticalClues(session, collectedClues);
        int ruleScore = scoreTextMatches(nullToBlank(request.ruleExplanation()), answerPoints, 20);
        ReportReviewAiResponse review = aiNarrativeClient.reviewTruthReport(new ReportReviewAiRequest(
                session.getCaseFile().getCode(),
                request.eventSummary(),
                request.responsiblePerson(),
                request.keyEvidence(),
                request.ruleExplanation(),
                request.npcLies(),
                request.conclusion(),
                collectedClues.stream().map(collected -> collected.getClue().getClueCode()).toList(),
                answerPoints));
        String ending = chooseEnding(truthScore + clueScore + ruleScore, review.endingSuggestion());
        ReportScore score = reportScoreRepository.save(ReportScore.of(
                report,
                truthScore,
                clueScore,
                ruleScore,
                ending,
                review.summary(),
                String.join("\n", review.missedPoints())));
        return TruthReportResultDto.from(report, score);
    }

    private int scoreCollectedCriticalClues(GameSession session, List<CollectedClue> collectedClues) {
        long totalCritical = caseClueRepository.countByCaseFileIdAndCriticalTrue(session.getCaseFile().getId());
        if (totalCritical == 0) {
            return 40;
        }
        long collectedCritical = collectedClues.stream().filter(collected -> collected.getClue().isCritical()).count();
        return (int) Math.min(40, Math.round(collectedCritical * 40.0 / totalCritical));
    }

    private int scoreTextMatches(String text, List<String> answerPoints, int maxScore) {
        if (answerPoints.isEmpty()) {
            return maxScore;
        }
        long matched = answerPoints.stream().filter(answer -> hasKeywordOverlap(text, answer)).count();
        return (int) Math.min(maxScore, Math.round(matched * (double) maxScore / answerPoints.size()));
    }

    private boolean hasKeywordOverlap(String text, String answerPoint) {
        String normalizedText = normalize(text);
        return keywords(answerPoint).stream().anyMatch(normalizedText::contains);
    }

    private List<String> keywords(String value) {
        return Arrays.stream(normalize(value).split("[^\\p{IsHan}a-z0-9]+"))
                .filter(token -> token.length() >= 2)
                .collect(Collectors.toList());
    }

    private String reportText(SubmitTruthReportRequest request) {
        return String.join("\n",
                nullToBlank(request.eventSummary()),
                nullToBlank(request.responsiblePerson()),
                nullToBlank(request.keyEvidence()),
                nullToBlank(request.ruleExplanation()),
                nullToBlank(request.npcLies()),
                nullToBlank(request.conclusion()));
    }

    private String chooseEnding(int totalScore, String aiSuggestion) {
        if (totalScore >= 85) {
            return "真相照明";
        }
        if (totalScore >= 60) {
            return "疑云暂散";
        }
        if (aiSuggestion != null && !aiSuggestion.isBlank() && !"未定结局".equals(aiSuggestion)) {
            return aiSuggestion;
        }
        return "谣雾未散";
    }

    private String normalize(String value) {
        return nullToBlank(value).toLowerCase(Locale.ROOT);
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}