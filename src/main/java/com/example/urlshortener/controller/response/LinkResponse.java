package com.example.urlshortener.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkResponse {
    private String shortLink;
    private String originalLink;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Long visitCount;
}
