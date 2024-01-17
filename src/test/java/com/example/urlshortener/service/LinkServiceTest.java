package com.example.urlshortener.service;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.generator.Generator;
import com.example.urlshortener.service.service.impl.LinkServiceImpl;
import com.example.urlshortener.validator.LongUrlValidator;
import com.example.urlshortener.validator.ShortUrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {
    @Mock
    private LinkRepository repository;
    private LinkServiceImpl service;
    private final LinkMapper mapper = new LinkMapper();
    private final ShortUrlValidator shortUrlValidator = Mockito.mock(ShortUrlValidator.class);
    private final LongUrlValidator longUrlValidator = Mockito.mock(LongUrlValidator.class);
    private final Generator generator = Mockito.mock(Generator.class);

    @BeforeEach
    void initService() {
        service = new LinkServiceImpl(repository, mapper, shortUrlValidator, longUrlValidator, generator);
    }

    @Test
    @DisplayName("Should call repository.findAll() one time")
    void shouldCallRepositoryFindAllOneTimeTest() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        service.findAll();
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create and return LinkDto for valid input")
    void shouldCreate_And_ReturnLinkDto_ForValidInputTest() {
        LinkDto inputDto = createLinkDto(null, "https://example.com");
        String expectedUrl = "abc1244";
        LinkDto expectedDto = LinkDto.builder()
                .shortUrl(expectedUrl)
                .createdAt(inputDto.getCreatedAt())
                .longUrl(inputDto.getLongUrl())
                .expiredAt(inputDto.getExpiredAt())
                .transitions(inputDto.getTransitions())
                .build();
        LinkEntity expectedEntity = mapper.toEntity(expectedDto);

        when(longUrlValidator.validate(anyString())).thenReturn(true);
        when(generator.generateShortUrl()).thenReturn(expectedUrl);
        when(shortUrlValidator.validate(anyString())).thenReturn(true);
        when(repository.existsByShortUrl(anyString())).thenReturn(false);
        when(repository.save(any(LinkEntity.class))).then(invocation -> invocation.getArgument(0));

        LinkDto result = service.create(inputDto);

        assertEquals(expectedDto, result);
        verify(repository, times(1)).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid long link when create")
    void shouldThrowIllegalArgumentException_ForInvalidLongLinkTest() {
        LinkDto invalidLongUrl = createLinkDto(null, "https://invalid-long-link.com");

        when(longUrlValidator.validate(invalidLongUrl.getLongUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(invalidLongUrl));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid generated short link")
    void shouldThrowIllegalArgumentException_ForInvalidGeneratedShortLinkTest() {
        LinkDto inputDto = createLinkDto(null, "https://example.com");
        LinkEntity expectedEntity = mapper.toEntity(inputDto);

        when(longUrlValidator.validate(expectedEntity.getLongUrl())).thenReturn(true);
        when(shortUrlValidator.validate(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(inputDto));
        verify(repository, never()).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for existing short link")
    void shouldThrowIllegalArgumentException_ForExistingShortLinkTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");
        LinkEntity expectedEntity = mapper.toEntity(inputDto);

        when(longUrlValidator.validate(expectedEntity.getLongUrl())).thenReturn(true);
        when(shortUrlValidator.validate(expectedEntity.getShortUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(inputDto));
        verify(repository, never()).existsByShortUrl(expectedEntity.getShortUrl());
        verify(repository, never()).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid short link when update")
    void shouldThrowIllegalArgumentException_ForShortLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.update(inputDto));
    }

    @Test
    @DisplayName("Should throw LinkNotFoundException for non-existing id")
    void shouldThrowLinkNotFoundException_ForShortLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(true);
        when(repository.findByShortUrl(inputDto.getShortUrl())).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.update(inputDto));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid long link when update")
    void shouldThrowIllegalArgumentException_ForLongLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");
        LinkEntity expectedEntity = createLinkEntity("abc123", "https://invalid-link.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(true);
        when(repository.findByShortUrl(inputDto.getShortUrl())).thenReturn(Optional.of(expectedEntity));
        when(longUrlValidator.validate(expectedEntity.getLongUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.update(inputDto));
    }

    @Test
    @DisplayName("Should update longLink when update")
    void shouldUpdateLongLinkWhenUpdateTest() {
        String shortLink = "abc123";
        LinkDto inputDto = createLinkDto(shortLink, "https://example.com");
        String newLongUrl = "https://new-link.com";
        LinkEntity originalEntity = createLinkEntity(shortLink, "https://example.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(true);
        when(repository.findByShortUrl(inputDto.getShortUrl())).thenReturn(Optional.of(originalEntity));
        when(longUrlValidator.validate(newLongUrl)).thenReturn(true);

        inputDto.setLongUrl(newLongUrl);
        service.update(inputDto);

        verify(repository, times(1)).findByShortUrl(shortLink);
        assertEquals(newLongUrl, originalEntity.getLongUrl());
    }

    @Test
    @DisplayName("Should update expiredAt when update")
    void shouldUpdateExpiredAt_WhenUpdateTest() {
        String shortUrl = "abc123";
        LinkDto inputDto = createLinkDto(shortUrl, "https://example.com");
        LocalDateTime newExpiredAt = LocalDateTime.now().plusDays(5);
        LinkEntity originalEntity = createLinkEntity(shortUrl, "https://example.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(true);
        when(repository.findByShortUrl(inputDto.getShortUrl())).thenReturn(Optional.of(originalEntity));

        inputDto.setExpiredAt(newExpiredAt);
        service.update(inputDto);

        verify(repository, times(1)).findByShortUrl(shortUrl);

        assertEquals(newExpiredAt, originalEntity.getExpiredAt());
    }

    @Test
    @DisplayName("Should delete link by valid id")
    void shouldDeleteLinkByValidIdTest() {
        String validUrlId = "abc123";

        when(shortUrlValidator.validate(validUrlId)).thenReturn(true);
        service.deleteByShortUrl(validUrlId);

        verify(repository, times(1)).deleteById(validUrlId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid id when deleteById")
    void shouldThrowExceptionForInvalidIdTest() {
        String invalidUrlId = "bbb5555";

        assertThrows(IllegalArgumentException.class, () -> service.deleteByShortUrl(invalidUrlId));
    }

    @Test
    @DisplayName("Should return link when a valid id is provided")
    void shouldReturnLinkDtoForValidIdTest() {
        String validUrlId = "abc123";
        LinkEntity mockEntity = createLinkEntity(validUrlId, "https://example.com");

        when(shortUrlValidator.validate(validUrlId)).thenReturn(true);
        when(repository.findByShortUrl(validUrlId)).thenReturn(Optional.of(mockEntity));

        LinkDto resultDto = service.getByShortUrl(validUrlId);

        assertEquals(validUrlId, resultDto.getShortUrl());
    }

    @Test
    @DisplayName("Should throw LinkNotFoundException for non-existing id")
    void shouldThrowLinkNotFoundException_ForNonExistingIdTest() {
        String nonExistingUrlId = "aaa666";

        when(shortUrlValidator.validate(nonExistingUrlId)).thenReturn(true);
        when(repository.findByShortUrl(nonExistingUrlId)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.getByShortUrl(nonExistingUrlId));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid id")
    void shouldThrowIllegalArgumentException_ForInvalidIdTest() {
        String invalidUrlId = "aaa666";

        when(shortUrlValidator.validate(invalidUrlId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.getByShortUrl(invalidUrlId));
        verify(repository, never()).findByShortUrl(any());
    }


    private LinkDto createLinkDto(String shortUrl, String longUrl) {
        return LinkDto.builder()
                .shortUrl(shortUrl)
                .longUrl(longUrl)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .transitions(0)
                .build();
    }

    private LinkEntity createLinkEntity(String shortUrl, String longUrl) {
        return LinkEntity.builder()
                .shortUrl(shortUrl)
                .longUrl(longUrl)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .transitions(0)
                .build();
    }
}