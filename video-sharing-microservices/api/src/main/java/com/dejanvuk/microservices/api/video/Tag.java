package com.dejanvuk.microservices.api.video;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class Tag {
    @Size(max = 30)
    private String name;
}
