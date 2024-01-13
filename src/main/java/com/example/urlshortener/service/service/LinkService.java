package com.example.urlshortener.service.service;

import com.example.urlshortener.service.dto.LinkDto;

import java.util.List;

public interface LinkService {
    List<LinkDto> listAll();
    LinkDto add(LinkDto dto);
    void deleteById(Long id);
    void update(LinkDto note);
    LinkDto getById(Long id);
}
