package com.example.urlshortener.validator;

import com.example.urlshortener.data.repository.LinkRepository;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ShortLinkValidator {
    private static final String PATTERN = "[A-Za-z0-9]{8}";
    private final LinkRepository repository;
    private final Pattern pattern;

    public ShortLinkValidator(LinkRepository repository) {
        this.repository = repository;
        this.pattern = Pattern.compile(PATTERN);
    }

    public boolean validate(String shorLink) {
        if (shorLink == null) return false;
        if (!pattern.matcher(shorLink).matches()) return false;

        return !repository.existsByShortLink(shorLink);
    }
}
