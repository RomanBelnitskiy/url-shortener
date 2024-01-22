package com.example.urlshortener.service.generator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {
    private static Generator generator;
    private static Pattern pattern;
    @BeforeAll
    static void init() {
        generator = new Generator();
        pattern = Pattern.compile("[A-Za-z0-9]{8}");
    }
    @ParameterizedTest
    @MethodSource("randomShortUrlProvider")
    void testGenerateShortLink(String randShortUrl) {
        Matcher matcher = pattern.matcher(randShortUrl);

        assertTrue(matcher.matches());
    }
    static Stream<String> randomShortUrlProvider() {
        return Stream
                .generate(generator::generateShortUrl)
                .limit(10);
    }
}