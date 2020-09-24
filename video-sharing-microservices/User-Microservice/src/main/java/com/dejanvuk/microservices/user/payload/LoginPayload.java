package com.dejanvuk.microservices.user.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginPayload {
    @NotBlank(message = "Username or Email cannot be empty!")
    private String usernameOrEmail;
    @NotBlank(message = "Password cannot be empty!")
    private String password;
}
