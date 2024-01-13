package com.example.urlshortener.service.service.impl;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import io.swagger.v3.oas.models.links.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public void deleteById(Long id) {
        //TODO змінти айді на стрінгу
        Objects.requireNonNull(id);

        linkRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(LinkDto link) {
        //TODO Створити клас UpdateLinkRequest і в мапері перетворити на дто
        if (Objects.isNull(link.getId())) {
            throw new LinkNotFoundException();
        }
        LinkEntity entity = linkRepository.findById(link.getId()).orElseThrow(() -> new LinkNotFoundException());
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
    public LinkDto getById(Long id) {
        Objects.requireNonNull(id);

        LinkEntity entity = linkRepository.findById(id).orElseThrow(() -> new LinkNotFoundException());
        return linkMapper.toDto(entity);
    }
}
