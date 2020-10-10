package com.dejanvuk.microservices.commentmicroservice.config;

import com.dejanvuk.microservices.api.exceptions.HttpError;
import com.dejanvuk.microservices.api.exceptions.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public HttpError handleInvalidInputExceptions(Exception ex) {
        return createHttpError(HttpStatus.UNPROCESSABLE_ENTITY, ex);
    }

    private HttpError createHttpError(HttpStatus httpStatus, Exception ex) {
        LOG.debug("Exception: {} message: {}", HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        return new HttpError(httpStatus, ZonedDateTime.now(), ex.getMessage());
    }
}
