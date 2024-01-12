package com.example.urlshortener.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkRequest {
    @NotBlank
    @Size(max = 2048)
    private String originalLink;
}
