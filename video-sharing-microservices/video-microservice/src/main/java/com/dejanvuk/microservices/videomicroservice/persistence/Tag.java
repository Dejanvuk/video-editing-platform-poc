package com.dejanvuk.microservices.videomicroservice.persistence;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class Tag {
    @Size(max = 30)
    private String name;
}
