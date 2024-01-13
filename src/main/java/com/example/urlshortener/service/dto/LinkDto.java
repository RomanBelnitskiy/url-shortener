package com.example.urlshortener.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto {
    private long id;
    private String shortLink;
    private String longLink;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private long transitions;
}
