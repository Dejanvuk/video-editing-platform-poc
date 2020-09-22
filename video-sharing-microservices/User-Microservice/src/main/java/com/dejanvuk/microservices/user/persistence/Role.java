package com.dejanvuk.microservices.user.persistence;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@Entity
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(name = "name", length = 20, unique = true, nullable = false)
    private RoleType name;
}
