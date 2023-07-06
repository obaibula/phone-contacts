package com.example.phonecontacts.contact;

import com.example.phonecontacts.config.TestDatabaseContainerConfig;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;


import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestDatabaseContainerConfig.class)
@TestPropertySource("classpath:application-test.properties")
class ContactRepositoryTest {

    @Autowired
    private static PostgreSQLContainer<?> container;

    @Autowired
    private ContactRepository underTest;

    @BeforeEach
    void setUp(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @Transactional
    public void shouldDeleteInBulkByName(){
        // given
        String name = "Petro";
        assertThat(underTest.existsByName(name)).isTrue();

        // when
        underTest.deleteInBulkByName(name);

        // then
        assertThat(underTest.existsByName(name)).isFalse();
    }
}