package com.dejanvuk.microservices.user.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
public class SignUpPayload {
    @NotBlank
    @Size(max = 30, min = 5)
    private String name;

    @NotBlank
    @Size(max = 20, min = 5)
    private String username;

    @NotBlank
    @Size(max = 30)
    @Email
    private String email;

    @NotBlank
    @Size(max = 30)
    private String password;
}
