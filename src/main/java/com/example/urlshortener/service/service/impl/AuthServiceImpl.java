package com.example.urlshortener.service.service.impl;

import com.example.urlshortener.controller.request.RegisterRequest;
import com.example.urlshortener.controller.request.auth.AuthRequest;
import com.example.urlshortener.controller.response.auth.AuthResponse;
import com.example.urlshortener.data.entity.UserEntity;
import com.example.urlshortener.data.entity.UserRole;
import com.example.urlshortener.data.repository.UserRepository;
import com.example.urlshortener.exception.UserAlreadyExistsException;
import com.example.urlshortener.service.service.AuthService;
import com.example.urlshortener.service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(request.getUsername());
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(String.valueOf(UserRole.USER))
                .build();

        Optional<UserEntity> optionalUser = repository.findByUsername(user.getUsername());

        if (optionalUser.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        repository.save(user);

        String jwtToken = jwtService.generateToken(Map.of("userId", user.getId()), user);

        return new AuthResponse(jwtToken);
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(Map.of("userId", user.getId()), user);

        return new AuthResponse(jwtToken);
    }
}
