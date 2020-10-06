package com.dejanvuk.microservices.commentmicroservice.exceptions;

public class InvalidCommentIdException extends RuntimeException {
    public InvalidCommentIdException() {
        super();
    }

    public InvalidCommentIdException(String message) {
        super(message);
    }

    public InvalidCommentIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCommentIdException(Throwable cause) {
        super(cause);
    }
}
