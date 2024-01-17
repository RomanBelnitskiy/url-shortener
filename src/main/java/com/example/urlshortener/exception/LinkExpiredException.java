package com.example.urlshortener.exception;

public class LinkExpiredException extends RuntimeException {
    private static final String LINK_EXPIRED_EXCEPTION_TEXT = "The link has expired.";

    public LinkExpiredException() {
        super(LINK_EXPIRED_EXCEPTION_TEXT);
    }
}
