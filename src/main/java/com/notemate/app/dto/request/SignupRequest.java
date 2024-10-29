package com.notemate.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Name cannot be blank!")
    private String name;

    @NotBlank(message = "Email cannot be blank!")
    @Email(message = "Invalid email!")
    private String email;

    @NotBlank(message = "Username cannot be blank!")
    private String username;

    @NotBlank(message = "Password cannot be blank!")
    private String password;
}
