package com.example.urlshortener.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLinkRequest {
    @NotBlank
    @Size(max = 2048)
    private String originalLink;
    @NotBlank
    private LocalDateTime expiredAt;
}
