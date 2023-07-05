package com.example.phonecontacts.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;
    private final UserDetailsService userDetailsService;

    @PostMapping
    private ResponseEntity<ContactDto> createContact(@RequestBody ContactDto contactDto, Principal principal) {

        var savedContact = contactService.save(ContactDto.DtoToContact(contactDto), principal);


        return created(getLocation(savedContact))
                .body(ContactDto.contactToDto(savedContact));
    }

    private URI getLocation(Contact contact) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(contact.getId())
                .toUri();
    }

    @GetMapping
    public ResponseEntity<List<ContactDto>> findAll(Principal principal) {
        return ResponseEntity.ok(contactService.findAll(principal));
    }
}
