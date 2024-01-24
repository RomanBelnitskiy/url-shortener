package com.example.urlshortener.controller;

import com.example.urlshortener.exception.LinkExpiredException;
import com.example.urlshortener.exception.LinkNotFoundException;
import com.example.urlshortener.service.dto.LinkDto;
import com.example.urlshortener.service.service.LinkService;
import com.example.urlshortener.validator.ShortUrlValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.*;

@Controller
@Slf4j
public class MainController {
    @Autowired
    private LinkService linkService;
    @Autowired
    private ShortUrlValidator shortUrlValidator;
    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/{shortUrl}")
    public RedirectView redirect(@PathVariable String shortUrl) {
        if (!shortUrlValidator.validate(shortUrl)) {
            throw new IllegalArgumentException("Invalid short url");
        }

        try {
            LinkDto link = attemptGetLinkFromCache(shortUrl);
            if (link != null) {
                updateTransitions(shortUrl);
                return new RedirectView(link.getLongUrl());
            }
        } catch (NullPointerException e) {
            log.info(e.getLocalizedMessage());
        }

        LinkDto linkDto = linkService.getByShortUrlAndIncreaseTransitions(shortUrl);

        return new RedirectView(linkDto.getLongUrl());
    }

    private LinkDto attemptGetLinkFromCache(String shortUrl) {
        Cache linksCache = cacheManager.getCache("links");
        return requireNonNull(linksCache).get(shortUrl, LinkDto.class);
    }

    private void updateTransitions(String shortUrl) {
        Cache transitionsCache = cacheManager.getCache("transitions");
        Long transitions = requireNonNull(transitionsCache).get(shortUrl, Long.class);
        if (transitions != null) {
            transitions++;
            transitionsCache.put(shortUrl, transitions);
        } else {
            transitionsCache.put(shortUrl, 1L);
        }
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
