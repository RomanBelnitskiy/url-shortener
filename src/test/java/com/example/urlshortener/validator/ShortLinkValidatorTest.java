package com.example.urlshortener.validator;

import com.example.urlshortener.data.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortLinkValidatorTest {
    private ShortLinkValidator validator;
    @Mock
    private LinkRepository repository;

    @BeforeEach
    void init() {
        validator = new ShortLinkValidator(repository);
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

    @Test
    @DisplayName("When such short link is present in DB then return false")
    void whenShortLinkPresentInDB_ThenReturnFalse() {
        String shortLink = "ABcd8822";

        when(repository.existsByShortLink(shortLink)).thenReturn(true);

        boolean result = validator.validate(shortLink);

        assertFalse(result);
    }

    @Test
    @DisplayName("When such short link is not present in DB then return true")
    void whenShortLinkNotPresentInDB_ThenReturnTrue() {
        String shortLink = "ABcd8822";

        when(repository.existsByShortLink(shortLink)).thenReturn(false);

        boolean result = validator.validate(shortLink);

        assertTrue(result);
    }
}