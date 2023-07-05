package com.example.phonecontacts.contact;

import java.security.Principal;
import java.util.List;

public interface ContactService {
    Contact save(Contact contact, Principal principal);
    List<ContactDto> findAll(Principal principal);
}
