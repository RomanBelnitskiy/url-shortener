package com.example.urlshortener.service.generator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class Generator {
    public String generateShortLink() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
