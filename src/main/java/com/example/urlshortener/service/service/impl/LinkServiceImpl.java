package com.example.urlshortener.service.service.impl;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    @Override
    public List<LinkDto> listAll() {
        return linkMapper.toDtos(linkRepository.findAll());
    }

    @Override
    public LinkDto add(LinkDto link) {
        Objects.requireNonNull(link);

        LinkEntity entity = linkMapper.toEntity(link);
        return linkMapper.toDto(linkRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id);

        linkRepository.deleteById(id);
    }

    @Override
    public void update(LinkDto link) {
        if (Objects.isNull(link.getId())) {
            throw new LinkNotFoundException();
        }
        getById(link.getId());
        linkRepository.save(linkMapper.toEntity(link));
    }

    @Override
    public LinkDto getById(Long id) {
        Objects.requireNonNull(id);

        LinkEntity entity = linkRepository.findById(id).orElseThrow(() -> new LinkNotFoundException());
        return linkMapper.toDto(entity);
    }
}
