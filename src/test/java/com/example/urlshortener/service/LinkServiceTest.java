package com.example.urlshortener.service;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.generator.Generator;
import com.example.urlshortener.service.service.impl.LinkServiceImpl;
import com.example.urlshortener.validator.LongLinkValidator;
import com.example.urlshortener.validator.ShortLinkValidator;
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
    private final ShortLinkValidator shortLinkValidator = Mockito.mock(ShortLinkValidator.class);
    private final LongLinkValidator longLinkValidator = Mockito.mock(LongLinkValidator.class);
    private final Generator generator = Mockito.mock(Generator.class);

    @BeforeEach
    void initService() {
        service = new LinkServiceImpl(repository, mapper, shortLinkValidator, longLinkValidator, generator);
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
        String expectedLink = "abc1244";
        LinkDto expectedDto = LinkDto.builder()
                .shortLink(expectedLink)
                .createdAt(inputDto.getCreatedAt())
                .longLink(inputDto.getLongLink())
                .expiredAt(inputDto.getExpiredAt())
                .transitions(inputDto.getTransitions())
                .build();
        LinkEntity expectedEntity = mapper.toEntity(expectedDto);

        when(longLinkValidator.validate(anyString())).thenReturn(true);
        when(generator.generateShortLink()).thenReturn(expectedLink);
        when(shortLinkValidator.validate(anyString())).thenReturn(true);
        when(repository.existsByShortLink(anyString())).thenReturn(false);
        when(repository.save(any(LinkEntity.class))).then(invocation -> invocation.getArgument(0));

        LinkDto result = service.create(inputDto);

        assertEquals(expectedDto, result);
        verify(repository, times(1)).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid long link when create")
    void shouldThrowIllegalArgumentException_ForInvalidLongLinkTest() {
        LinkDto invalidLongLink = createLinkDto(null, "https://invalid-long-link.com");

        when(longLinkValidator.validate(invalidLongLink.getLongLink())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(invalidLongLink));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid generated short link")
    void shouldThrowIllegalArgumentException_ForInvalidGeneratedShortLinkTest() {
        LinkDto inputDto = createLinkDto(null, "https://example.com");
        LinkEntity expectedEntity = mapper.toEntity(inputDto);

        when(longLinkValidator.validate(expectedEntity.getLongLink())).thenReturn(true);
        when(shortLinkValidator.validate(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(inputDto));
        verify(repository, never()).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for existing short link")
    void shouldThrowIllegalArgumentException_ForExistingShortLinkTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");
        LinkEntity expectedEntity = mapper.toEntity(inputDto);

        when(longLinkValidator.validate(expectedEntity.getLongLink())).thenReturn(true);
        when(shortLinkValidator.validate(expectedEntity.getShortLink())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(inputDto));
        verify(repository, never()).existsByShortLink(expectedEntity.getShortLink());
        verify(repository, never()).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid short link when update")
    void shouldThrowIllegalArgumentException_ForShortLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");

        when(shortLinkValidator.validate(inputDto.getShortLink())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.update(inputDto));
    }

    @Test
    @DisplayName("Should throw LinkNotFoundException for non-existing id")
    void shouldThrowLinkNotFoundException_ForShortLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");

        when(shortLinkValidator.validate(inputDto.getShortLink())).thenReturn(true);
        when(repository.findByShortLink(inputDto.getShortLink())).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.update(inputDto));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid long link when update")
    void shouldThrowIllegalArgumentException_ForLongLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");
        LinkEntity expectedEntity = createLinkEntity("abc123", "https://invalid-link.com");

        when(shortLinkValidator.validate(inputDto.getShortLink())).thenReturn(true);
        when(repository.findByShortLink(inputDto.getShortLink())).thenReturn(Optional.of(expectedEntity));
        when(longLinkValidator.validate(expectedEntity.getLongLink())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.update(inputDto));
    }

    @Test
    @DisplayName("Should update longLink when update")
    void shouldUpdateLongLinkWhenUpdateTest() {
        String shortLink = "abc123";
        LinkDto inputDto = createLinkDto(shortLink, "https://example.com");
        String newLongLink = "https://new-link.com";
        LinkEntity originalEntity = createLinkEntity(shortLink, "https://example.com");

        when(shortLinkValidator.validate(inputDto.getShortLink())).thenReturn(true);
        when(repository.findByShortLink(inputDto.getShortLink())).thenReturn(Optional.of(originalEntity));
        when(longLinkValidator.validate(newLongLink)).thenReturn(true);

        inputDto.setLongLink(newLongLink);
        service.update(inputDto);

        verify(repository, times(1)).findByShortLink(shortLink);
        assertEquals(newLongLink, originalEntity.getLongLink());
    }

    @Test
    @DisplayName("Should update expiredAt when update")
    void shouldUpdateExpiredAt_WhenUpdateTest() {
        String shortLink = "abc123";
        LinkDto inputDto = createLinkDto(shortLink, "https://example.com");
        LocalDateTime newExpiredAt = LocalDateTime.now().plusDays(5);
        LinkEntity originalEntity = createLinkEntity(shortLink, "https://example.com");

        when(shortLinkValidator.validate(inputDto.getShortLink())).thenReturn(true);
        when(repository.findByShortLink(inputDto.getShortLink())).thenReturn(Optional.of(originalEntity));

        inputDto.setExpiredAt(newExpiredAt);
        service.update(inputDto);

        verify(repository, times(1)).findByShortLink(shortLink);

        assertEquals(newExpiredAt, originalEntity.getExpiredAt());
    }

    @Test
    @DisplayName("Should delete link by valid id")
    void shouldDeleteLinkByValidIdTest() {
        String validLinkId = "abc123";

        when(shortLinkValidator.validate(validLinkId)).thenReturn(true);
        service.deleteById(validLinkId);

        verify(repository, times(1)).deleteById(validLinkId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid id when deleteById")
    void shouldThrowExceptionForInvalidIdTest() {
        String invalidLinkId = "bbb5555";

        assertThrows(IllegalArgumentException.class, () -> service.deleteById(invalidLinkId));
    }

    @Test
    @DisplayName("Should return link when a valid id is provided")
    void shouldReturnLinkDtoForValidIdTest() {
        String validLinkId = "abc123";
        LinkEntity mockEntity = createLinkEntity(validLinkId, "https://example.com");

        when(shortLinkValidator.validate(validLinkId)).thenReturn(true);
        when(repository.findByShortLink(validLinkId)).thenReturn(Optional.of(mockEntity));

        LinkDto resultDto = service.getById(validLinkId);

        assertEquals(validLinkId, resultDto.getShortLink());
    }

    @Test
    @DisplayName("Should throw LinkNotFoundException for non-existing id")
    void shouldThrowLinkNotFoundException_ForNonExistingIdTest() {
        String nonExistingLinkId = "aaa666";

        when(shortLinkValidator.validate(nonExistingLinkId)).thenReturn(true);
        when(repository.findByShortLink(nonExistingLinkId)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.getById(nonExistingLinkId));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid id")
    void shouldThrowIllegalArgumentException_ForInvalidIdTest() {
        String invalidLinkId = "aaa666";

        when(shortLinkValidator.validate(invalidLinkId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.getById(invalidLinkId));
        verify(repository, never()).findByShortLink(any());
    }


    private LinkDto createLinkDto(String shortLink, String longLink) {
        return LinkDto.builder()
                .shortLink(shortLink)
                .longLink(longLink)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .transitions(0)
                .build();
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