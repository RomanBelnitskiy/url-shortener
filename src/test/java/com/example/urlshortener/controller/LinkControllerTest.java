package com.example.urlshortener.controller;

import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @DisplayName("Should get all link")
    void getAllLink() throws Exception {
        List<LinkDto> linkDtos = getlinkDtos();
        List<LinkResponse> linkResponses = getLinkResponses();

        Mockito.when(linkService.findAll()).thenReturn(linkDtos);
        Mockito.when(linkMapper.toResponses(linkDtos)).thenReturn(linkResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/link"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should get link by valid id")
    void getLinkByShortUrl() throws Exception {
        String shortUrl = "example";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);

        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setShortUrl(shortUrl);
        linkResponse.setLongUrl(longUrl);

        Mockito.when(linkService.getByShortUrl(shortUrl)).thenReturn(linkDto);
        Mockito.when(linkMapper.toResponse(linkDto)).thenReturn(linkResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/link/{shortLink}", shortUrl))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.shortUrl").value(linkResponse.getShortUrl()))
                .andExpect(jsonPath("$.longUrl").value(linkResponse.getLongUrl()))
                .andExpect(jsonPath("$.createdAt").value(linkResponse.getCreatedAt()))
                .andExpect(jsonPath("$.expiredAt").value(linkResponse.getExpiredAt()))
                .andExpect(jsonPath("$.transitions").value(linkResponse.getTransitions()));

    }

    @Test
    @DisplayName("Should create link if not exists link")
    void createLink() throws Exception {
        CreateLinkRequest createLinkRequest = new CreateLinkRequest();
        createLinkRequest.setLongUrl("http://example.com");
        createLinkRequest.setExpiredAt(LocalDateTime.now());

        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setLongUrl("http://example.com");
        linkResponse.setExpiredAt(LocalDateTime.now());

        LinkDto createdLinkDto = linkMapper.toDto(createLinkRequest);
        Mockito.when(linkService.create(Mockito.any())).thenReturn(createdLinkDto);
        Mockito.when(linkMapper.toResponse(createdLinkDto)).thenReturn(linkResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLinkRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.shortUrl").value(linkResponse.getShortUrl()))
                .andExpect(jsonPath("$.longUrl").value(linkResponse.getLongUrl()));
    }

    @Test
    @DisplayName("Should update link by valid id")
    void updateLink() throws Exception {
        String shortLink = "example";
        UpdateLinkRequest linkRequest = new UpdateLinkRequest();
        linkRequest.setLongUrl("http://exampleUpdate.com");
        linkRequest.setExpiredAt(LocalDateTime.now().plusMinutes(1));

        LinkDto linkDto = linkMapper.toDto(shortLink, linkRequest);
        Mockito.doNothing().when(linkService).update(linkDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/link/{shortLink}", shortLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should delete link by valid id")
    void deleteLink() throws Exception {
        String shortUrl = "example";
        String longUrl = "http://example.com";

        LinkDto linkDto = new LinkDto();
        linkDto.setShortUrl(shortUrl);
        linkDto.setLongUrl(longUrl);

        Mockito.doNothing().when(linkService).deleteByShortUrl(shortUrl);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/link/{shortLink}", shortUrl))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
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
        LinkResponse linkResponse = new LinkResponse("example",
                "http://exemple.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(4),
                0L);

        LinkResponse linkResponse1 = new LinkResponse("example2",
                "http://exempleSecond.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(4),
                1L);
        return List.of(linkResponse, linkResponse1);
    }
}

