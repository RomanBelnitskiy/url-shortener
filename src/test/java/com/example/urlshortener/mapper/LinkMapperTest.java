package com.example.urlshortener.mapper;

import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.service.dto.LinkDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LinkMapperTest {
    private final LinkMapper mapper = new LinkMapper();

    static String url;
    static String host;

    @BeforeAll
    static void staticInit() {
        url = "test";
        host = "test";
    }

    @Test
    void fromEntityToDto() {
        LocalDateTime time = LocalDateTime.now();

        LinkEntity entity = LinkEntity.builder()
                .shortUrl(url)
                .longUrl(url)
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();
        LinkDto actual = mapper.toDto(entity);

        LinkDto expected = LinkDto.builder()
                .shortUrl(url)
                .longUrl(url)
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void fromDtoToEntity() {
        LocalDateTime time = LocalDateTime.now();

        LinkDto dto = LinkDto.builder()
                .shortUrl(url)
                .longUrl(url)
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();

        LinkEntity actual = mapper.toEntity(dto);

        LinkEntity expected = LinkEntity.builder()
                .shortUrl(url)
                .longUrl(url)
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void fromCreateRequestToDto() {
        LocalDateTime time = LocalDateTime.now();

        CreateLinkRequest request = new CreateLinkRequest();
        request.setLongUrl(url);
        LinkDto actual = mapper.toDto(request);

        actual.setShortUrl(url);
        actual.setCreatedAt(time);
        actual.setExpiredAt(time);
        actual.setTransitions(0);

        LinkDto expected = LinkDto.builder()
                .shortUrl(url)
                .longUrl(url)
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void fromUpdateRequestToDto() {
        LocalDateTime time = LocalDateTime.now();

        UpdateLinkRequest request = new UpdateLinkRequest();
        request.setExpiredAt(time);
        request.setLongUrl(url);

        LinkDto actual = mapper.toDto(url, request);

        LinkDto expected = LinkDto.builder()
                .shortUrl(url)
                .longUrl(url)
                .expiredAt(time)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void fromDtoToResponse() {
        LocalDateTime time = LocalDateTime.now();

        LinkDto dto = LinkDto.builder()
                .shortUrl(url)
                .longUrl(url)
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();

        LinkResponse actual = mapper.toResponse(dto, host);
        LinkResponse expected = LinkResponse.builder()
                .fullShortUrl(host + "/" + url)
                .shortUrl(url)
                .longUrl(url)
                .createdAt(time)
                .expiredAt(time)
                .transitions(0L)
                .build();

        assertEquals(expected, actual);
    }
}