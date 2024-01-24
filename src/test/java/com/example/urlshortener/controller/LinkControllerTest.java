package com.example.urlshortener.controller;

import com.example.urlshortener.config.AuthFilter;
import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = {LinkController.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)}
)
class LinkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    LinkService linkService;

    @MockBean
    LinkMapper linkMapper;

    final ObjectMapper objectMapper = new ObjectMapper();

    static String host;

    @BeforeAll
    static void staticInit() {
        host = "test";
    }

    @BeforeEach
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should get all link")
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void getAllLink() throws Exception {
        List<LinkDto> linkDtos = getlinkDtos();
        List<LinkResponse> linkResponses = getLinkResponses();

        when(linkService.findAll(1L)).thenReturn(linkDtos);
        when(linkMapper.toResponses(any(), any())).thenReturn(linkResponses);

        mockMvc.perform(get("/api/v1/link")
                        .with(csrf()).requestAttr("userId", 1L))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should get link by valid id")
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void getLinkByShortUrl() throws Exception {
        String shortUrl = "example";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);

        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setShortUrl(shortUrl);
        linkResponse.setLongUrl(longUrl);

        when(linkService.getByShortUrl(shortUrl, 1L)).thenReturn(linkDto);
        when(linkMapper.toResponse(any(), any())).thenReturn(linkResponse);

        mockMvc.perform(get("/api/v1/link/{shortLink}", shortUrl)
                        .with(csrf()).requestAttr("userId", 1L))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.shortUrl").value(linkResponse.getShortUrl()))
                .andExpect(jsonPath("$.longUrl").value(linkResponse.getLongUrl()))
                .andExpect(jsonPath("$.createdAt").value(linkResponse.getCreatedAt()))
                .andExpect(jsonPath("$.expiredAt").value(linkResponse.getExpiredAt()))
                .andExpect(jsonPath("$.transitions").value(linkResponse.getTransitions()));

    }

    @Test
    @DisplayName("Should create link if not exists link")
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void createLink() throws Exception {
        CreateLinkRequest createLinkRequest = new CreateLinkRequest();
        createLinkRequest.setLongUrl("http://example.com");

        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setFullShortUrl(host + "/" + "qwertyui");
        linkResponse.setShortUrl("qwertyui");
        linkResponse.setLongUrl("http://example222.com");

        when(linkMapper.toResponse(any(), any())).thenReturn(linkResponse);

        mockMvc.perform(post("/api/v1/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLinkRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .requestAttr("userId", 1L))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.fullShortUrl").value(linkResponse.getFullShortUrl()))
                .andExpect(jsonPath("$.shortUrl").value(linkResponse.getShortUrl()))
                .andExpect(jsonPath("$.longUrl").value(linkResponse.getLongUrl()));
    }

    @Test
    @DisplayName("Should update link by valid id")
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void updateLink() throws Exception {
        String shortLink = "example";
        UpdateLinkRequest linkRequest = new UpdateLinkRequest();
        linkRequest.setLongUrl("http://exampleUpdate.com");
        linkRequest.setExpiredAt(LocalDateTime.now().plusMinutes(1));

        LinkDto linkDto = linkMapper.toDto(shortLink, linkRequest);
        doNothing().when(linkService).update(linkDto, 1L);

        mockMvc.perform(put("/api/v1/link/{shortLink}", shortLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .requestAttr("userId", 1L))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should delete link by valid id")
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void deleteLink() throws Exception {
        String shortUrl = "example";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);

        doNothing().when(linkService).deleteByShortUrl(shortUrl, 1L);

        mockMvc.perform(delete("/api/v1/link/{shortLink}", shortUrl)
                        .with(csrf())
                        .requestAttr("userId", 1L))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }


    public List<LinkDto> getlinkDtos() {
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

        return List.of(linkDto, linkDtoSecond);
    }

    public List<LinkResponse> getLinkResponses() {
        LinkResponse linkResponse = new LinkResponse(host,
                "example",
                "http://exemple.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(4),
                0L);

        LinkResponse linkResponse1 = new LinkResponse(host,
                "example2",
                "http://exempleSecond.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(4),
                1L);
        return List.of(linkResponse, linkResponse1);
    }
}

