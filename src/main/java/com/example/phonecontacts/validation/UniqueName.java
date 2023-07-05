package com.example.phonecontacts.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = UniqueNameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UniqueName {

    String message() default "Contact is already registered";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};
}
