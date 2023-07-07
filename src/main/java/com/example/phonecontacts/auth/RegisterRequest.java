package com.example.phonecontacts.auth;


import com.example.phonecontacts.validation.UniqueLogin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @UniqueLogin
        @NotNull(message = "Username must not be null")
        @Pattern(regexp = "^[A-Za-z0-9]+$",
        message = "Invalid username: Must contain only latin characters and numbers")
        @Size(min = 5, max = 30,
                message = "Invalid username: Must be of 5 - 30 characters")
        String username,
        @NotNull(message = "Password must not be null")
        @Pattern(regexp = "^(?=.*\\d)(?=.*\\p{Lower})(?=.*\\p{Upper})(?=.*\\p{Punct}).*$",
                message = "Invalid password: The password must contain: numbers, " +
                        "lowercase/uppercase letters, " +
                        "and special symbols")
        @Size(min = 8, max = 32, message = "Invalid password: Must be of 8-32 characters")
        String password) {
}
