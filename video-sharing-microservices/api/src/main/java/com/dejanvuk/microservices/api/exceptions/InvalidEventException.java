package com.dejanvuk.microservices.api.exceptions;

public class InvalidEventException extends RuntimeException  {
    public InvalidEventException() {
        super();
    }

    public InvalidEventException(String message) {
        super(message);
    }

    public InvalidEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEventException(Throwable cause) {
        super(cause);
    }
}
