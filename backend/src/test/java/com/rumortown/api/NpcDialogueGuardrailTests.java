package com.rumortown.api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rumortown.RumorTownApplication;
import com.rumortown.ai.AiNarrativeClient;
import com.rumortown.ai.NpcDialogueAiResponse;
import com.rumortown.user.User;
import com.rumortown.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(classes = RumorTownApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rumortown-npc;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class NpcDialogueGuardrailTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AiNarrativeClient aiNarrativeClient;

    @Test
    void dialogueCollectsOnlyExistingAuthorizedUnlockedClues() throws Exception {
        Long caseId = caseId("CLOCK_317");
        Long sessionId = createSession(caseId);
        Long npcId = npcId("CLOCK_317", "CLOCK_WATCHMAN");
        when(aiNarrativeClient.generateNpcReply(any())).thenReturn(new NpcDialogueAiResponse(
                "钟声不是自己响的。",
                "紧张",
                List.of(
                        "CLOCK_TIME_0317_BELL",
                        "CLOCK_LOCATION_ARCHIVE_DUST",
                        "CLOCK_EVIDENCE_RED_GLOVE",
                        "NOT_A_REAL_CLUE"),
                List.of("三点十七分后不要回头"),
                2));

        mockMvc.perform(post("/api/sessions/{sessionId}/npcs/{npcId}/messages", sessionId, npcId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"三点十七分的钟声怎么回事？\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reply").value("钟声不是自己响的。"))
                .andExpect(jsonPath("$.data.newlyCollectedClues", hasSize(1)))
                .andExpect(jsonPath("$.data.newlyCollectedClues[0].clueCode").value("CLOCK_TIME_0317_BELL"));

        Integer collectedCount = jdbcTemplate.queryForObject(
                "select count(*) from collected_clues where session_id = ?",
                Integer.class,
                sessionId);
        Integer allowedCount = jdbcTemplate.queryForObject(
                "select count(*) from collected_clues cc join case_clues c on c.id = cc.clue_id where cc.session_id = ? and c.clue_code = 'CLOCK_TIME_0317_BELL'",
                Integer.class,
                sessionId);
        org.assertj.core.api.Assertions.assertThat(collectedCount).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(allowedCount).isEqualTo(1);
    }

    @Test
    void clueArchiveListsAndUpdatesCollectedClues() throws Exception {
        Long caseId = caseId("CLOCK_317");
        Long sessionId = createSession(caseId);
        Long npcId = npcId("CLOCK_317", "CLOCK_WATCHMAN");
        Long clueId = clueId("CLOCK_317", "CLOCK_TIME_0317_BELL");
        when(aiNarrativeClient.generateNpcReply(any())).thenReturn(new NpcDialogueAiResponse(
                "去查钟楼记录。",
                "谨慎",
                List.of("CLOCK_TIME_0317_BELL"),
                List.of(),
                0));
        mockMvc.perform(post("/api/sessions/{sessionId}/npcs/{npcId}/messages", sessionId, npcId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"我该查哪里？\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/sessions/{sessionId}/clues", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].category", hasItem("TIME")))
                .andExpect(jsonPath("$.data[*].clues[*].clueCode", hasItem("CLOCK_TIME_0317_BELL")));

        mockMvc.perform(patch("/api/sessions/{sessionId}/clues/{clueId}", sessionId, clueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"importance\":\"IMPORTANT\",\"status\":\"RESOLVED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.importance").value("IMPORTANT"))
                .andExpect(jsonPath("$.data.status").value("RESOLVED"));
    }

    private Long createSession(Long caseId) throws Exception {
        User user = userRepository.save(User.guest("npc-guest-" + System.nanoTime(), LocalDateTime.now()));
        MvcResult result = mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + user.getId() + ",\"caseId\":" + caseId + "}"))
                .andExpect(status().isOk())
                .andReturn();
        Number id = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        return id.longValue();
    }

    private Long caseId(String code) {
        return jdbcTemplate.queryForObject("select id from cases where code = ?", Long.class, code);
    }

    private Long npcId(String caseCode, String npcCode) {
        return jdbcTemplate.queryForObject(
                "select n.id from case_npcs n join cases c on c.id = n.case_id where c.code = ? and n.code = ?",
                Long.class,
                caseCode,
                npcCode);
    }

    private Long clueId(String caseCode, String clueCode) {
        return jdbcTemplate.queryForObject(
                "select cl.id from case_clues cl join cases c on c.id = cl.case_id where c.code = ? and cl.clue_code = ?",
                Long.class,
                caseCode,
                clueCode);
    }
}