package com.dejanvuk.microservices.user.persistence;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@Document(collection = "roles")
public class Role {
    @Id
    private Long id;

    private RoleType name;
}
