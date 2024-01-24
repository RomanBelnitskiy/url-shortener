package com.example.urlshortener.service;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.data.repository.UserRepository;
import com.example.urlshortener.exception.LinkExpiredException;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.generator.Generator;
import com.example.urlshortener.service.service.impl.LinkServiceImpl;
import com.example.urlshortener.validator.LongUrlValidator;
import com.example.urlshortener.validator.ShortUrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void initService() {
        service = new LinkServiceImpl(
                repository,
                mapper,
                shortUrlValidator,
                longUrlValidator,
                generator,
                userRepository
        );
    }

    @Test
    @DisplayName("Should call repository.findAll() one time")
    void shouldCallRepositoryFindAllOneTimeTest() {
        when(repository.findAll(1L)).thenReturn(Collections.emptyList());

        service.findAll(1L);
        verify(repository, times(1)).findAll(1L);
    }

    @Test
    @DisplayName("Should call repository.findByActiveLinks() one time")
    void shouldCallRepositoryFindByActiveLinksOneTimeTest() {
        when(repository.findByActiveLinks(1L)).thenReturn(Collections.emptyList());

        service.findAllActiveLinks(1L);

        verify(repository, times(1)).findByActiveLinks(1L);
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

        when(longUrlValidator.validate(anyString())).thenReturn(true);
        when(generator.generateShortUrl()).thenReturn(expectedUrl);
        when(shortUrlValidator.validate(anyString())).thenReturn(true);
        when(repository.existsByShortUrl(anyString())).thenReturn(false);
        when(repository.save(any(LinkEntity.class))).then(invocation -> invocation.getArgument(0));

        LinkDto result = service.create(inputDto, 1L);

        assertEquals(expectedDto, result);
        verify(repository, times(1)).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid long link when create")
    void shouldThrowIllegalArgumentException_ForInvalidLongLinkTest() {
        LinkDto invalidLongUrl = createLinkDto(null, "https://invalid-long-link.com");

        when(longUrlValidator.validate(invalidLongUrl.getLongUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(invalidLongUrl, 1L));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid generated short link")
    void shouldThrowIllegalArgumentException_ForInvalidGeneratedShortLinkTest() {
        LinkDto inputDto = createLinkDto(null, "https://example.com");
        LinkEntity expectedEntity = mapper.toEntity(inputDto);

        when(longUrlValidator.validate(expectedEntity.getLongUrl())).thenReturn(true);
        when(shortUrlValidator.validate(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(inputDto, 1L));
        verify(repository, never()).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for existing short link")
    void shouldThrowIllegalArgumentException_ForExistingShortLinkTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");
        LinkEntity expectedEntity = mapper.toEntity(inputDto);

        when(longUrlValidator.validate(expectedEntity.getLongUrl())).thenReturn(true);
        when(shortUrlValidator.validate(expectedEntity.getShortUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(inputDto, 1L));
        verify(repository, never()).existsByShortUrl(expectedEntity.getShortUrl());
        verify(repository, never()).save(any(LinkEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid short link when update")
    void shouldThrowIllegalArgumentException_ForShortLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.update(inputDto, 1L));
    }

    @Test
    @DisplayName("Should throw LinkNotFoundException for non-existing id")
    void shouldThrowLinkNotFoundException_ForShortLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(true);
        when(repository.findByShortUrl(inputDto.getShortUrl(), 1L)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.update(inputDto, 1L));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid long link when update")
    void shouldThrowIllegalArgumentException_ForLongLink_WhenUpdateTest() {
        LinkDto inputDto = createLinkDto("abc123", "https://example.com");
        LinkEntity expectedEntity = createLinkEntity("abc123", "https://invalid-link.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(true);
        when(repository.findByShortUrl(inputDto.getShortUrl(), 1L)).thenReturn(Optional.of(expectedEntity));
        when(longUrlValidator.validate(expectedEntity.getLongUrl())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.update(inputDto, 1L));
    }

    @Test
    @DisplayName("Should update longLink when update")
    void shouldUpdateLongLinkWhenUpdateTest() {
        String shortLink = "abc123";
        LinkDto inputDto = createLinkDto(shortLink, "https://example.com");
        String newLongUrl = "https://new-link.com";
        LinkEntity originalEntity = createLinkEntity(shortLink, "https://example.com");

        when(shortUrlValidator.validate(inputDto.getShortUrl())).thenReturn(true);
        when(repository.findByShortUrl(inputDto.getShortUrl(), 1L)).thenReturn(Optional.of(originalEntity));
        when(longUrlValidator.validate(newLongUrl)).thenReturn(true);

        inputDto.setLongUrl(newLongUrl);
        service.update(inputDto, 1L);

        verify(repository, times(1)).findByShortUrl(shortLink, 1L);
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
        when(repository.findByShortUrl(inputDto.getShortUrl(), 1L)).thenReturn(Optional.of(originalEntity));

        inputDto.setExpiredAt(newExpiredAt);
        service.update(inputDto, 1L);

        verify(repository, times(1)).findByShortUrl(shortUrl, 1L);

        assertEquals(newExpiredAt, originalEntity.getExpiredAt());
    }

    @Test
    @DisplayName("Should delete link by valid id")
    void shouldDeleteLinkByValidIdTest() {
        String validShortUrl = "abc123";

        when(shortUrlValidator.validate(validShortUrl)).thenReturn(true);
        service.deleteByShortUrl(validShortUrl, 1L);

        verify(repository, times(1)).deleteByShortUrl(validShortUrl, 1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid id when deleteById")
    void shouldThrowExceptionForInvalidIdTest() {
        String invalidShortUrl = "bbb5555";

        assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteByShortUrl(invalidShortUrl, 1L)
        );
    }

    @Test
    @DisplayName("Should return link when a valid id is provided")
    void shouldReturnLinkDtoForValidIdTest() {
        String validShortUrl = "abc123";
        LinkEntity mockEntity = createLinkEntity(validShortUrl, "https://example.com");

        when(shortUrlValidator.validate(validShortUrl)).thenReturn(true);
        when(repository.findByShortUrl(validShortUrl, 1L)).thenReturn(Optional.of(mockEntity));

        LinkDto resultDto = service.getByShortUrl(validShortUrl, 1L);

        assertEquals(validShortUrl, resultDto.getShortUrl());
    }

    @Test
    @DisplayName("Should throw LinkNotFoundException for non-existing id")
    void shouldThrowLinkNotFoundException_ForNonExistingIdTest() {
        String nonExistingShortUrl = "aaa666";

        when(shortUrlValidator.validate(nonExistingShortUrl)).thenReturn(true);
        when(repository.findByShortUrl(nonExistingShortUrl, 1L)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.getByShortUrl(nonExistingShortUrl, 1L));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid id")
    void shouldThrowIllegalArgumentException_ForInvalidIdTest() {
        String invalidShortUrl = "aaa666";

        when(shortUrlValidator.validate(invalidShortUrl)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.getByShortUrl(invalidShortUrl, 1L));
        verify(repository, never()).findByShortUrl(any());
    }

    @Nested
    @DisplayName("getByShortUrlAndIncreaseTransitions tests")
    class GetByShortUrlAndIncreaseTransitionsTests {
        @Test
        @DisplayName("Should return LinkDto with increased transitions")
        void shouldReturnLinkDtoWithIncreasedTransitions() {
            LinkDto linkDto = createLinkDto("aaaabbbb", "https://www.google.com/");
            long initTransitions = linkDto.getTransitions();

            when(repository.findByShortUrl(linkDto.getShortUrl()))
                    .thenReturn(Optional.of(mapDtoToEntity(linkDto)));

            LinkDto resultDto = service.getByShortUrlAndIncreaseTransitions(linkDto.getShortUrl());

            assertEquals(initTransitions + 1, resultDto.getTransitions());
        }

        @Test
        @DisplayName("Should throws LinkNotFoundException")
        void shouldThrowsLinkNotFoundException() {
            LinkDto linkDto = createLinkDto("aaaabbbb", "https://www.google.com/");

            when(repository.findByShortUrl(linkDto.getShortUrl()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    LinkNotFoundException.class,
                    () -> service.getByShortUrlAndIncreaseTransitions(linkDto.getShortUrl()));
        }

        @Test
        @DisplayName("Should throws LinkExpiredException")
        void shouldThrowsLinkExpiredException() {
            LinkDto linkDto = createLinkDto("aaaabbbb", "https://www.google.com/");
            linkDto.setExpiredAt(LocalDateTime.now().minusMonths(1));

            when(repository.findByShortUrl(linkDto.getShortUrl()))
                    .thenReturn(Optional.of(mapDtoToEntity(linkDto)));

            assertThrows(
                    LinkExpiredException.class,
                    () -> service.getByShortUrlAndIncreaseTransitions(linkDto.getShortUrl()));
        }
    }

    @Test
    @DisplayName("Should call repository.increaseTransitions()")
    void updateTransitions_CallIncreaseTransitions() {
        String shortUrl = "aaaabbbb";

        service.updateTransitions(shortUrl);

        verify(repository, times(1)).increaseTransitions(shortUrl);
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

    private LinkEntity mapDtoToEntity(LinkDto dto) {
        return LinkEntity.builder()
                .shortUrl(dto.getShortUrl())
                .longUrl(dto.getLongUrl())
                .createdAt(dto.getCreatedAt())
                .expiredAt(dto.getExpiredAt())
                .transitions(dto.getTransitions())
                .build();
    }
}