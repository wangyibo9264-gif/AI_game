package com.rumortown.casefile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    @Test
    void findAllStartsEmpty() {
        assertThat(caseFileRepository.findAll()).isEmpty();
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