package com.example.urlshortener.service.generator;
import org.apache.commons.lang3.RandomStringUtils;

public class Generator {
    public static String generateShortLink() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
