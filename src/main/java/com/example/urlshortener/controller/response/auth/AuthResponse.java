package com.example.urlshortener.controller.response.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private Long id;
    private String username;
    private String email;
    private String role;

}
