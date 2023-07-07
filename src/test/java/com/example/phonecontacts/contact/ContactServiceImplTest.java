package com.example.phonecontacts.contact;

import com.example.phonecontacts.exception.ContactNotFoundException;
import com.example.phonecontacts.exception.UserNotAuthorizedException;
import com.example.phonecontacts.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {
    @Mock private ContactRepository contactRepository;
    @Mock private UserDetailsService userDetailsService;

    private ContactService underTest;

    @BeforeEach
    void setUp(){
        underTest = new ContactServiceImpl(contactRepository, userDetailsService);
    }

    @Test
    void canAddContact(){
        // given
        var contact = new Contact();
        contact.setName("Ivan Volodymyrovych");

        var principal = Mockito.mock(Principal.class);
        var user = new User();

        String username = "user123";
        user.setUsername(username);

        given(principal.getName())
                .willReturn(username);
        given(userDetailsService.loadUserByUsername(username))
                .willReturn(user);
        // when
        underTest.save(contact, principal);

        // then
        verify(contactRepository, times(1)).save(contact);
        verify(userDetailsService, times(1)).loadUserByUsername(username);

        var contactArgumentCaptor = ArgumentCaptor.forClass(Contact.class);
        verify(contactRepository).save(contactArgumentCaptor.capture());
        var capturedContact = contactArgumentCaptor.getValue();

        assertThat(contact.getName()).isEqualTo(capturedContact.getName());
        assertThat(contact.getUser()).isEqualTo(user);

    }

    @Test
    void shouldReturnListOfContactDtosForTheCurrentUser(){
        // given
        var principal = Mockito.mock(Principal.class);
        var user = new User();

        String username = "user123";
        user.setUsername(username);

        given(principal.getName())
                .willReturn(username);
        given(userDetailsService.loadUserByUsername(username))
                .willReturn(user);

        given(principal.getName()).willReturn(username);

        List<Contact> contacts = new ArrayList<>();

        Contact contact1 = new Contact();
        contact1.setName("Contact1");
        Contact contact2 = new Contact();
        contact2.setName("Contact2");
        contacts.add(contact1);
        contacts.add(contact2);

        given(contactRepository.findAllByUserId(user.getId()))
                .willReturn(contacts);

        // when
        var result = underTest.findAll(principal);

        // then
        verify(userDetailsService, times(1)).loadUserByUsername(user.getUsername());
        verify(contactRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(contactRepository, userDetailsService);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldDeleteInBulkWhenContactIsFound(){
        // given
        var name = "Viktor Yushchenko";
        var contact = new Contact();
        contact.setName(name);

        given(contactRepository.findByName(name))
                .willReturn(Optional.of(contact));

        // when
        underTest.deleteByName(name);

        // then
        verify(contactRepository, times(1)).findByName(name);
        verify(contactRepository, times(1)).deleteInBulkByName(name);
    }

    @Test
    void shouldThrowExceptionWhenContactIsNotFound(){
        // given
        var name = "Viktor Yushchenko";
        given(contactRepository.findByName(name))
                .willReturn(Optional.empty());


        // then
        assertThatThrownBy(() -> underTest.deleteByName(name))
                .isInstanceOf(ContactNotFoundException.class)
                .hasMessage("Contact not found with name - " + name);
    }

    @Test
    void shouldUpdateWhenContactIsFoundAndOwnedByUser(){
        // given
        Long contactId = 1L;
        String name = "Volodymyr Zelenskii";
        var principal = Mockito.mock(Principal.class);
        var user = new User();

        String username = "user123";
        user.setUsername(username);

        given(principal.getName())
                .willReturn(username);

        var existingContact = new Contact();
        existingContact.setId(contactId);
        existingContact.setName(name);
        existingContact.setUser(user);

        var updatedContact = new Contact();
        updatedContact.setName(name);

        given(contactRepository.findById(contactId))
                .willReturn(Optional.of(existingContact));
        given(contactRepository.save(existingContact))
                .willReturn(existingContact);
        given(contactRepository.findByName(name))
                .willReturn(Optional.empty());

        // when
        var contactAfterUpdate = underTest.update(contactId, updatedContact, principal);
        // then
        assertThat(contactAfterUpdate.getName()).isEqualTo(name);
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(existingContact);
        verifyNoMoreInteractions(contactRepository, userDetailsService);

    }

    @Test
    void shouldThrowExceptionWhenContactIsNotFoundAndOwnedByUser(){
        var name = "Igor";
        var updatedContact = new Contact();
        updatedContact.setName(name);

        assertThatThrownBy(() -> underTest.update(1L,
                updatedContact, null))
                .isInstanceOf(ContactNotFoundException.class)
                .hasMessage("Not found contact with name - " + name);
    }

    @Test
    void shouldThrowExceptionWhenContactIsFoundButNotOwnedByUser(){
        // given
        Long contactId = 1L;
        String name = "Volodymyr Zelenskii";
        var principal = Mockito.mock(Principal.class);
        var user = new User();

        String username = "user123";
        user.setUsername(username);

        given(principal.getName())
                .willReturn("UnauthorizedUser");

        var existingContact = new Contact();
        existingContact.setName(name);
        existingContact.setUser(user);

        given(contactRepository.findById(contactId))
                .willReturn(Optional.of(existingContact));

        assertThatThrownBy(() -> underTest.update(1L, existingContact, principal))
                .isInstanceOf(UserNotAuthorizedException.class)
                .hasMessage("Access denied");


    }

}