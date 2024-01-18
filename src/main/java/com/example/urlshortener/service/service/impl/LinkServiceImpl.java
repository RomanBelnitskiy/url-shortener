package com.example.urlshortener.service.service.impl;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.exception.LinkExpiredException;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.generator.Generator;
import com.example.urlshortener.service.service.LinkService;
import com.example.urlshortener.validator.LongUrlValidator;
import com.example.urlshortener.validator.ShortUrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Transactional(readOnly = true)
    public List<LinkDto> findAll() {
        return linkMapper.toDtos(linkRepository.findAll());
    }

    @Override
    @Transactional
    public LinkDto create(LinkDto link) {
        Objects.requireNonNull(link);

        LinkEntity entity = linkMapper.toEntity(link);

        if (!longUrlValidator.validate(entity.getLongUrl())) {
            throw new IllegalArgumentException("Invalid long url");
        }

        do {
            entity.setShortUrl(generator.generateShortUrl());
            validateShortUrl(entity.getShortUrl());
        } while (linkRepository.existsByShortUrl(entity.getShortUrl()));

        return linkMapper.toDto(linkRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteByShortUrl(String shortUrl) {
        validateShortUrl(shortUrl);

        linkRepository.deleteById(shortUrl);
    }

    @Override
    @Transactional
    public void update(LinkDto link) {
        validateShortUrl(link.getShortUrl());
        LinkEntity entity = linkRepository.findByShortUrl(
                link.getShortUrl()).orElseThrow(LinkNotFoundException::new);

        if (!entity.getLongUrl().equals(link.getLongUrl())) {
            if (longUrlValidator.validate(link.getLongUrl())) {
                entity.setLongUrl(link.getLongUrl());
            } else {
                throw new IllegalArgumentException("Invalid long url");
            }
        }

        if (!entity.getExpiredAt().equals(link.getExpiredAt())) {
            entity.setExpiredAt(link.getExpiredAt());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LinkDto getByShortUrl(String shortUrl) {
        validateShortUrl(shortUrl);

        LinkEntity entity = linkRepository.findByShortUrl(shortUrl)
                .orElseThrow(LinkNotFoundException::new);
        return linkMapper.toDto(entity);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LinkDto getByShortUrlAndIncreaseTransitions(String shortUrl) {
        validateShortUrl(shortUrl);

        LinkEntity entity = linkRepository.findByShortUrl(shortUrl)
                .orElseThrow(LinkNotFoundException::new);

        if (entity.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new LinkExpiredException();
        }

        entity.setTransitions(entity.getTransitions() + 1);

        return linkMapper.toDto(entity);
    }

    private void validateShortUrl(String shortUrl) {
        if (!shortUrlValidator.validate(shortUrl)) {
            throw new IllegalArgumentException("Invalid short url");
        }
    }
}
