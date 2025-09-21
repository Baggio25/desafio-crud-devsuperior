package com.devsuperior.desafio.controllers.hendlers;

import com.devsuperior.desafio.dto.CustomError;
import com.devsuperior.desafio.dto.ValidationError;
import com.devsuperior.desafio.services.Exceptions.DatabaseException;
import com.devsuperior.desafio.services.Exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return getCustomErrorResponseEntity(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> resourceNotFound(DatabaseException e, HttpServletRequest request) {
        return getCustomErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            err.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

    private static ResponseEntity<CustomError> getCustomErrorResponseEntity(HttpStatus notFound, String e, HttpServletRequest request) {
        CustomError err = new CustomError(Instant.now(), notFound.value(), e, request.getRequestURI());
        return ResponseEntity.status(notFound).body(err);
    }
}