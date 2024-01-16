package com.example.urlshortener.validator;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class LongLinkValidator {
    private static final String[] schemes = {"http", "https"};
    private final UrlValidator urlValidator;

    public LongLinkValidator() {
        this.urlValidator = new UrlValidator(schemes);
    }

    public boolean validate(String link) {
        if (!urlValidator.isValid(link)) return false;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofMillis(1500))
                    .uri(new URI(link))
                    .headers("Accept", "*/*", "Accept-Encoding", "gzip, deflate, br")
                    .build();

            HttpResponse<Void> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200;

        } catch (Exception e) {
            return false;
        }
    }
}
