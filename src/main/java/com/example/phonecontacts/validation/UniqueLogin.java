package com.example.phonecontacts.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = UniqueLoginValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UniqueLogin {

    String message() default "This username is taken, please use another one for registration.";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};
}
