package com.example.urlshortener.controller;

import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MainController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/{shortLink}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortLink) {
        try {
            LinkDto linkDto = linkService.getById(shortLink);
            return new RedirectView(linkDto.getLongLink());
        } catch (LinkNotFoundException e) {
            return new RedirectView("http://example.com");
        }
    }
}
