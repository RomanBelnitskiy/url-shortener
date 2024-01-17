package com.example.urlshortener.service.generator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {
    private static Generator generator;
    private static Pattern pattern;
    @BeforeAll
    static void init() {
        generator = new Generator();
        pattern = Pattern.compile("[A-Za-z0-9]{8}");
    }
    @ParameterizedTest
    @MethodSource("randomShortLinkProvider")
    void testGenerateShortLink(String randShortLink) {
        Matcher matcher = pattern.matcher(randShortLink);

        assertTrue(matcher.matches());
    }
    static Stream<String> randomShortLinkProvider() {
        return Stream
                .generate(generator::generateShortLink)
                .limit(10);
    }
}