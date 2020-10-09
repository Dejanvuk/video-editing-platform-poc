package com.dejanvuk.microservices.commentmicroservice.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentPayload {
    @NotBlank
    private String ownerId;

    @NotBlank
    private String videoId;

    @Size(max = 256)
    @NotBlank
    private String content;

}
