package com.example.urlshortener.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ShortLinkValidatorTest {
    private ShortLinkValidator validator;

    @BeforeEach
    void init() {
        validator = new ShortLinkValidator();
    }

    @Test
    @DisplayName("When short link is null then return false")
    void whenShortLinkIsNull_ThenReturnFalse() {
        boolean result = validator.validate(null);

        assertFalse(result);
    }

    @Test
    @DisplayName("When short link consists of 8 characters or numbers then return true")
    void whenShortLinkConsistsOf8CharOrNum_ThenReturnTrue() {
        boolean result = validator.validate("ABcd8822");

        assertTrue(result);
    }

    @ParameterizedTest
    @DisplayName("When short link don't consists of 8 characters or numbers then return false")
    @ValueSource(strings = {"", "asd", " 1234asd", ";:@#,.", "        "})
    void whenShortLinkIsNotValid_ThenReturnFalse(String shortLink) {
        boolean result = validator.validate(shortLink);

        assertFalse(result);
    }
}