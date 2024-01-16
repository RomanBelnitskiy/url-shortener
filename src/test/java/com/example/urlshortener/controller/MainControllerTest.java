package com.example.urlshortener.controller;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
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
    private LinkService linkService;

    @Test
    void redirectToOriginalUrl_ShouldRedirectToOriginalUrl_WhenShortLinkExists() throws Exception {
        String shortLink = "abc123";
        String longLink = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortLink(shortLink);
        linkDto.setLongLink(longLink);

        Mockito.when(linkService.getById(shortLink)).thenReturn(linkDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/{shortLink}", shortLink))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(longLink));
    }
}
