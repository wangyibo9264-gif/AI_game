package com.rumortown.casefile;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rumortown;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class CaseRepositoryTests {

    @Autowired
    private CaseFileRepository caseFileRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findAllStartsWithSeededCases() {
        assertThat(caseFileRepository.findAll())
                .hasSize(3)
                .extracting("code")
                .containsExactlyInAnyOrder("CLOCK_317", "ROSE_ROOM_8", "FOG_LAST_FERRY");
    }

    @Test
    void seededCasesIncludeExpectedPlayableContent() {
        for (String caseCode : List.of("CLOCK_317", "ROSE_ROOM_8", "FOG_LAST_FERRY")) {
            assertThat(countForCase("case_rules", caseCode)).isEqualTo(3);
            assertThat(countForCase("case_locations", caseCode)).isEqualTo(4);
            assertThat(countForCase("case_npcs", caseCode)).isEqualTo(5);
            assertThat(countForCase("case_clues", caseCode)).isEqualTo(10);

            Integer knowledgeRows = jdbcTemplate.queryForObject("""
                    select count(*)
                    from npc_knowledge nk
                    join case_npcs n on n.id = nk.npc_id
                    join cases c on c.id = n.case_id
                    where c.code = ?
                    """, Integer.class, caseCode);
            assertThat(knowledgeRows).isEqualTo(5);

            Integer clueCategories = jdbcTemplate.queryForObject("""
                    select count(distinct clue.category)
                    from case_clues clue
                    join cases c on c.id = clue.case_id
                    where c.code = ?
                    """, Integer.class, caseCode);
            assertThat(clueCategories).isEqualTo(6);

            Integer criticalClues = jdbcTemplate.queryForObject("""
                    select count(*)
                    from case_clues clue
                    join cases c on c.id = clue.case_id
                    where c.code = ? and clue.critical = true
                    """, Integer.class, caseCode);
            assertThat(criticalClues).isBetween(3, 4);
        }
    }

    private int countForCase(String tableName, String caseCode) {
        return jdbcTemplate.queryForObject("""
                select count(*)
                from %s item
                join cases c on c.id = item.case_id
                where c.code = ?
                """.formatted(tableName), Integer.class, caseCode);
    }

    @Test
    void caseFileUsesPlannedFields() throws NoSuchFieldException {
        assertThat(CaseFile.class.getDeclaredField("code")).isNotNull();
        assertThat(CaseFile.class.getDeclaredField("estimatedMinutes")).isNotNull();
        assertThat(CaseFile.class.getDeclaredFields())
                .extracting(java.lang.reflect.Field::getName)
                .doesNotContain("createdBy");
    }
}