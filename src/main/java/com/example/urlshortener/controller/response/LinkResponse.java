package com.example.urlshortener.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkResponse {
    private String fullShortUrl;
    private String shortUrl;
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Long transitions;
}
