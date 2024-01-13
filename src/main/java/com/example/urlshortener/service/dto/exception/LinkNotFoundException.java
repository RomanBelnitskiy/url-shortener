package com.example.urlshortener.service.dto.exception;

import com.example.urlshortener.service.dto.LinkDto;

public class LinkNotFoundException extends Exception {
    private static final String LINK_NOT_FOUND_EXCEPTION_TEXT_WITH_ID = "Посилання з ідентифікатором %s не знайдено!";
    private static final String LINK_NOT_FOUND_EXCEPTION_TEXT_WITH_LINK = "Посилання %s не знайдено!";
    private static final String LINK_NOT_FOUND_EXCEPTION_TEXT = "Посилання не знайдено!";

    public LinkNotFoundException(Long linkId) {
        super(String.format(LINK_NOT_FOUND_EXCEPTION_TEXT_WITH_ID, linkId));
    }

    public LinkNotFoundException(LinkDto dto) {
        super(String.format(LINK_NOT_FOUND_EXCEPTION_TEXT_WITH_LINK, dto));
    }

    public LinkNotFoundException() {
        super(LINK_NOT_FOUND_EXCEPTION_TEXT);
    }
}
