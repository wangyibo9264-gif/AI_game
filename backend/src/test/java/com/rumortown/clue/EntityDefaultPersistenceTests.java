package com.rumortown.clue;

import static org.assertj.core.api.Assertions.assertThat;

import com.rumortown.casefile.CaseFile;
import com.rumortown.casefile.CaseFileRepository;
import com.rumortown.casefile.CaseStatus;
import com.rumortown.game.GameSession;
import com.rumortown.game.GameSessionRepository;
import com.rumortown.game.SessionStatus;
import com.rumortown.user.User;
import com.rumortown.user.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rumortown-defaults;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class EntityDefaultPersistenceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaseFileRepository caseFileRepository;

    @Autowired
    private CaseClueRepository caseClueRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private CollectedClueRepository collectedClueRepository;

    @Test
    void savingMinimalEntitiesUsesJavaSideDefaults() {
        LocalDateTime now = LocalDateTime.now();

        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "displayName", "Guest");
        ReflectionTestUtils.setField(user, "createdAt", now);
        userRepository.saveAndFlush(user);

        CaseFile caseFile = instantiate(CaseFile.class);
        ReflectionTestUtils.setField(caseFile, "code", "case-defaults");
        ReflectionTestUtils.setField(caseFile, "title", "Defaults Case");
        ReflectionTestUtils.setField(caseFile, "status", CaseStatus.DRAFT);
        caseFileRepository.saveAndFlush(caseFile);

        CaseClue clue = instantiate(CaseClue.class);
        ReflectionTestUtils.setField(clue, "caseFile", caseFile);
        ReflectionTestUtils.setField(clue, "clueCode", "clue-defaults");
        ReflectionTestUtils.setField(clue, "title", "Defaults Clue");
        ReflectionTestUtils.setField(clue, "content", "Default content");
        ReflectionTestUtils.setField(clue, "category", ClueCategory.CONTEXT);
        ReflectionTestUtils.setField(clue, "unlockStage", 1);
        caseClueRepository.saveAndFlush(clue);

        GameSession session = instantiate(GameSession.class);
        ReflectionTestUtils.setField(session, "user", user);
        ReflectionTestUtils.setField(session, "caseFile", caseFile);
        ReflectionTestUtils.setField(session, "status", SessionStatus.ACTIVE);
        ReflectionTestUtils.setField(session, "createdAt", now);
        ReflectionTestUtils.setField(session, "updatedAt", now);
        gameSessionRepository.saveAndFlush(session);

        CollectedClue collectedClue = instantiate(CollectedClue.class);
        ReflectionTestUtils.setField(collectedClue, "session", session);
        ReflectionTestUtils.setField(collectedClue, "clue", clue);
        ReflectionTestUtils.setField(collectedClue, "collectedAt", now);
        collectedClueRepository.saveAndFlush(collectedClue);

        assertThat(ReflectionTestUtils.getField(user, "guest")).isEqualTo(true);
        assertThat(ReflectionTestUtils.getField(clue, "critical")).isEqualTo(false);
        assertThat(ReflectionTestUtils.getField(session, "currentStage")).isEqualTo(1);
        assertThat(ReflectionTestUtils.getField(collectedClue, "importance")).isEqualTo(ClueImportance.NORMAL);
        assertThat(ReflectionTestUtils.getField(collectedClue, "status")).isEqualTo(CollectedClueStatus.UNRESOLVED);
    }

    private static <T> T instantiate(Class<T> entityClass) {
        return BeanUtils.instantiateClass(entityClass);
    }
}
