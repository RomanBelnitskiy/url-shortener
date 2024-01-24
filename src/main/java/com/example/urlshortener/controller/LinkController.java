package com.example.urlshortener.controller;

import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/link")
public class LinkController {
    private final LinkService linkService;
    private final LinkMapper linkMapper;

    public LinkController(LinkService linkService, LinkMapper linkMapper) {
        this.linkService = linkService;
        this.linkMapper = linkMapper;
    }

    @GetMapping
    public ResponseEntity<List<LinkResponse>> getAllLink(@RequestAttribute Long userId) {
        return ResponseEntity.ok(
                linkMapper.toResponses(
                        linkService.findAll(userId)
                ));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkResponse> getLinkByShortUrl(
            @PathVariable String shortUrl,
            @RequestAttribute Long userId
    ) {
        LinkDto linkDto = linkService.getByShortUrl(shortUrl, userId);
        return ResponseEntity.ok(linkMapper.toResponse(linkDto));
    }

    @GetMapping("/active")
    public ResponseEntity<List<LinkResponse>> getAllActiveLinks(@RequestAttribute Long userId){
        return ResponseEntity.ok(
                linkMapper.toResponses(
                        linkService.findAllActiveLinks(userId)
                ));
    }

    @PostMapping
    public ResponseEntity<LinkResponse> createLink(
            @RequestBody CreateLinkRequest linkRequest,
            @RequestAttribute Long userId
    ) {
        LinkDto linkDto = linkMapper.toDto(linkRequest);
        LinkResponse linkResponse = linkMapper.toResponse(linkService.create(linkDto, userId));
        return ResponseEntity.ok(linkResponse);
    }

    @PutMapping("/{shortUrl}")
    public void updateLink(
            @PathVariable String shortUrl,
            @RequestBody UpdateLinkRequest updateLinkRequest,
            @RequestAttribute Long userId
    ) {
        LinkDto linkDto = linkMapper.toDto(shortUrl, updateLinkRequest);
        linkService.update(linkDto, userId);
    }

    @DeleteMapping("/{shortUrl}")
    public void deleteLink(
            @PathVariable String shortUrl,
            @RequestAttribute Long userId
    ) {
        linkService.deleteByShortUrl(shortUrl, userId);
    }
}
