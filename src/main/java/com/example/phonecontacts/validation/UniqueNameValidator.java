package com.example.phonecontacts.validation;

import com.example.phonecontacts.contact.ContactRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {
    private final ContactRepository contactRepository;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return !contactRepository.existsByName(name);
    }
}
