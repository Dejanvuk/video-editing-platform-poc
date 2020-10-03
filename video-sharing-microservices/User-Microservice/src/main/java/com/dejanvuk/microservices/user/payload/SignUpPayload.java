package com.dejanvuk.microservices.user.payload;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Schema(description = "Class representing a user's sign up request")
@Data
public class SignUpPayload {
    @NotBlank
    @Size(max = 30, min = 5)
    @ApiModelProperty(notes = "Full name of the person", example = "John Doe", required = true, position = 0)
    private String name;

    @NotBlank
    @Size(max = 20, min = 5)
    @ApiModelProperty(notes = "Unique username of the account", example = "johndoe321", required = true, position = 1)
    private String username;

    @NotBlank
    @Size(max = 30)
    @Email
    @ApiModelProperty(notes = "Unique email", example = "john@doe.com", required = true, position = 2)
    private String email;

    @NotBlank
    @Size(max = 30)
    @ApiModelProperty(notes = "Password of the account", required = true, position = 3)
    private String password;
}
