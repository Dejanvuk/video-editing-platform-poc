package com.dejanvuk.microservices.api.user;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Schema(description = "Class representing the user's private details")
@Data
@RequiredArgsConstructor
public class User {
    @ApiModelProperty(notes = "Full name of the person", example = "johndoe321", position = 0)
    private String name;
    @ApiModelProperty(notes = "Unique email", example = "johndoe321", position = 1)
    private String email;
    @ApiModelProperty(notes = "Account's profile image url", example = "http://www.image-provider.com/image.jpeg", position = 2)
    private String imageUrl;
}
