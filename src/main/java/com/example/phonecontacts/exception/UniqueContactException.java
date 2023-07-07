package com.example.phonecontacts.exception;

public class UniqueContactException extends RuntimeException {
    public UniqueContactException(String message) {
        super(message);
    }
}
