package com.example.urlshortener.controller.response;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.data.repository.LinkRepository;
import io.swagger.v3.oas.models.links.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private LinkRepository testRepository;

    @GetMapping("/{shortLink}")
    public RedirectView redirectToOriginalUrl (@PathVariable String shortLink) {
        Optional<LinkEntity> originalUrl = testRepository.findByShortLink(shortLink);

        if (originalUrl.isPresent()) {
            return new RedirectView(originalUrl.get().getLongLink());
        } else {
            return new RedirectView("/");
        }
    }
}
