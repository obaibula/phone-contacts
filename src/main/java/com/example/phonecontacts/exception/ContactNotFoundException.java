package com.example.phonecontacts.exception;

public class ContactNotFoundException extends EntityNotFoundException {
    public ContactNotFoundException(String message) {
        super(message);
    }
}
