package com.example.urlshortener.service.service;

import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.exception.LinkNotFoundException;

import java.util.List;

public interface LinkService {
    List<LinkDto> findAll();
    LinkDto create(LinkDto dto);
    void deleteByShortUrl(String shortUrl);
    void update(LinkDto dto) throws LinkNotFoundException;
    LinkDto getByShortUrl(String shortUrl) throws LinkNotFoundException;
    LinkDto getByShortUrlAndIncreaseTransitions(String shortUrl);
    void updateTransitions(String shortUrl, Long transitions);
}
