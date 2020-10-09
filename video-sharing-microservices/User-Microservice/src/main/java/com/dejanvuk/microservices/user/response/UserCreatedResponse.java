package com.dejanvuk.microservices.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreatedResponse {
    private String message;
}
