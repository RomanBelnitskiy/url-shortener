package com.example.urlshortener.service.service;

import com.example.urlshortener.service.dto.LinkDto;

import java.util.List;

public interface LinkService {
    List<LinkDto> findAll();
    LinkDto create(LinkDto dto);
    void deleteById(Long id);
    void update(LinkDto note);
    LinkDto getById(Long id);
}
