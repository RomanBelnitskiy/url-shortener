package com.example.urlshortener.controller;

import com.example.urlshortener.config.AuthFilter;
import com.example.urlshortener.exception.LinkExpiredException;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import com.example.urlshortener.validator.ShortUrlValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = MainController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)})
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkService linkService;
    @MockBean
    private ShortUrlValidator validator;
    @MockBean
    private CacheManager cacheManager;
    @MockBean
    private Cache cache;

    @Test
    @DisplayName("When short url is valid and link hasn't expired then does redirect")
    void redirect_ShouldRedirectToLongUrl_WhenShortUrlValidAndLinkNotExpired() throws Exception {
        String shortUrl = "abcd1234";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);
        linkDto.setCreatedAt(LocalDateTime.now());
        linkDto.setExpiredAt(LocalDateTime.now().plusMonths(1));

        when(validator.validate(shortUrl)).thenReturn(true);
        when(cacheManager.getCache("links")).thenReturn(cache);
        when(cache.get(shortUrl, LinkDto.class)).thenReturn(null);

        when(linkService.getByShortUrlAndIncreaseTransitions(shortUrl)).thenReturn(linkDto);

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(longUrl));
    }

    @Test
    @DisplayName("When short url is present in cache then does not call linkService")
    void redirect_DoesNotCallLinkService_WhenShortUrlIsPresentInCache() throws Exception {
        String shortUrl = "abcd1234";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);
        linkDto.setCreatedAt(LocalDateTime.now());
        linkDto.setExpiredAt(LocalDateTime.now().plusMonths(1));

        when(validator.validate(shortUrl)).thenReturn(true);
        when(cacheManager.getCache("links")).thenReturn(cache);
        when(cache.get(shortUrl, LinkDto.class)).thenReturn(linkDto);

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(longUrl));

        verify(linkService, never()).getByShortUrlAndIncreaseTransitions(anyString());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when short link is not valid")
    void redirect_ThrowsIllegalArgumentException_WhenShortLinkIsNotValid() throws Exception {
        String shortUrl = "abc123";

        when(validator.validate(shortUrl)).thenReturn(false);

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Invalid short url")));
    }

    @Test
    @DisplayName("Throws LinkNotFoundException when can not find link")
    void redirect_ThrowsLinkNotFoundException_WhenCanNotFindLink() throws Exception {
        String shortUrl = "abcd1234";

        when(validator.validate(shortUrl)).thenReturn(true);
        when(cacheManager.getCache("links")).thenReturn(cache);
        when(cache.get(shortUrl, LinkDto.class)).thenReturn(null);

        when(linkService.getByShortUrlAndIncreaseTransitions(shortUrl))
                .thenThrow(new LinkNotFoundException());

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is(404))
                .andExpect(content().string(containsString("Link not found.")));
    }

    @Test
    @DisplayName("Throws LinkExpiredException when link has expired")
    void redirect_ThrowsLinkExpiredException_WhenLinkHasExpired() throws Exception {
        String shortUrl = "abcd1234";

        when(validator.validate(shortUrl)).thenReturn(true);
        when(cacheManager.getCache("links")).thenReturn(cache);
        when(cache.get(shortUrl, LinkDto.class)).thenReturn(null);

        when(linkService.getByShortUrlAndIncreaseTransitions(anyString()))
                .thenThrow(new LinkExpiredException());

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is(410))
                .andExpect(content().string(containsString("The link has expired.")));
    }
}
