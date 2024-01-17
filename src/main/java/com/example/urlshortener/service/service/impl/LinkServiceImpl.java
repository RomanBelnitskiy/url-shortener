package com.example.urlshortener.service.service.impl;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.generator.Generator;
import com.example.urlshortener.service.service.LinkService;
import com.example.urlshortener.validator.LongUrlValidator;
import com.example.urlshortener.validator.ShortUrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;
    private final ShortUrlValidator shortUrlValidator;
    private final LongUrlValidator longUrlValidator;
    private final Generator generator;

    @Override
    public List<LinkDto> findAll() {
        return linkMapper.toDtos(linkRepository.findAll());
    }

    @Override
    @Transactional
    public LinkDto create(LinkDto link) {
        Objects.requireNonNull(link);

        LinkEntity entity = linkMapper.toEntity(link);

        if (!longUrlValidator.validate(entity.getLongUrl())) {
            throw new IllegalArgumentException("Invalid long link");
        }

        do {
            entity.setShortUrl(generator.generateShortUrl());
            if (!shortUrlValidator.validate(entity.getShortUrl())) {
                throw new IllegalArgumentException("Invalid short link");
            }
        } while (linkRepository.existsByShortUrl(entity.getShortUrl()));

        return linkMapper.toDto(linkRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteByShortUrl(String shortUrl) {
        if (!shortUrlValidator.validate(shortUrl)) {
            throw new IllegalArgumentException("Invalid id");
        }

        linkRepository.deleteById(shortUrl);
    }

    @Override
    @Transactional
    public void update(LinkDto link) {
        if (!shortUrlValidator.validate(link.getShortUrl())) {
            throw new IllegalArgumentException("Invalid short link");
        }
        LinkEntity entity = linkRepository.findByShortUrl(
                link.getShortUrl()).orElseThrow(LinkNotFoundException::new);

        if (!entity.getLongUrl().equals(link.getLongUrl())) {
            if (longUrlValidator.validate(link.getLongUrl())) {
                entity.setLongUrl(link.getLongUrl());
            } else {
                throw new IllegalArgumentException("Invalid long link");
            }
        }

        if (!entity.getExpiredAt().equals(link.getExpiredAt())) {
            entity.setExpiredAt(link.getExpiredAt());
        }
    }


    @Override
    public LinkDto getByShortUrl(String shortUrl) {
        if (!shortUrlValidator.validate(shortUrl)) {
            throw new IllegalArgumentException("Invalid id");
        }

        LinkEntity entity = linkRepository.findByShortUrl(shortUrl).orElseThrow(LinkNotFoundException::new);
        return linkMapper.toDto(entity);
    }
}
