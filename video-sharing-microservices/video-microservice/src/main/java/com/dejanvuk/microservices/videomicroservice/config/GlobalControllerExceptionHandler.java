package com.dejanvuk.microservices.videomicroservice.config;

import com.dejanvuk.microservices.api.exceptions.HttpError;
import com.dejanvuk.microservices.api.exceptions.NotFoundException;
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

    @ResponseStatus()
    @ExceptionHandler(NotFoundException.class)
    public HttpError handleNotFoundExceptions(Exception ex) {
        return createHttpError(HttpStatus.NOT_FOUND, ex);
    }

    private HttpError createHttpError(HttpStatus httpStatus, Exception ex) {
        LOG.debug("Exception: {} message: {}", httpStatus, ex.getMessage());
        return new HttpError(httpStatus, ZonedDateTime.now(), ex.getMessage());
    }

}
