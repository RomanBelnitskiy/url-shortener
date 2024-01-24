package com.example.urlshortener.exception;

public class UserFoundException extends RuntimeException {
    private static final String USER_FOUND_TEXT_WITH_USERNAME = "User with username %s was found.";

    public UserFoundException(String username) {
        super(String.format(USER_FOUND_TEXT_WITH_USERNAME, username));
    }
}
