package com.example.urlshortener.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ShortUrlValidator {
    private static final String PATTERN = "[A-Za-z0-9]{8}";
    private final Pattern pattern;

    public ShortUrlValidator() {
        this.pattern = Pattern.compile(PATTERN);
    }

    public boolean validate(String shorLink) {
        if (shorLink == null) return false;
        return pattern.matcher(shorLink).matches();
    }
}
