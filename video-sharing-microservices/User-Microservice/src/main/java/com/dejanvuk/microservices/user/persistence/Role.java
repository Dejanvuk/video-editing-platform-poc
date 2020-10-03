package com.dejanvuk.microservices.user.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "roles")
public class Role {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private RoleType name;
}
