package com.dejanvuk.microservices.videomicroservice.config;

import com.dejanvuk.microservices.videomicroservice.exceptions.InvalidVideoIdException;
import com.dejanvuk.microservices.videomicroservice.exceptions.VideoNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
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
    @ExceptionHandler(InvalidVideoIdException.class)
    public HttpError handleInvalidVideoIdExceptions(Exception ex) {
        return createHttpError(HttpStatus.UNPROCESSABLE_ENTITY, ex);
    }

    @ResponseStatus()
    @ExceptionHandler(VideoNotFoundException.class)
    public HttpError handleVideoNotFoundExceptions(Exception ex) {
        return createHttpError(HttpStatus.NOT_FOUND, ex);
    }

    private HttpError createHttpError(HttpStatus httpStatus, Exception ex) {
        LOG.debug("Exception: {} message: {}", HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        return new HttpError(httpStatus, ZonedDateTime.now(), ex.getMessage());
    }

    @Data
    @AllArgsConstructor
    private class HttpError {
        private HttpStatus httpStatus;
        private ZonedDateTime timestamp;
        private String message;
    }
}
