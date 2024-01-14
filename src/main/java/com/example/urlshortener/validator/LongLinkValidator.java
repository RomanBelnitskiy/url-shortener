package com.example.urlshortener.validator;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class LongLinkValidator {
    private static final String[] schemes = {"http", "https"};
    private final UrlValidator urlValidator;
    private final RestTemplate restTemplate;

    public LongLinkValidator() {
        Duration timeout = Duration.ofMillis(1500);
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();

        this.urlValidator = new UrlValidator(schemes);
    }

    public boolean validate(String link) {
        if (!urlValidator.isValid(link)) return false;

        try {
            ResponseEntity<String> response = restTemplate
                    .exchange(link, HttpMethod.HEAD, null, String.class);

            return response.getStatusCode().is2xxSuccessful() ||
                    response.getStatusCode().is3xxRedirection();
        } catch (Exception e) {
            return false;
        }
    }
}
