package com.example.urlshortener.service.mapper;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.service.dto.LinkDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LinkMapperTest {
    @Autowired
    private LinkMapper mapper;

    @Test
    void fromValidEntityToDto() {
        LocalDateTime time = LocalDateTime.now();

        LinkEntity entity = LinkEntity.builder()
                .id(1)
                .shortLink("12345678")
                .longLink("http://test")
                .createAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();
        LinkDto actual = mapper.toDto(entity);

        LinkDto expected = LinkDto.builder()
                .shortLink("12345678")
                .longLink("http://test")
                .build();

        assertEquals(actual, expected);
    }

    @Test
    void fromInvalidFilledEntityToDto() {
        LocalDateTime time = LocalDateTime.now();

        LinkEntity entity = LinkEntity.builder()
                .id(1)
                .shortLink("12345678")
                .longLink("http://test")
                .createAt(time)
                .expiredAt(time)
                .transitions(0)
                .build();
        LinkDto actual = mapper.toDto(entity);

        LinkDto expected = LinkDto.builder()
                .shortLink("123456789")
                .longLink("http://test")
                .build();

        assertNotEquals(actual, expected);
    }

    @Test
    void fromInvalidNotFilledEntityToDto() {
        assertThrows(Exception.class, () -> {
            LinkEntity entity = LinkEntity.builder().build();
            mapper.toDto(entity);
        });
    }
}