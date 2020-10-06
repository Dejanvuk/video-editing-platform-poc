package com.dejanvuk.microservices.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class HttpError {
    private HttpStatus httpStatus;
    private ZonedDateTime timestamp;
    private String message;
}
