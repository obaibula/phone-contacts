package com.example.phonecontacts.contact;

import com.example.phonecontacts.validation.PostInfo;
import com.example.phonecontacts.validation.PutInfo;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
class ContactDtoTest {
    private Validator validator;

    @Mock
    ContactRepository contactRepository;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void nameShouldNotBeNull() {
        // given
        var dto = new ContactDto(
                null,
                List.of("email@mail.com"),
                List.of("+38 050 134-95-32"));

        // when
        var violations = validator.validate(dto, PutInfo.class);

        // then
        assertThat(violations.size()).isEqualTo(1);
        String errorMessage = violations.iterator().next().getMessage();
        assertThat(errorMessage).isEqualTo("Invalid name : name must not be null");
    }

    @Test
    public void emailsShouldBeUniqueInTheProvidedList(){
        // given
        var dto = new ContactDto(
                "name",
                List.of("email@mail.com", "email2@mail.com", "email2@mail.com"),
                null
        );

        // when
        var violations = validator.validate(dto, PutInfo.class);

        // then
        assertThat(violations.size()).isEqualTo(1);
        String errorMessage = violations.iterator().next().getMessage();
        assertThat(errorMessage).isEqualTo("Duplicate emails found");
    }

    @Test
    public void phoneNumbersShouldBeUniqueInTheProvidedList(){
        // given
        var dto = new ContactDto(
                "name",
                null,
                List.of("+38 050 134-95-32", "+38 050 134-95-32")
        );

        // when
        var violations = validator.validate(dto, PutInfo.class);

        // then
        assertThat(violations.size()).isEqualTo(1);
        String errorMessage = violations.iterator().next().getMessage();
        assertThat(errorMessage).isEqualTo("Duplicate phoneNumbers found");
    }

    @Test
    public void emailsShouldBeValid(){
        // given
        var dto = new ContactDto(
                "name",
                List.of("email@mail.com", "email2mail.com", "email2@mail.com"),
                null
        );

        // when
        var violations = validator.validate(dto, PutInfo.class);

        // then
        assertThat(violations.size()).isEqualTo(1);
        String errorMessage = violations.iterator().next().getMessage();
        assertThat(errorMessage).isEqualTo("Invalid email: must be example@mail.com");
    }

    @Test
    public void emailShouldNotBeNull(){
        // given
        List<String> listOfEmailsWithNull = new ArrayList<>();
        listOfEmailsWithNull.add(null);
        var dto = new ContactDto(
                "name",
                listOfEmailsWithNull,
                null
        );

        // when
        var violations = validator.validate(dto, PutInfo.class);

        // then
        assertThat(violations.size()).isEqualTo(1);
        String errorMessage = violations.iterator().next().getMessage();
        assertThat(errorMessage).isEqualTo("Invalid email: Email must not be null");
    }

    @Test
    public void phoneNumbersShouldNotBeNull(){
        // given
        List<String> listOfPhoneNUmbersWithNull = new ArrayList<>();
        listOfPhoneNUmbersWithNull.add(null);
        var dto = new ContactDto(
                "name",
                null,
                listOfPhoneNUmbersWithNull
        );

        // when
        var violations = validator.validate(dto, PutInfo.class);

        // then
        assertThat(violations.size()).isEqualTo(1);
        String errorMessage = violations.iterator().next().getMessage();
        assertThat(errorMessage).isEqualTo("Invalid phoneNumber: phoneNumber must not be null");
    }

    @Test
    public void phoneNumbersShouldBeValid(){
        // given
        var dto = new ContactDto(
                "name",
                null,
                List.of("+38 050 134-95-32", "+38abc")
        );

        // when
        var violations = validator.validate(dto, PutInfo.class);

        // then
        assertThat(violations.size()).isEqualTo(1);
        String errorMessage = violations.iterator().next().getMessage();
        assertThat(errorMessage).isEqualTo("Invalid phoneNumber: The phone number should be in the next format: +38 050 123-45-67");
    }

}