package com.example.urlshortener.controller;

import com.example.urlshortener.controller.request.CreateLinkRequest;
import com.example.urlshortener.controller.request.UpdateLinkRequest;
import com.example.urlshortener.controller.response.LinkResponse;
import com.example.urlshortener.mapper.LinkMapper;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/link")
public class LinkController {
    private final LinkService linkService;
    private final LinkMapper linkMapper;

    @Autowired
    public LinkController(LinkService linkService, LinkMapper linkMapper) {
        this.linkService = linkService;
        this.linkMapper = linkMapper;
    }

    @GetMapping("/list")
    public ResponseEntity<List<LinkResponse>> getAllLink() {
        return ResponseEntity.ok(linkMapper.toResponses(linkService.findAll()));
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<LinkResponse> getLinkByShortLink(@PathVariable(value = "shortLink") String shortLink) {
        LinkDto linkDto = linkService.getById(shortLink);
        return ResponseEntity.ok(linkMapper.toResponse(linkDto));
    }

    @PostMapping("/add")
    public ResponseEntity<LinkResponse> createLink(@RequestBody CreateLinkRequest linkRequest) {
        LinkDto linkDto = linkMapper.toDto(linkRequest);
        return ResponseEntity.ok(linkMapper.toResponse(linkService.create(linkDto)));
    }

    @PutMapping("/{shortLink}")
    public void updateLink(@PathVariable(value = "shortLink") String shortLink, @RequestBody UpdateLinkRequest updateLinkRequest) {
        LinkDto linkDto = linkMapper.toLinkDto(shortLink, updateLinkRequest);
        linkService.update(linkDto);
    }

    @DeleteMapping("/{shortLink}")
    public void deleteLink(@PathVariable(value = "shortLink") String shortLink) {
        linkService.deleteById(shortLink);
    }
}
