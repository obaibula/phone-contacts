package com.example.phonecontacts.contact;

import com.example.phonecontacts.email.Email;
import com.example.phonecontacts.phonenumber.PhoneNumber;

import java.util.List;
import java.util.stream.Collectors;

public record ContactDto(String name,
                         List<String> emails,
                         List<String> phoneNumbers) {
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

    private static List<PhoneNumber> getPhoneNumbers(ContactDto contactDto) {
        return contactDto.phoneNumbers
                .stream()
                .map(stringPhone -> {
                    var phone = new PhoneNumber();
                    phone.setPhoneNumber(stringPhone);
                    return phone;
                }).collect(Collectors.toList());
    }

    private static List<Email> getEmails(ContactDto contactDto) {
        return contactDto.emails
                .stream()
                .map(stringEmail -> {
                    var email = new Email();
                    email.setEmail(stringEmail);
                    return email;
                })
                .collect(Collectors.toList());
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
