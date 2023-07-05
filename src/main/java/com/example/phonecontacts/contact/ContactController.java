package com.example.phonecontacts.contact;

import jakarta.validation.Valid;
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
    public ResponseEntity<ContactDto> createContact(@RequestBody @Valid ContactDto contactDto, Principal principal) {

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

    @DeleteMapping
    public ResponseEntity<Void> deleteContact(@RequestBody Contact contact){
        contactService.deleteByName(contact.getName());
        return ResponseEntity.noContent().build();
    }

    // todo
    @PutMapping
    public ResponseEntity<ContactDto> updateContact(@RequestBody @Valid ContactDto contactDto, Principal principal){
        var savedContact = contactService.save(ContactDto.DtoToContact(contactDto), principal);
        return created(getLocation(savedContact))
                .body(ContactDto.contactToDto(savedContact));
    }

}
