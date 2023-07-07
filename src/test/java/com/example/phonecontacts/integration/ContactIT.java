package com.example.phonecontacts.integration;

import com.example.phonecontacts.config.JwtService;
import com.example.phonecontacts.config.TestDatabaseContainerConfig;
import com.example.phonecontacts.contact.Contact;
import com.example.phonecontacts.contact.ContactRepository;
import com.example.phonecontacts.exception.ContactNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
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
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

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
    public void canCreateNewContact() throws Exception {
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

    @Test
    public void canDeleteContact() throws Exception {
        // given
        String name = faker.name().name();
        var contact = new Contact();
        contact.setName(name);

        mockMvc.perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isCreated());

        // when
        var resultActions = mockMvc
                .perform(delete("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                        .content(objectMapper.writeValueAsString(contact)));

        // then
        resultActions.andExpect(status().isNoContent());
        var exists = contactRepository.existsByName(name);
        assertThat(exists).isFalse();
    }

    @Test
    public void canGetTheListOfContacts() throws Exception {
        // when
        var getContactResult = mockMvc.perform(get("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken()))
                .andExpect(status().isOk())
                .andReturn();

        var contentAsString = getContactResult
                .getResponse()
                .getContentAsString();

        List<Contact> contacts = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );

        // then
        assertThat(contacts).hasSize(2);
    }

    @Test
    public void canUpdateContact() throws Exception {
        // given
        String oldName = faker.name().name();
        var contact = new Contact();
        contact.setName(oldName);

        mockMvc.perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isCreated());

        var id = contactRepository.findAll()
                .stream()
                .filter(c -> c.getName().equals(oldName))
                .map(Contact::getId)
                .findFirst()
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        System.err.println(id);

        var newName = faker.name().name();
        contact.setName(newName);

        // when
        var resultActions = mockMvc
                .perform(put("/api/v1/contacts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                        .content(objectMapper.writeValueAsString(contact)));

        // then

        resultActions.andExpect(status().isCreated());
        var existsByOldName = contactRepository.existsByName(oldName);
        assertThat(existsByOldName).isFalse();
        var existsByNewName = contactRepository.existsByName(newName);
        assertThat(existsByNewName).isTrue();

    }

    private String generateTestToken() {
        // by registered user "oleh123"
        return jwtService
                .generateToken(userDetailsService
                        .loadUserByUsername("oleh123"));
    }
}
