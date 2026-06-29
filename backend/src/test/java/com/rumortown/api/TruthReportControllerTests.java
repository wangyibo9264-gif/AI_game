package com.rumortown.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rumortown.RumorTownApplication;
import com.rumortown.user.User;
import com.rumortown.user.UserRepository;
import java.time.LocalDateTime;
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
        "spring.datasource.url=jdbc:h2:mem:rumortown-reports;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class TruthReportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void submittingReportAtStageFourCreatesReportAndScore() throws Exception {
        Long caseId = caseId("CLOCK_317");
        Long sessionId = createSession(caseId);
        jdbcTemplate.update("update game_sessions set current_stage = 4 where id = ?", sessionId);
        jdbcTemplate.update("insert into collected_clues (session_id, clue_id, importance, status, collected_at) select ?, id, 'CRITICAL', 'RESOLVED', CURRENT_TIMESTAMP from case_clues where case_id = ? and critical = true limit 3", sessionId, caseId);

        mockMvc.perform(post("/api/sessions/{sessionId}/reports", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventSummary":"镇长被约到钟楼，三点十七分的钟声被手动触发，红手套和旧矿难赔偿表串起了动机。",
                                  "responsiblePerson":"与旧矿难赔偿有关的人",
                                  "keyEvidence":"三点十七分钟声、沾煤灰的红手套、女儿便签",
                                  "ruleExplanation":"钟楼规则真正指向人为触发钟声，不是怪谈本身。",
                                  "npcLies":"秘书隐瞒了留言，物资管理员隐瞒手套领用册缺页。",
                                  "conclusion":"怪谈掩盖了人为复仇。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reportId").isNumber())
                .andExpect(jsonPath("$.data.truthScore").isNumber())
                .andExpect(jsonPath("$.data.clueScore").isNumber())
                .andExpect(jsonPath("$.data.ruleScore").isNumber())
                .andExpect(jsonPath("$.data.ending").isString())
                .andExpect(jsonPath("$.data.summary").isString());

        Integer reportCount = jdbcTemplate.queryForObject("select count(*) from truth_reports where session_id = ?", Integer.class, sessionId);
        Integer scoreCount = jdbcTemplate.queryForObject("select count(*) from report_scores rs join truth_reports tr on tr.id = rs.report_id where tr.session_id = ?", Integer.class, sessionId);
        org.assertj.core.api.Assertions.assertThat(reportCount).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(scoreCount).isEqualTo(1);
    }

    @Test
    void submittingReportBeforeStageFourReturnsBadRequest() throws Exception {
        Long sessionId = createSession(caseId("CLOCK_317"));

        mockMvc.perform(post("/api/sessions/{sessionId}/reports", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventSummary\":\"还没查完\"}"))
                .andExpect(status().isBadRequest());
    }

    private Long createSession(Long caseId) throws Exception {
        User user = userRepository.save(User.guest("report-guest-" + System.nanoTime(), LocalDateTime.now()));
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
}