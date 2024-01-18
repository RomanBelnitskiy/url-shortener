package com.example.urlshortener.controller;

import com.example.urlshortener.exception.LinkExpiredException;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MainController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkService linkService;

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

        when(linkService.getByShortUrlAndIncreaseTransitions(shortUrl)).thenReturn(linkDto);

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(longUrl));
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when short link is not valid")
    void redirect_ThrowsIllegalArgumentException_WhenShortLinkIsNotValid() throws Exception {
        String shortUrl = "abc123";

        when(linkService.getByShortUrlAndIncreaseTransitions(shortUrl))
                .thenThrow(new IllegalArgumentException("Invalid short url"));

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Invalid short url")));
    }

    @Test
    @DisplayName("Throws LinkNotFoundException when can not find link")
    void redirect_ThrowsLinkNotFoundException_WhenCanNotFindLink() throws Exception {
        String shortUrl = "abcd1234";

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

        when(linkService.getByShortUrlAndIncreaseTransitions(anyString()))
                .thenThrow(new LinkExpiredException());

        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is(410))
                .andExpect(content().string(containsString("The link has expired.")));
    }
}
