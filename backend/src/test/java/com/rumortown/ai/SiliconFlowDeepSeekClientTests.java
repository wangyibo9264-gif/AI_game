package com.rumortown.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class SiliconFlowDeepSeekClientTests {

    private final SiliconFlowDeepSeekClient client = new SiliconFlowDeepSeekClient(
            WebClient.builder(),
            new ObjectMapper(),
            "",
            "https://example.invalid/v1",
            "deepseek-ai/DeepSeek-V4-Pro");

    @Test
    void parsesNpcDialogueJsonFromChatCompletionContent() throws Exception {
        String content = """
                {"reply":"钟声不是自己响的。","mood":"紧张","revealedClueCodes":["CLOCK_TIME_0317_BELL"],"ruleHints":["别在三点十七分回头"],"suspicionDelta":2}
                """;
        String body = new ObjectMapper().writeValueAsString(java.util.Map.of(
                "choices", java.util.List.of(java.util.Map.of(
                        "message", java.util.Map.of("content", content)))));

        NpcDialogueAiResponse response = client.parseNpcDialogueResponse(body);

        assertThat(response.reply()).isEqualTo("钟声不是自己响的。");
        assertThat(response.mood()).isEqualTo("紧张");
        assertThat(response.revealedClueCodes()).containsExactly("CLOCK_TIME_0317_BELL");
        assertThat(response.ruleHints()).containsExactly("别在三点十七分回头");
        assertThat(response.suspicionDelta()).isEqualTo(2);
    }

    @Test
    void invalidNpcDialogueJsonFallsBackToSafeReplyWithoutClues() {
        String body = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "不是合法 JSON"
                      }
                    }
                  ]
                }
                """;

        NpcDialogueAiResponse response = client.parseNpcDialogueResponse(body);

        assertThat(response.reply()).isNotBlank();
        assertThat(response.revealedClueCodes()).isEmpty();
        assertThat(response.ruleHints()).isEmpty();
        assertThat(response.suspicionDelta()).isZero();
    }

    @Test
    void missingApiKeyUsesDeterministicMockReplyWithoutClues() {
        NpcDialogueAiResponse response = client.generateNpcReply(new NpcDialogueAiRequest(
                "CLOCK_317",
                "CLOCK_WATCHMAN",
                "梁守钟",
                "守钟人",
                1,
                "三点十七分发生了什么？",
                "守钟人知道钟声不是自动报时。",
                java.util.List.of("CLOCK_PERSON_SECRETARY_ALIBI"),
                java.util.List.of("CLOCK_TIME_0317_BELL"),
                java.util.List.of("三点十七分后不要回头")));

        assertThat(response.reply()).contains("梁守钟");
        assertThat(response.revealedClueCodes()).isEmpty();
        assertThat(response.ruleHints()).isEmpty();
    }
}