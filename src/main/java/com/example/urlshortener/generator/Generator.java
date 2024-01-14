package com.example.urlshortener.generator;
import org.apache.commons.lang3.RandomStringUtils;

public class Generator {
    public static String generateShortLink() {
        // Генеруємо випадковий рядок довжиною 8 символів
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
