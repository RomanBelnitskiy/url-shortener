package com.example.urlshortener.mapper;

import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.service.dto.LinkDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LinkMapperTest {
    private LinkMapper mapper = new LinkMapper();

    @Test
    void fromEntityToDto() {
        LocalDateTime time = LocalDateTime.now();

        LinkEntity entity = LinkEntity.builder()
                .shortLink("test")
                .longLink("test")
                .createAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();
        LinkDto actual = mapper.toDto(entity);

        LinkDto expected = LinkDto.builder()
                .shortLink("test")
                .longLink("test")
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
                .shortLink("test")
                .longLink("test")
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();

        LinkEntity actual = mapper.toEntity(dto);

        LinkEntity expected = LinkEntity.builder()
                .shortLink("test")
                .longLink("test")
                .createAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void fromCreateRequestToDto() {
        LocalDateTime time = LocalDateTime.now();

        CreateLinkRequest request = new CreateLinkRequest();
        request.setExpiredAt(time);
        request.setOriginalLink("test");
        LinkDto actual = mapper.toDto(request);

        actual.setShortLink("test");
        actual.setCreatedAt(time);
        actual.setExpiredAt(time);
        actual.setTransitions(0);

        LinkDto expected = LinkDto.builder()
                .shortLink("test")
                .longLink("test")
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
        request.setOriginalLink("test");

        LinkDto actual = mapper.toDto("test", request);

        LinkDto expected = LinkDto.builder()
                .shortLink("test")
                .longLink("test")
                .expiredAt(time)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void fromDtoToResponse() {
        LocalDateTime time = LocalDateTime.now();

        LinkDto dto = LinkDto.builder()
                .shortLink("test")
                .longLink("test")
                .createdAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();


        LinkResponse actual = mapper.toResponse(dto);
        LinkResponse expected = LinkResponse.builder()
                .shortLink("test")
                .originalLink("test")
                .createdAt(time)
                .expiredAt(time)
                .visitCount(0L)
                .build();

        assertEquals(expected, actual);
    }
}