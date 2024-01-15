package com.example.urlshortener.controller;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@WebMvcTest(MainController.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkRepository linkRepository;

    @Test
    void redirectToOriginalUrl_ShouldRedirectToOriginalUrl_WhenShortLinkExists() throws Exception {
        String shortLink = "abc123";
        String longLink = "http://example.com";

        LinkEntity linkEntity = new LinkEntity();
        linkEntity.setShortLink(shortLink);
        linkEntity.setLongLink(longLink);

        Mockito.when(linkRepository.findByShortLink(shortLink)).thenReturn(Optional.of(linkEntity));

        mockMvc.perform(MockMvcRequestBuilders.get("/{shortLink}", shortLink))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(longLink));
    }
}
