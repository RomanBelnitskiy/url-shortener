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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static org.springframework.http.HttpStatus.*;

@Controller
public class MainController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/{shortUrl}")
    public RedirectView redirect(@PathVariable String shortUrl) {
            LinkDto linkDto = linkService.getByShortUrlAndIncreaseTransitions(shortUrl);

            return new RedirectView(linkDto.getLongUrl());
    }

    @ExceptionHandler(LinkNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ModelAndView notFoundExceptionHandler(Exception ex) {
        return getModelAndView(ex, NOT_FOUND.value());
    }

    @ExceptionHandler(LinkExpiredException.class)
    @ResponseStatus(GONE)
    public ModelAndView expiredExceptionHandler(Exception ex) {
        return getModelAndView(ex, GONE.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ModelAndView illegalArgumentExceptionHandler(Exception ex) {
        return getModelAndView(ex, BAD_REQUEST.value());
    }

    private ModelAndView getModelAndView(Exception ex, int statusCode) {
        ModelAndView model = new ModelAndView("error");
        model.addObject("statusCode", statusCode);
        model.addObject("message", ex.getLocalizedMessage());

        return model;
    }
}
