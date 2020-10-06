package com.dejanvuk.microservices.commentmicroservice.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdateCommentPayload {
    @Size(max = 256)
    @NotBlank
    private String editedContent;
}
