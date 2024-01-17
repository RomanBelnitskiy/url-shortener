package com.example.urlshortener.mapper;

import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.service.dto.LinkDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LinkMapper {

    public LinkDto toDto(LinkEntity link) {
        return LinkDto.builder()
                .longUrl(link.getLongUrl())
                .shortUrl(link.getShortUrl())
                .createdAt(link.getCreatedAt())
                .expiredAt(link.getExpiredAt())
                .transitions(link.getTransitions())
                .build();
    }

    public LinkDto toDto(CreateLinkRequest request) {
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
        return LinkDto.builder()
                .longUrl(request.getLongUrl())
                .createdAt(time)
                .expiredAt(time.plusMonths(1))
                .transitions(0)
                .build();
    }

    public LinkEntity toEntity(LinkDto dto) {
        return LinkEntity.builder()
                .longUrl(dto.getLongUrl())
                .shortUrl(dto.getShortUrl())
                .createdAt(dto.getCreatedAt())
                .expiredAt(dto.getExpiredAt())
                .transitions(dto.getTransitions())
                .build();
    }

    public LinkResponse toResponse(LinkDto dto) {
        return LinkResponse.builder()
                .longUrl(dto.getLongUrl())
                .shortUrl(dto.getShortUrl())
                .createdAt(dto.getCreatedAt())
                .expiredAt(dto.getExpiredAt())
                .transitions(dto.getTransitions())
                .build();
    }

    public List<LinkDto> toDtos(List<LinkEntity> links) {
        return links.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<LinkResponse> toResponses(List<LinkDto> dtos) {
        return dtos.stream().map(this::toResponse).collect(Collectors.toList());
    }
    public LinkDto toDto(String shortUrl, UpdateLinkRequest updateLinkRequest) {
        return LinkDto.builder()
                .shortUrl(shortUrl)
                .longUrl(updateLinkRequest.getLongUrl())
                .expiredAt(updateLinkRequest.getExpiredAt()).build();
    }
}