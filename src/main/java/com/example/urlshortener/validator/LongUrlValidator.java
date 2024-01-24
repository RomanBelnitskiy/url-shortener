package com.example.urlshortener.validator;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

@Component
public class LongUrlValidator {
    private static final Set<String> schemes = Set.of("http", "https");

    public boolean validate(String link) {
        URI uri;
        if (link == null) {
            return false;
        } else {
            try {
                uri = new URI(link);
            } catch (URISyntaxException ex) {
                return false;
            }

            String scheme = uri.getScheme();
            if (scheme == null || !schemes.contains(scheme)) {
                return false;
            }
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofMillis(3000))
                    .uri(uri)
                    .headers("Accept", "*/*", "Accept-Encoding", "gzip, deflate, br")
                    .build();

            HttpResponse<Void> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() >= 200 && response.statusCode() < 400;
        } catch (Exception e) {
            return false;
        }
    }
}
