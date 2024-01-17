package com.example.urlshortener.controller;

import com.example.urlshortener.exception.LinkExpiredException;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MainController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/{shortUrl}")
    public RedirectView redirect(@PathVariable String shortUrl) {
            LinkDto linkDto = linkService.getByShortUrlAndIncreaseTransitions(shortUrl);

            return new RedirectView(linkDto.getLongUrl());
    }

    @ExceptionHandler({
            LinkNotFoundException.class,
            LinkExpiredException.class,
            IllegalArgumentException.class
    })
    public ModelAndView exceptionHandler(Exception ex) {
        ModelAndView model = new ModelAndView("error");
        model.addObject("message", ex.getLocalizedMessage());

        return model;
    }
}
