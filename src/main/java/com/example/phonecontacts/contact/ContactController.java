package com.example.phonecontacts.contact;

import com.example.phonecontacts.validation.PostInfo;
import com.example.phonecontacts.validation.PutInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
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

    // Please note that due to the limited timeframe of 3 days for completing the test task,
    // I implemented the DTO mapping in a functional but suboptimal manner.
    // Also, it should be in the service class.
    @PostMapping
    public ResponseEntity<ContactDto> createContact(@RequestBody @Validated(PostInfo.class) ContactDto contactDto, Principal principal) {

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
    public ResponseEntity<Void> deleteContact(@RequestBody Contact contact, Principal principal) {
        contactService.deleteByName(contact.getName(), principal);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{contactId}")
    public ResponseEntity<Void> updateContact(@PathVariable Long contactId,
                                              @RequestBody @Validated(PutInfo.class) ContactDto contactUpdate,
                                              Principal principal) {

        contactService.update(
                contactId,
                ContactDto.DtoToContact(contactUpdate),
                principal);

        return ResponseEntity.noContent().build();
    }

}
