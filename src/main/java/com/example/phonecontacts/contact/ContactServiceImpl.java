package com.example.phonecontacts.contact;

import com.example.phonecontacts.email.Email;
import com.example.phonecontacts.exception.ContactNotFoundException;
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
        // for maximum simplicity, I have come up with this solution. (The same is for update method)
        Set<Email> emails = contact.getEmails();
        if (emails != null) {
            emails.forEach(email -> email.setContact(contact));
        }
        Set<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null) {
            phoneNumbers.forEach(phoneNumber -> phoneNumber.setContact(contact));
        }

        // Set USER
        contact.setUser(user);
        user.addContact(contact);

        return contactRepository.save(contact);
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
    public void deleteByName(String name) {
        contactRepository.findByName(name)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found with name - " + name));
        contactRepository.deleteInBulkByName(name);
    }

    @Override
    @Transactional
    public Contact update(Long contactId, Contact contact, Principal principal) {
        String name = contact.getName();
        // Get existing contact form the db
        var existingContact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Not found contact with name - " + name));

        // Validate ownership of the contact before updating
        if (!existingContact.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized"); // todo
        }

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

        return contactRepository.save(existingContact);

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
