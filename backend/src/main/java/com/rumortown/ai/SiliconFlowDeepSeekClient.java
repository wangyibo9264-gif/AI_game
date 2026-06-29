package com.rumortown.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SiliconFlowDeepSeekClient implements AiNarrativeClient {
    private static final String NPC_SYSTEM_PROMPT = """
            你是《谣镇档案：怪谈调查局》的 NPC 叙事助手。案件真相、线索、规则均由服务器固定。
            只能根据请求中 allowedRevealClueCodes 返回可揭示线索编号，不得编造新线索、不得修改真相。
            仅输出 JSON：reply, mood, revealedClueCodes, ruleHints, suspicionDelta。
            """;

    private static final String REPORT_SYSTEM_PROMPT = """
            你是《谣镇档案：怪谈调查局》的结案报告文字润色助手。
            只能评价玩家报告表达，不得改变服务器固定答案。仅输出 JSON：summary, missedPoints, endingSuggestion。
            """;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public SiliconFlowDeepSeekClient(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${siliconflow.api-key:}") String apiKey,
            @Value("${siliconflow.base-url:https://api.siliconflow.cn/v1}") String baseUrl,
            @Value("${siliconflow.model:deepseek-ai/DeepSeek-V4-Pro}") String model
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
    }

    @Override
    public NpcDialogueAiResponse generateNpcReply(NpcDialogueAiRequest request) {
        if (apiKey.isBlank()) {
            return mockNpcReply(request);
        }
        try {
            String body = postChatCompletion(NPC_SYSTEM_PROMPT, buildNpcUserPrompt(request));
            return parseNpcDialogueResponse(body);
        } catch (RuntimeException exception) {
            return NpcDialogueAiResponse.safe("对方像是听见了什么远处的响动，暂时避开了这个问题。");
        }
    }

    @Override
    public ReportReviewAiResponse reviewTruthReport(ReportReviewAiRequest request) {
        if (apiKey.isBlank()) {
            return ReportReviewAiResponse.safe();
        }
        try {
            String body = postChatCompletion(REPORT_SYSTEM_PROMPT, buildReportUserPrompt(request));
            return parseReportReviewResponse(body);
        } catch (RuntimeException exception) {
            return ReportReviewAiResponse.safe();
        }
    }

    NpcDialogueAiResponse parseNpcDialogueResponse(String responseBody) {
        try {
            String content = extractMessageContent(responseBody);
            String json = extractJsonObject(content);
            return objectMapper.readValue(json, NpcDialogueAiResponse.class).sanitized();
        } catch (RuntimeException | JsonProcessingException exception) {
            return NpcDialogueAiResponse.safe("对方压低声音说：这件事不能只听传闻。你还需要更多证据。");
        }
    }

    ReportReviewAiResponse parseReportReviewResponse(String responseBody) {
        try {
            String content = extractMessageContent(responseBody);
            String json = extractJsonObject(content);
            return objectMapper.readValue(json, ReportReviewAiResponse.class).sanitized();
        } catch (RuntimeException | JsonProcessingException exception) {
            return ReportReviewAiResponse.safe();
        }
    }

    private String postChatCompletion(String systemPrompt, String userPrompt) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)),
                "temperature", 0.6,
                "response_format", Map.of("type", "json_object"));

        return webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private NpcDialogueAiResponse mockNpcReply(NpcDialogueAiRequest request) {
        String npcName = request.npcName() == null || request.npcName().isBlank() ? "对方" : request.npcName();
        return NpcDialogueAiResponse.safe(npcName + "盯着你看了一会儿：先把能核实的线索记下来，别让镇上的传闻牵着走。");
    }

    private String buildNpcUserPrompt(NpcDialogueAiRequest request) {
        return """
                caseCode: %s
                npcCode: %s
                npcName: %s
                roleName: %s
                currentStage: %d
                playerQuestion: %s
                knownFacts: %s
                collectedClueCodes: %s
                allowedRevealClueCodes: %s
                rules: %s
                """.formatted(
                request.caseCode(),
                request.npcCode(),
                request.npcName(),
                request.roleName(),
                request.currentStage(),
                request.playerQuestion(),
                request.knownFacts(),
                request.collectedClueCodes(),
                request.allowedRevealClueCodes(),
                request.rules());
    }

    private String buildReportUserPrompt(ReportReviewAiRequest request) {
        return """
                caseCode: %s
                eventSummary: %s
                responsiblePerson: %s
                keyEvidence: %s
                ruleExplanation: %s
                npcLies: %s
                conclusion: %s
                collectedClueCodes: %s
                canonicalAnswerPoints: %s
                """.formatted(
                request.caseCode(),
                request.eventSummary(),
                request.responsiblePerson(),
                request.keyEvidence(),
                request.ruleExplanation(),
                request.npcLies(),
                request.conclusion(),
                request.collectedClueCodes(),
                request.canonicalAnswerPoints());
    }

    private String extractMessageContent(String responseBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.at("/choices/0/message/content").asText();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("AI response content is blank");
        }
        return content;
    }

    private String extractJsonObject(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "").trim();
        }
        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace < 0 || lastBrace < firstBrace) {
            throw new IllegalArgumentException("AI response does not contain a JSON object");
        }
        return trimmed.substring(firstBrace, lastBrace + 1);
    }
}