package com.example.urlshortener.controller;

import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import com.example.urlshortener.service.service.impl.LinkServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = {LinkController.class})
class LinkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LinkService linkService;

    @MockBean
    LinkMapper linkMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllLink() throws Exception {
        LinkDto linkDto = new LinkDto("example",
                "http://exemple.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(4),
                0);

        LinkDto linkDtoSecond = new LinkDto("example2",
                "http://exempleSecond.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(4),
                1);

        List<LinkDto> linkEntities = List.of(linkDto, linkDtoSecond);
        List<LinkResponse> linkResponses = linkMapper.toResponses(linkEntities);

        Mockito.when(linkService.findAll()).thenReturn(linkEntities);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/link"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.size()", Matchers.is(linkResponses.size())));
    }

    @Test
    void getLinkByShortUrl() throws Exception {
        String shortUrl = "example";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);

        Mockito.when(linkService.getByShortUrl(shortUrl)).thenReturn(linkDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/link/{shortLink}", shortUrl))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }

    @Test
    void createLink() throws Exception {
        CreateLinkRequest createLinkRequest = new CreateLinkRequest();
        createLinkRequest.setLongUrl("http://example.com");
        createLinkRequest.setExpiredAt(LocalDateTime.now().plusMinutes(1));

        LinkDto createdLinkDto = linkMapper.toDto(createLinkRequest);
        Mockito.when(linkService.create(Mockito.any())).thenReturn(createdLinkDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLinkRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void updateLink() throws Exception {
        String shortLink = "example";
        UpdateLinkRequest linkRequest = new UpdateLinkRequest();
        linkRequest.setLongUrl("http://example.com");
        linkRequest.setExpiredAt(LocalDateTime.now().plusMinutes(1));

        LinkDto linkDto = linkMapper.toDto(shortLink, linkRequest);
        Mockito.doNothing().when(linkService).update(linkDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/link/{shortLink}", shortLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void deleteLink() throws Exception {
        String shortUrl = "example";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);

        Mockito.doNothing().when(linkService).deleteByShortUrl(shortUrl);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/link/{shortLink}", shortUrl))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
}