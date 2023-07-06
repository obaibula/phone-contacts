package com.example.phonecontacts.contact;

import com.example.phonecontacts.email.Email;
import com.example.phonecontacts.phonenumber.PhoneNumber;
import com.example.phonecontacts.validation.PostInfo;
import com.example.phonecontacts.validation.PutInfo;
import com.example.phonecontacts.validation.UniqueName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ContactDto(
        @UniqueName(groups = PostInfo.class)
        @NotNull(message = "Invalid name : name must not be null", groups = {PostInfo.class, PutInfo.class})
        String name,
        @UniqueElements(message = "Duplicate emails found", groups = {PostInfo.class, PutInfo.class})
        List<@jakarta.validation.constraints.Email(message = "Invalid email: must be example@mail.com", groups = {PostInfo.class, PutInfo.class})
        @NotNull(message = "Invalid email: Email must not be null", groups = {PostInfo.class, PutInfo.class})
                String> emails,
        @UniqueElements(message = "Duplicate phoneNumbers found", groups = {PostInfo.class, PutInfo.class})
        List<@NotNull(message = "Invalid phoneNumber: phoneNumber must not be null", groups = {PostInfo.class, PutInfo.class})
        @Pattern(regexp = "^\\+38 \\d{3} \\d{3}-\\d{2}-\\d{2}$",
                message = "Invalid phoneNumber: The phone number should be in the next format: +38 050 123-45-67",
                groups = {PostInfo.class, PutInfo.class})
                String> phoneNumbers) {

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
        if(contactDto.phoneNumbers == null)
            return null;

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
        if(contactDto.emails == null)
            return null;

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
        if(contact.getPhoneNumbers() == null)
            return null;

        return contact.getPhoneNumbers()
                .stream()
                .map(PhoneNumber::getPhoneNumber)
                .collect(Collectors.toList());
    }

    private static List<String> getEmails(Contact contact) {
        if(contact.getEmails() == null)
            return null;

        return contact.getEmails()
                .stream()
                .map(Email::getEmail)
                .collect(Collectors.toList());
    }
}
