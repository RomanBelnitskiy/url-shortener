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
    public ResponseEntity<List<LinkResponse>> getAllLink() {
        return ResponseEntity.ok(
                linkMapper.toResponses(
                        linkService.findAll()
                ));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkResponse> getLinkByShortLink(@PathVariable String shortUrl) {
        LinkDto linkDto = linkService.getByShortUrl(shortUrl);
        return ResponseEntity.ok(linkMapper.toResponse(linkDto));
    }

    @PostMapping
    public ResponseEntity<LinkResponse> createLink(@RequestBody CreateLinkRequest linkRequest) {
        LinkDto linkDto = linkMapper.toDto(linkRequest);
        LinkResponse linkResponse = linkMapper.toResponse(linkService.create(linkDto));
        return ResponseEntity.ok(linkResponse);
    }

    @PutMapping("/{shortUrl}")
    public void updateLink(@PathVariable String shortUrl, @RequestBody UpdateLinkRequest updateLinkRequest) {
        LinkDto linkDto = linkMapper.toDto(shortUrl, updateLinkRequest);
        linkService.update(linkDto);
    }

    @DeleteMapping("/{shortUrl}")
    public void deleteLink(@PathVariable String shortUrl) {
        linkService.deleteByShortUrl(shortUrl);
    }
}
