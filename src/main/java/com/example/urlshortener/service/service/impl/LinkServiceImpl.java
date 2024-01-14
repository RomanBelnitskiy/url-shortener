package com.example.urlshortener.service.service.impl;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.service.LinkService;
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

    @Override
    public List<LinkDto> findAll() {
        return linkMapper.toDtos(linkRepository.findAll());
    }

    @Override
    @Transactional
    public LinkDto create(LinkDto link) {
        Objects.requireNonNull(link);

        // TODO треба згенерувати коротку лінку

        // TODO також провалідувати лінку

        LinkEntity entity = linkMapper.toEntity(link);
        return linkMapper.toDto(linkRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        //TODO змінти айді на стрінгу
        Objects.requireNonNull(id);

        linkRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(LinkDto link) throws LinkNotFoundException {
        //TODO Створити клас UpdateLinkRequest і в мапері перетворити на дто
        if (Objects.isNull(link.getShortLink())) {
            throw new LinkNotFoundException();
        }
        LinkEntity entity = linkRepository.findByShortLink(
                link.getShortLink()).orElseThrow(LinkNotFoundException::new
        );
        if (link.getLongLink() != null) {
            //TODO треба додати валідацію довгої лінки
            entity.setLongLink(link.getLongLink());
        }
        if (link.getShortLink() != null) {
            entity.setExpiredAt(link.getExpiredAt());
        }
        linkRepository.save(entity);
    }

    @Override
    public LinkDto getById(String id) throws LinkNotFoundException {
        Objects.requireNonNull(id);

        LinkEntity entity = linkRepository.findByShortLink(id).orElseThrow(LinkNotFoundException::new);
        return linkMapper.toDto(entity);
    }
}
