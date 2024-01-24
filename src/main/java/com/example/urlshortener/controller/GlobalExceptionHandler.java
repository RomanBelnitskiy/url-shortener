package com.example.urlshortener.controller;

import com.example.urlshortener.exception.LinkNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<Map<String, String>> linkNotFoundException(LinkNotFoundException ex) {
        return new ResponseEntity<>(getErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> illegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(getErrorMap(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> usernameNotFoundExceptionHandler(UsernameNotFoundException ex) {
        return new ResponseEntity<>(getErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> badCredentialsExceptionHandler(BadCredentialsException ex) {
        return new ResponseEntity<>(getErrorMap(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> generalException() {
        return new ResponseEntity<>(getErrorMap("Bad request. Try again!"), HttpStatus.BAD_REQUEST);
    }

    private static Map<String, String> getErrorMap(String errorMessage) {
        return Map.of("error", errorMessage);
    }
}
