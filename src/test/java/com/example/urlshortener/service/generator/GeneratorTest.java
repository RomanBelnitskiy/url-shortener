package com.example.urlshortener.service.generator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {
    private static final int LOOP_COUNT = 1000;
    private Generator generator = new Generator();
    @Test
    void testGenerateShortLinkV1() {
        final char[] testChars = {'a', 'z', 'A', 'Z', '0', '9'};
        boolean[] found = {false, false, false, false, false, false};

        for (int i = 0; i < LOOP_COUNT; i++) {
            String randShortLink = generator.generateShortLink();
            for (int j = 0; j < testChars.length; j++) {
                if (randShortLink.indexOf(testChars[j]) > 0) {
                    found[j] = true;
                }
            }
        }

        for (int i = 0; i < testChars.length; i++) {
            assertTrue(found[i], String.format("alphanumeric character not generated in %d attempts", LOOP_COUNT));
        }
    }

    @Test
    void testGenerateShortLinkV2() {
        Pattern pattern = Pattern.compile("[A-Za-z0-9]{8}");
        String randShortLink = generator.generateShortLink();
        Matcher matcher = pattern.matcher(randShortLink);

        assertTrue(matcher.matches());
    }
}