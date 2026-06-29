package com.rumortown;

import com.rumortown.casefile.CaseService;
import com.rumortown.clue.ClueService;
import com.rumortown.game.GameSessionService;
import com.rumortown.npc.NpcDialogueService;
import com.rumortown.report.TruthReportService;
import com.rumortown.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(
        classes = RumorTownApplication.class,
        properties = {
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
        }
)
class RumorTownApplicationTests {

    @MockBean
    private CaseService caseService;

    @MockBean
    private UserService userService;

    @MockBean
    private GameSessionService gameSessionService;

    @MockBean
    private NpcDialogueService npcDialogueService;

    @MockBean
    private ClueService clueService;

    @MockBean
    private TruthReportService truthReportService;

    @Test
    void contextLoads() {
    }
}