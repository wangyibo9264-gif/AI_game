package com.rumortown.api;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rumortown.RumorTownApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = RumorTownApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rumortown-api;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class GuestAndCaseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createGuestReturnsGuestPlayerAndPersistsUser() throws Exception {
        mockMvc.perform(post("/api/guest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.displayName", startsWith("guest-")))
                .andExpect(jsonPath("$.data.guest").value(true));

        Integer guestCount = jdbcTemplate.queryForObject(
                "select count(*) from users where guest = true and display_name like 'guest-%' and created_at is not null",
                Integer.class);
        org.assertj.core.api.Assertions.assertThat(guestCount).isEqualTo(1);
    }

    @Test
    void listCasesReturnsSeededCasesWithPlayerSafeFields() throws Exception {
        mockMvc.perform(get("/api/cases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[*].code", hasItem("CLOCK_317")))
                .andExpect(jsonPath("$.data[*].code", hasItem("ROSE_ROOM_8")))
                .andExpect(jsonPath("$.data[*].code", hasItem("FOG_LAST_FERRY")))
                .andExpect(jsonPath("$.data[*].id", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[*].title", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[*].summary", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[*].difficulty", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[*].estimatedMinutes", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[*].status", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data[*].rules").doesNotExist())
                .andExpect(jsonPath("$.data[*].truthMeaning").doesNotExist())
                .andExpect(jsonPath("$.data[*].npcKnowledge").doesNotExist())
                .andExpect(jsonPath("$.data[*].hiddenFacts").doesNotExist())
                .andExpect(jsonPath("$.data[*].forbiddenTopics").doesNotExist())
                .andExpect(jsonPath("$.data[*].canonicalTruth").doesNotExist())
                .andExpect(jsonPath("$.data[*].reportAnswers").doesNotExist());
    }

    @Test
    void caseDetailReturnsPlayerSafeRulesAndLocations() throws Exception {
        Long caseId = jdbcTemplate.queryForObject(
                "select id from cases where code = 'CLOCK_317'",
                Long.class);

        mockMvc.perform(get("/api/cases/{caseId}", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(caseId))
                .andExpect(jsonPath("$.data.code").value("CLOCK_317"))
                .andExpect(jsonPath("$.data.summary").isString())
                .andExpect(jsonPath("$.data.rules", hasSize(3)))
                .andExpect(jsonPath("$.data.rules[*].id", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.rules[*].ruleCode", hasItem("CLOCK_RULE_TIME")))
                .andExpect(jsonPath("$.data.rules[*].ruleText", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.rules[*].displayOrder", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.rules[*].truthMeaning").doesNotExist())
                .andExpect(jsonPath("$.data.locations", hasSize(4)))
                .andExpect(jsonPath("$.data.locations[*].id", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.locations[*].code", hasItem("CLOCK_TOWER")))
                .andExpect(jsonPath("$.data.locations[*].name", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.locations[*].description", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.locations[*].unlockStage", everyItem(org.hamcrest.Matchers.notNullValue())))
                .andExpect(jsonPath("$.data.truthMeaning").doesNotExist())
                .andExpect(jsonPath("$.data.npcKnowledge").doesNotExist())
                .andExpect(jsonPath("$.data.hiddenFacts").doesNotExist())
                .andExpect(jsonPath("$.data.forbiddenTopics").doesNotExist())
                .andExpect(jsonPath("$.data.canonicalTruth").doesNotExist())
                .andExpect(jsonPath("$.data.reportAnswers").doesNotExist());
    }
}
