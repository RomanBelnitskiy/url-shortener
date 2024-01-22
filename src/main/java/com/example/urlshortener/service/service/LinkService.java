package com.example.urlshortener.service.service;

import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.exception.LinkNotFoundException;

import java.util.List;

public interface LinkService {
    List<LinkDto> findAll(Long userId);
    LinkDto create(LinkDto dto, Long userId);
    void deleteByShortUrl(String shortUrl);
    void update(LinkDto dto, Long userId);
    LinkDto getByShortUrl(String shortUrl, Long userId);
    LinkDto getByShortUrlAndIncreaseTransitions(String shortUrl);
    void updateTransitions(String shortUrl, Long transitions);
}
