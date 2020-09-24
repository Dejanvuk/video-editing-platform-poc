package com.dejanvuk.microservices.user.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private String imageUrl;
}
