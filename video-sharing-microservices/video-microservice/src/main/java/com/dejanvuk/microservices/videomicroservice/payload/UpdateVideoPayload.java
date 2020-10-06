package com.dejanvuk.microservices.videomicroservice.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdateVideoPayload {
    @Size(max = 64)
    @NotBlank
    private String newTitle;

    @Size(max = 256)
    @NotBlank
    private String description;
}
