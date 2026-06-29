package com.rumortown.npc;

import com.rumortown.ai.AiNarrativeClient;
import com.rumortown.ai.NpcDialogueAiRequest;
import com.rumortown.ai.NpcDialogueAiResponse;
import com.rumortown.casefile.CaseNpc;
import com.rumortown.casefile.CaseNpcRepository;
import com.rumortown.casefile.CaseRuleRepository;
import com.rumortown.casefile.NpcKnowledge;
import com.rumortown.casefile.NpcKnowledgeRepository;
import com.rumortown.clue.CaseClue;
import com.rumortown.clue.CaseClueRepository;
import com.rumortown.clue.CollectedClue;
import com.rumortown.clue.CollectedClueDto;
import com.rumortown.clue.CollectedClueRepository;
import com.rumortown.game.DialogueMessage;
import com.rumortown.game.DialogueMessageRepository;
import com.rumortown.game.DialogueSenderType;
import com.rumortown.game.GameSession;
import com.rumortown.game.GameSessionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NpcDialogueService {
    private final GameSessionRepository gameSessionRepository;
    private final CaseNpcRepository caseNpcRepository;
    private final NpcKnowledgeRepository npcKnowledgeRepository;
    private final CaseClueRepository caseClueRepository;
    private final CollectedClueRepository collectedClueRepository;
    private final DialogueMessageRepository dialogueMessageRepository;
    private final CaseRuleRepository caseRuleRepository;
    private final AiNarrativeClient aiNarrativeClient;

    public NpcDialogueService(
            GameSessionRepository gameSessionRepository,
            CaseNpcRepository caseNpcRepository,
            NpcKnowledgeRepository npcKnowledgeRepository,
            CaseClueRepository caseClueRepository,
            CollectedClueRepository collectedClueRepository,
            DialogueMessageRepository dialogueMessageRepository,
            CaseRuleRepository caseRuleRepository,
            AiNarrativeClient aiNarrativeClient
    ) {
        this.gameSessionRepository = gameSessionRepository;
        this.caseNpcRepository = caseNpcRepository;
        this.npcKnowledgeRepository = npcKnowledgeRepository;
        this.caseClueRepository = caseClueRepository;
        this.collectedClueRepository = collectedClueRepository;
        this.dialogueMessageRepository = dialogueMessageRepository;
        this.caseRuleRepository = caseRuleRepository;
        this.aiNarrativeClient = aiNarrativeClient;
    }

    @Transactional
    public NpcDialogueResponseDto sendMessage(Long sessionId, Long npcId, NpcMessageRequest request) {
        if (request.question() == null || request.question().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question is required");
        }
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        CaseNpc npc = caseNpcRepository.findById(npcId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NPC not found"));
        if (!npc.getCaseFile().getId().equals(session.getCaseFile().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NPC does not belong to this case");
        }
        if (npc.getUnlockStage() > session.getCurrentStage()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NPC is locked for current stage");
        }

        NpcKnowledge knowledge = npcKnowledgeRepository.findByNpcId(npcId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NPC knowledge not found"));
        LocalDateTime now = LocalDateTime.now();
        dialogueMessageRepository.save(DialogueMessage.of(session, npc, DialogueSenderType.PLAYER, request.question(), now));

        NpcDialogueAiResponse aiResponse = aiNarrativeClient.generateNpcReply(buildAiRequest(session, npc, knowledge, request.question()));
        List<CollectedClueDto> newlyCollectedClues = collectAllowedClues(session, knowledge, aiResponse.revealedClueCodes());
        dialogueMessageRepository.save(DialogueMessage.of(session, npc, DialogueSenderType.NPC, aiResponse.reply(), LocalDateTime.now()));

        return new NpcDialogueResponseDto(
                aiResponse.reply(),
                aiResponse.mood(),
                aiResponse.ruleHints(),
                aiResponse.suspicionDelta(),
                newlyCollectedClues);
    }

    private NpcDialogueAiRequest buildAiRequest(GameSession session, CaseNpc npc, NpcKnowledge knowledge, String question) {
        List<String> collectedCodes = collectedClueRepository.findBySessionIdOrderByClueCategoryAscClueIdAsc(session.getId())
                .stream()
                .map(collectedClue -> collectedClue.getClue().getClueCode())
                .toList();
        List<String> rules = caseRuleRepository.findByCaseFileIdOrderByDisplayOrderAsc(session.getCaseFile().getId())
                .stream()
                .map(rule -> rule.getRuleText())
                .toList();
        String guardedFacts = "knownFacts: " + nullToBlank(knowledge.getKnownFacts())
                + "\nhiddenFacts: " + nullToBlank(knowledge.getHiddenFacts())
                + "\nforbiddenTopics: " + nullToBlank(knowledge.getForbiddenTopics())
                + "\nnpcPersonality: " + nullToBlank(npc.getPersonality())
                + "\nspeakingStyle: " + nullToBlank(npc.getSpeakingStyle())
                + "\nlocation: " + npc.getLocation().getName();
        return new NpcDialogueAiRequest(
                session.getCaseFile().getCode(),
                npc.getCode(),
                npc.getName(),
                npc.getRoleName(),
                session.getCurrentStage(),
                question,
                guardedFacts,
                collectedCodes,
                splitCodes(knowledge.getRevealableClueCodes()),
                rules);
    }

    private List<CollectedClueDto> collectAllowedClues(GameSession session, NpcKnowledge knowledge, List<String> aiClueCodes) {
        if (aiClueCodes == null || aiClueCodes.isEmpty()) {
            return List.of();
        }
        Set<String> allowedCodes = new LinkedHashSet<>(splitCodes(knowledge.getRevealableClueCodes()));
        List<String> requestedCodes = aiClueCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        Map<String, CaseClue> cluesByCode = caseClueRepository
                .findByCaseFileIdAndClueCodeIn(session.getCaseFile().getId(), requestedCodes)
                .stream()
                .collect(Collectors.toMap(CaseClue::getClueCode, Function.identity()));

        List<CollectedClueDto> collected = new ArrayList<>();
        for (String code : requestedCodes) {
            CaseClue clue = cluesByCode.get(code);
            if (clue == null || !allowedCodes.contains(code)) {
                continue;
            }
            if (clue.getUnlockStage() > session.getCurrentStage()) {
                continue;
            }
            if (collectedClueRepository.existsBySessionIdAndClueId(session.getId(), clue.getId())) {
                continue;
            }
            CollectedClue saved = collectedClueRepository.save(CollectedClue.collect(session, clue, LocalDateTime.now()));
            collected.add(CollectedClueDto.from(saved));
        }
        return collected;
    }

    private List<String> splitCodes(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .toList();
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}