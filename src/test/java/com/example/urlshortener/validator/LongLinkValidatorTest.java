package com.example.urlshortener.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LongLinkValidatorTest {
    private final LongLinkValidator validator;

    LongLinkValidatorTest() {
        validator = new LongLinkValidator();
    }

    @Test
    @DisplayName("When long link is valid then return true")
    void whenLongLinkIsValid_ThenReturnTrue() {
        String longLink = "https://github.com/RomanBelnitskiy/url-shortener";

        boolean result = validator.validate(longLink);

        assertTrue(result);
    }

    @ParameterizedTest
    @DisplayName("When long link is not valid URL then return false")
    @ValueSource(strings = {
            "ABcd8822",
            "https://github1.com/RomanBelnitskiy/url-shortener"
    })
    void whenLongLinkIsNotValidURL_ThenReturnFalse(String longLink) {
        boolean result = validator.validate(longLink);

        assertFalse(result);
    }

}