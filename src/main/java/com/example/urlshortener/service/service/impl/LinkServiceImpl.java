package com.example.urlshortener.service.service.impl;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.service.LinkService;
import com.example.urlshortener.validator.LongLinkValidator;
import com.example.urlshortener.validator.ShortLinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.example.urlshortener.service.generator.Generator.generateShortLink;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;
    private final ShortLinkValidator shortLinkValidator;
    private final LongLinkValidator longLinkValidator;

    @Override
    public List<LinkDto> findAll() {
        return linkMapper.toDtos(linkRepository.findAll());
    }

    @Override
    @Transactional
    public LinkDto create(LinkDto link) {
        Objects.requireNonNull(link);

        LinkEntity entity = linkMapper.toEntity(link);

        if (!longLinkValidator.validate(entity.getLongLink())) {
            throw new IllegalArgumentException("Invalid long link");
        }

        do {
            entity.setShortLink(generateShortLink());
            if (!shortLinkValidator.validate(entity.getShortLink())) {
                throw new IllegalArgumentException("Invalid short link");
            }
        } while (linkRepository.existsByShortLink(entity.getShortLink()));

        return linkMapper.toDto(linkRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        if (!shortLinkValidator.validate(id)) {
            throw new IllegalArgumentException("Invalid id");
        }

        linkRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(LinkDto link) {
        if (!shortLinkValidator.validate(link.getShortLink())) {
            throw new IllegalArgumentException("Invalid short link");
        }
        LinkEntity entity = linkRepository.findByShortLink(
                link.getShortLink()).orElseThrow(LinkNotFoundException::new);

        if (!entity.getLongLink().equals(link.getLongLink())) {
            if (longLinkValidator.validate(link.getLongLink())) {
                entity.setLongLink(link.getLongLink());
            } else {
                throw new IllegalArgumentException("Invalid long link");
            }
        }

        if (!entity.getExpiredAt().equals(link.getExpiredAt())) {
            entity.setExpiredAt(link.getExpiredAt());
        }
    }


    @Override
    public LinkDto getById(String id) {
        if (!shortLinkValidator.validate(id)) {
            throw new IllegalArgumentException("Invalid id");
        }

        LinkEntity entity = linkRepository.findByShortLink(id).orElseThrow(LinkNotFoundException::new);
        return linkMapper.toDto(entity);
    }
}
