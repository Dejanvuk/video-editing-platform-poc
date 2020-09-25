package com.dejanvuk.microservices.user.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document(collection="users")
public class UserEntity {
    @Setter(AccessLevel.NONE)
    @Id
    private String id;

    @NotBlank
    @Size(max = 30)
    private String name;

    @Size(max = 30)
    private String username;

    @NotBlank
    @Size(max = 30)
    @Email
    private String email;

    //@NotBlank
    @Size(max = 60)
    private String password;

    private Set<Role> roles;

    private String imageUrl;

    private String providerType;

    private String token;

    private Boolean verified;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date created_At;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date updated_At;

    @Version
    private Integer version;

    @CreatedDate
    private void onCreate() {
        created_At = new Date();
    }

    @LastModifiedDate
    private void onUpdate() {
        updated_At = new Date();
    }
}
