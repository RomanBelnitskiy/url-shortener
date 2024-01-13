package com.example.urlshortener.service.mapper;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.service.dto.LinkDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LinkMapper {

    public LinkDto toDto(LinkEntity link) {
        return LinkDto.builder()
                .id(link.getId())
                .longLink(link.getLongLink())
                .shortLink(link.getShortLink())
                .createdAt(link.getCreateAt())
                .expiredAt(link.getExpiredAt())
                .transitions(link.getTransitions())
                .build();
    }

    public List<LinkDto> toDtos(Collection<LinkEntity> links) {
        return links.stream().map(this::toDto).collect(Collectors.toList());
    }

    public LinkEntity toEntity(LinkDto dto) {
        return LinkEntity.builder()
                .id(dto.getId())
                .longLink(dto.getLongLink())
                .shortLink(dto.getShortLink())
                .createAt(dto.getCreatedAt())
                .expiredAt(dto.getExpiredAt())
                .transitions(dto.getTransitions())
                .build();
    }

    public List<LinkEntity> toEntities(Collection<LinkDto> dtos) {
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }
}