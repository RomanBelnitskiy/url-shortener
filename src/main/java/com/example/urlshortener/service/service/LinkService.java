package com.example.urlshortener.service.service;

import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.exception.LinkNotFoundException;

import java.util.List;

public interface LinkService {
    List<LinkDto> findAll();
    LinkDto create(LinkDto dto);
    void deleteById(String id);
    void update(LinkDto note) throws LinkNotFoundException;
    LinkDto getById(String id) throws LinkNotFoundException;
}
