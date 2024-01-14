package com.example.urlshortener.service;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.dto.exception.LinkNotFoundException;
import com.example.urlshortener.service.mapper.LinkMapper;
import com.example.urlshortener.service.service.impl.LinkServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {
    @Mock
    private LinkRepository repository;
    @Mock
    private LinkMapper mapper;
    @InjectMocks
    private LinkServiceImpl service;

    @Test
    @DisplayName("Should create the link with valid input")
    void shouldCreateLinkWithValidInputTest() {
        LinkEntity linkEntity = createLinkEntity("short-link", "long-link");
        LinkDto inputDto = new LinkDto();
        when(mapper.toEntity(inputDto)).thenReturn(linkEntity);
        when(mapper.toDto(linkEntity)).thenReturn(inputDto);
        when(repository.save(any(LinkEntity.class))).thenReturn(linkEntity);

        LinkDto result = service.create(inputDto);

        assertNotNull(result);
        assertEquals(inputDto, result);
        verify(repository, times(1)).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the link is null")
    void shouldThrowExceptionWhenLinkIsNullTest() {
        LinkDto inputDto = null;

        assertThrows(NullPointerException.class, () -> service.create(inputDto));
        verify(repository, never()).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should call repository.findAll() one time")
    void shouldCallRepositoryFindAllOneTimeTest() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        service.findAll();
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list")
    void shouldReturnEmptyListTest() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<LinkDto> result = service.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a list of two links")
    void shouldReturnListOfLinksTest() {
        LinkEntity firstLink = createLinkEntity("short-link 1", "long-link 1");
        LinkEntity secondLink = createLinkEntity("short-link 2", "long-link 2");

        List<LinkEntity> linkEntities = Arrays.asList(firstLink, secondLink);
        List<LinkDto> linkDtos = Arrays.asList(new LinkDto(), new LinkDto());

        when(repository.findAll()).thenReturn(linkEntities);
        when(mapper.toDtos(linkEntities)).thenReturn(linkDtos);

        List<LinkDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get link by id")
    void shouldGetLinkByIdTest() throws LinkNotFoundException {
        String shortLink = "short-link";
        LinkEntity linkEntity = createLinkEntity(shortLink, "long-link");
        LinkDto linkDto = new LinkDto();

        when(repository.findByShortLink(shortLink)).thenReturn(Optional.of(linkEntity));
        when(mapper.toDto(linkEntity)).thenReturn(linkDto);

        LinkDto result = service.getById(shortLink);
        assertNotNull(result);
        assertEquals(linkDto, result);
        verify(repository, times(1)).findByShortLink(shortLink);
    }

    @Test
    @DisplayName("Should throw LinkNotFoundException when link is not found by id")
    void shouldThrowExceptionWhenLinkNotFoundByIdTest() {
        String shortLink = "short-link";

        when(repository.findByShortLink(shortLink)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.getById(shortLink));
        verify(repository, times(1)).findByShortLink(shortLink);
    }

    @Test
    @DisplayName("Should throw NullPointerException when shortLink is null")
    void getByIdThrowsNullPointerExceptionWhenShortLinkIsNull() {
        assertThrows(NullPointerException.class, () -> service.getById(null));
        verify(repository, never()).findById(anyString());
        verify(mapper, never()).toDto(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should delete link by id")
    void shouldDeleteLinkByIdTest() {
        String shortLink = "test-short-link";
    }

    private LinkEntity createLinkEntity(String shortLink, String longLink) {
        return LinkEntity.builder()
                .shortLink(shortLink)
                .longLink(longLink)
                .createAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .transitions(0)
                .build();
    }
}

