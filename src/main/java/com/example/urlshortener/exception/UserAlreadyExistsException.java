package com.example.urlshortener.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String USER_FOUND_TEXT_WITH_USERNAME = "User with username %s already exists.";

    public UserAlreadyExistsException(String username) {
        super(String.format(USER_FOUND_TEXT_WITH_USERNAME, username));
    }
}
