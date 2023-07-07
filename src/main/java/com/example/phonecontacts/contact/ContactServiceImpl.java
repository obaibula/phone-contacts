package com.example.phonecontacts.contact;

import com.example.phonecontacts.email.Email;
import com.example.phonecontacts.exception.ContactNotFoundException;
import com.example.phonecontacts.exception.UniqueContactException;
import com.example.phonecontacts.exception.UserNotAuthorizedException;
import com.example.phonecontacts.phonenumber.PhoneNumber;
import com.example.phonecontacts.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public Contact save(Contact contact, Principal principal) {
        // Fetch the appropriate USER
        var username = principal.getName();
        var user = (User) userDetailsService.loadUserByUsername(username);

        // Set the contact for all emails and phone numbers, as we are passing a list of strings.
        // During the creation of a Contact in the database,
        // there will be multiple INSERT statements executed.
        // This should not impact the performance in our case
        // since people typically have only 1-2 phone numbers and emails.
        // Considering the time constraints and
        // for the sake of core functionality, I have come up with this solution.
        // (The same applies to the update method)
        setContactToAllEmails(contact);
        setContactToAllPhoneNumbers(contact);

        // Set USER
        contact.setUser(user);
        user.addContact(contact);

        return contactRepository.save(contact);
    }

    private void setContactToAllPhoneNumbers(Contact contact) {
        Set<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null) {
            phoneNumbers.forEach(phoneNumber -> phoneNumber.setContact(contact));
        }
    }

    private void setContactToAllEmails(Contact contact) {
        Set<Email> emails = contact.getEmails();
        if (emails != null) {
            emails.forEach(email -> email.setContact(contact));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> findAll(Principal principal) {
        // Fetch the USER to show only owned entities.
        var username = principal.getName();
        var user = (User) userDetailsService.loadUserByUsername(username);

        return contactRepository.findAllByUserId(user.getId())
                .stream()
                .map(ContactDto::contactToDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByName(String name, Principal principal) {
        var existingContact = contactRepository.findByName(name)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found with name - " + name));
        validateOwnership(principal, existingContact);
        contactRepository.deleteInBulkByName(name);
    }

    @Override
    @Transactional
    public Contact update(Long contactId, Contact contact, Principal principal) {
        String name = contact.getName();
        // Get existing contact form the db
        var existingContact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Not found contact with name - " + name));


        validateOwnership(principal, existingContact);
        // Please note that due to the limited timeframe of 3 days for completing the test task,
        // I implemented the validation in a functional but suboptimal manner..
        checkIfContactIsPresent(contactId, name);


        updateAppropriateFields(contact, name, existingContact);

        return contactRepository.save(existingContact);

    }

    private void checkIfContactIsPresent(Long contactId, String name) {
        var contactIsPresentExcludingCurrentId = contactRepository.findByName(name)
                .stream()
                .filter(c -> !c.getId().equals(contactId))
                .anyMatch(c -> c.getName().equals(name));

        if(contactIsPresentExcludingCurrentId){
            throw new UniqueContactException("The contact is already present");
        }
    }

    private void validateOwnership(Principal principal, Contact existingContact) {
        if (!existingContact.getUser().getUsername().equals(principal.getName())) {
            throw new UserNotAuthorizedException("Access denied"); // todo
        }
    }

    private void updateAppropriateFields(Contact contact, String name, Contact existingContact) {
        // Update appropriate fields
        existingContact.setName(name);

        // Remove all existing emails and phone numbers, as required in the task(!).
        existingContact.removeAllEmails();
        existingContact.removeAllPhoneNumbers();

        // Update emails
        if (contact.getEmails() != null) {
            updateEmails(contact, existingContact);
        }

        // Update phoneNumbers
        if (contact.getPhoneNumbers() != null) {
            updatePhoneNumbers(contact, existingContact);
        }
    }

    private void updateEmails(Contact contact, Contact existingContact) {
        contact.getEmails()
                .forEach(email -> {
                    email.setContact(existingContact);
                    existingContact.addEmail(email);
                });
    }

    private void updatePhoneNumbers(Contact contact, Contact existingContact) {
        contact.getPhoneNumbers()
                .forEach(phoneNumber -> {
                    phoneNumber.setContact(existingContact);
                    existingContact.addPhoneNumber(phoneNumber);
                });
    }

}
