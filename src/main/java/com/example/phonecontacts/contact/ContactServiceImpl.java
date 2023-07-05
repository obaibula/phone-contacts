package com.example.phonecontacts.contact;

import com.example.phonecontacts.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final UserDetailsService userDetailsService;
    @Override
    public Contact save(Contact contact, Principal principal) {
        var username = principal.getName();
        var user = (User) userDetailsService.loadUserByUsername(username);

        contact.getEmails().forEach(email -> email.setContact(contact));
        contact.getPhoneNumbers().forEach(phoneNumber -> phoneNumber.setContact(contact));

        contact.setUser(user);
        user.addContact(contact);

        return contactRepository.save(contact);
    }

    @Override
    public List<ContactDto> findAll(Principal principal) {
        var username = principal.getName();
        var user = (User)userDetailsService.loadUserByUsername(username);

        return contactRepository.findAllByUserId(user.getId())
                .stream()
                .map(ContactDto::contactToDto)
                .toList();
    }
}
