package com.example.phonecontacts.contact;

import com.example.phonecontacts.email.Email;
import com.example.phonecontacts.phonenumber.PhoneNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ContactDto(
        @NotNull(message = "Invalid name : name must not be null")
        String name,
        @UniqueElements(message = "Duplicate emails found")
        List<
                @jakarta.validation.constraints.Email(message = "Invalid email: must be example@mail.com")
                @NotNull(message = "Invalid email: Email must not be null")
                        String
                > emails,
        @UniqueElements(message = "Duplicate phoneNumbers found")
        List<
                @NotNull(message = "Invalid phoneNumber: phoneNumber must not be null")
                @Pattern(regexp = "^\\+38 \\d{3} \\d{3}-\\d{2}-\\d{2}$",
                        message = "Invalid phoneNumber: The phone number should be in the next format: +38 050 123-45-67")
                        String
                > phoneNumbers) {
    public static ContactDto contactToDto(Contact contact) {
        return new ContactDto(
                contact.getName(),
                getEmails(contact),
                getPhoneNumbers(contact));
    }

    public static Contact DtoToContact(ContactDto contactDto) {
        var contact = new Contact();
        contact.setName(contactDto.name);
        contact.setEmails(getEmails(contactDto));
        contact.setPhoneNumbers(getPhoneNumbers(contactDto));
        return contact;
    }

    private static Set<PhoneNumber> getPhoneNumbers(ContactDto contactDto) {
        return contactDto.phoneNumbers
                .stream()
                .map(getPhoneNumberFunction())
                .collect(Collectors.toSet());
    }

    private static Function<String, PhoneNumber> getPhoneNumberFunction() {
        return stringPhone -> {
            var phone = new PhoneNumber();
            phone.setPhoneNumber(stringPhone);
            return phone;
        };
    }

    private static Set<Email> getEmails(ContactDto contactDto) {
        return contactDto.emails
                .stream()
                .map(getEmailFunction())
                .collect(Collectors.toSet());
    }

    private static Function<String, Email> getEmailFunction() {
        return stringEmail -> {
            var email = new Email();
            email.setEmail(stringEmail);
            return email;
        };
    }

    private static List<String> getPhoneNumbers(Contact contact) {
        return contact.getPhoneNumbers()
                .stream()
                .map(PhoneNumber::getPhoneNumber)
                .collect(Collectors.toList());
    }

    private static List<String> getEmails(Contact contact) {
        return contact.getEmails()
                .stream()
                .map(Email::getEmail)
                .collect(Collectors.toList());
    }
}
