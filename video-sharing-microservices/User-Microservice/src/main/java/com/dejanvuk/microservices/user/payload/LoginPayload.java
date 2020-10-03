package com.dejanvuk.microservices.user.payload;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "Class representing a user's sign in request")
@Data
public class LoginPayload {
    @NotBlank(message = "Username or Email cannot be empty!")
    @ApiModelProperty(notes = "Username or Email of the account", example = "johndoe321", required = true, position = 0)
    private String usernameOrEmail;
    @NotBlank(message = "Password cannot be empty!")
    @ApiModelProperty(notes = "The password associated with the account", required = true, position = 1)
    private String password;
}
