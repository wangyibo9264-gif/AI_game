package com.rumortown.api;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rumortown.RumorTownApplication;
import com.rumortown.user.User;
import com.rumortown.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(classes = RumorTownApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rumortown-sessions;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class GameSessionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createSessionStartsAtStageOneWithUnlockedLocationsAndNpcs() throws Exception {
        Long userId = createGuestUser();
        Long caseId = caseId("CLOCK_317");

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + userId + ",\"caseId\":" + caseId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.caseId").value(caseId))
                .andExpect(jsonPath("$.data.currentStage").value(1))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.availableLocations[*].code", hasItem("CLOCK_TOWER")))
                .andExpect(jsonPath("$.data.availableLocations[*].code", hasItem("TOWN_HALL")))
                .andExpect(jsonPath("$.data.availableLocations[*].code", not(hasItem("MINE_GATE"))))
                .andExpect(jsonPath("$.data.availableLocations[*].unlockStage", everyItem(org.hamcrest.Matchers.lessThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.availableNpcs[*].code", hasItem("CLOCK_WATCHMAN")))
                .andExpect(jsonPath("$.data.availableNpcs[*].code", hasItem("CLOCK_SECRETARY")))
                .andExpect(jsonPath("$.data.availableNpcs[*].code", not(hasItem("CLOCK_MINER"))))
                .andExpect(jsonPath("$.data.availableNpcs[*].unlockStage", everyItem(org.hamcrest.Matchers.lessThanOrEqualTo(1))));
    }

    @Test
    void visitingStageLockedLocationReturnsBadRequest() throws Exception {
        Long sessionId = createSession(caseId("CLOCK_317"));
        Long lockedLocationId = locationId("CLOCK_317", "MINE_GATE");

        mockMvc.perform(post("/api/sessions/{sessionId}/locations/{locationId}/visit", sessionId, lockedLocationId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void advanceMovesStageForwardAfterEnoughCollectedCriticalClues() throws Exception {
        Long caseId = caseId("CLOCK_317");
        Long sessionId = createSession(caseId);
        List<Long> clueIds = jdbcTemplate.queryForList(
                "select id from case_clues where case_id = ? order by id limit 3",
                Long.class,
                caseId);
        for (Long clueId : clueIds) {
            jdbcTemplate.update(
                    "insert into collected_clues (session_id, clue_id, importance, status, collected_at) values (?, ?, 'NORMAL', 'UNRESOLVED', CURRENT_TIMESTAMP)",
                    sessionId,
                    clueId);
        }

        mockMvc.perform(post("/api/sessions/{sessionId}/advance", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentStage").value(2))
                .andExpect(jsonPath("$.data.availableLocations[*].code", hasItem("MINE_GATE")))
                .andExpect(jsonPath("$.data.availableLocations[*].code", not(hasItem("BELL_ARCHIVE"))));
    }

    private Long createSession(Long caseId) throws Exception {
        Long userId = createGuestUser();
        MvcResult result = mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + userId + ",\"caseId\":" + caseId + "}"))
                .andExpect(status().isOk())
                .andReturn();
        Number id = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        return id.longValue();
    }

    private Long createGuestUser() {
        User user = userRepository.save(User.guest("session-guest-" + System.nanoTime(), LocalDateTime.now()));
        return user.getId();
    }

    private Long caseId(String code) {
        return jdbcTemplate.queryForObject("select id from cases where code = ?", Long.class, code);
    }

    private Long locationId(String caseCode, String locationCode) {
        return jdbcTemplate.queryForObject(
                "select l.id from case_locations l join cases c on c.id = l.case_id where c.code = ? and l.code = ?",
                Long.class,
                caseCode,
                locationCode);
    }
}