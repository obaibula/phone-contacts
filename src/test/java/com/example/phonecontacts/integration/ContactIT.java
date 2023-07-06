package com.example.phonecontacts.integration;

import com.example.phonecontacts.config.JwtService;
import com.example.phonecontacts.config.TestDatabaseContainerConfig;
import com.example.phonecontacts.contact.Contact;
import com.example.phonecontacts.contact.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.github.javafaker.Faker;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestDatabaseContainerConfig.class)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class ContactIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private final Faker faker = new Faker();



    @BeforeEach
    void setUp(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void canCreateNewContact() throws Exception {
        // given
        String name = faker.name().name();
        var contact = new Contact();
        contact.setName(name);
        // when
        var resultActions = mockMvc
                .perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                        .content(objectMapper.writeValueAsString(contact)));
        // then

        resultActions.andExpect(status().isCreated());
        var contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(3);
    }

    private String generateTestToken() {
        // by registered user "oleh123"
        return jwtService
                .generateToken(userDetailsService
                        .loadUserByUsername("oleh123"));
    }
}
