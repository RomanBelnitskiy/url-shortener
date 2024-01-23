package com.example.urlshortener.controller;

import com.example.urlshortener.exception.LinkNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void testLinkNotFoundException() {
        LinkNotFoundException exception = new LinkNotFoundException(1L);

        ResponseEntity<Map<String, String>> expected = new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.NOT_FOUND);

        ResponseEntity<Map<String, String>> actual = globalExceptionHandler.linkNotFoundException(exception);

        assertEquals(expected, actual);
    }

    @Test
    void testIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("some exception");

        ResponseEntity<Map<String, String>> expected = new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.BAD_REQUEST);

        ResponseEntity<Map<String, String>> actual = globalExceptionHandler.illegalArgumentException(exception);

        assertEquals(expected, actual);
    }

    @Test
    void testGeneralException() {
        ResponseEntity<Map<String, String>> expected = new ResponseEntity<>(Map.of("error", "Bad request. Try again!"), HttpStatus.BAD_REQUEST);

        ResponseEntity<Map<String, String>> actual = globalExceptionHandler.generalException();

        assertEquals(expected, actual);
    }
}