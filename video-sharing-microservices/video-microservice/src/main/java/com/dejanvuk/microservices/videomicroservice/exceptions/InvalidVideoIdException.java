package com.dejanvuk.microservices.videomicroservice.exceptions;

public class InvalidVideoIdException extends RuntimeException {
    public InvalidVideoIdException() {
        super();
    }

    public InvalidVideoIdException(String message) {
        super(message);
    }

    public InvalidVideoIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVideoIdException(Throwable cause) {
        super(cause);
    }
}
