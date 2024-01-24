package com.example.urlshortener.service;

import com.example.urlshortener.controller.request.RegisterRequest;
import com.example.urlshortener.controller.request.auth.AuthRequest;
import com.example.urlshortener.controller.response.auth.AuthResponse;
import com.example.urlshortener.data.entity.UserDetailsImpl;
import com.example.urlshortener.data.entity.UserEntity;
import com.example.urlshortener.data.repository.UserRepository;
import com.example.urlshortener.exception.UserAlreadyExistsException;
import com.example.urlshortener.service.service.JwtService;
import com.example.urlshortener.service.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should register user and return JWT token")
    void registerUserAndReturnJwtTokenTest() {
        RegisterRequest request = new RegisterRequest("username", "password");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).then(invocation -> {
            UserEntity user = invocation.getArgument(0, UserEntity.class);
            user.setId(1L);
            return user;
        });
        when(jwtService.generateToken(anyMap(), any(UserDetailsImpl.class))).thenReturn("jwtToken");

        AuthResponse authResponse = authService.register(request);

        assertNotNull(authResponse);
        assertEquals("jwtToken", authResponse.getToken());
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).findByUsername("username");
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(jwtService, times(1)).generateToken(anyMap(), any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException")
    void throwUserAlreadyExistsExceptionTest() {
        RegisterRequest request = new RegisterRequest("username", "password");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("Should authenticate user and return JWT token")
    void authenticateUserTest() {
        AuthRequest request = new AuthRequest("username", "password");
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(1L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetailsImpl.class))).thenReturn("jwtToken");

        AuthResponse authResponse = authService.authenticate(request);

        assertNotNull(authResponse);
        assertEquals("jwtToken", authResponse.getToken());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1))
                .generateToken(anyMap(), any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException")
    void throwBadCredentialsExceptionTest() {
        AuthRequest request = new AuthRequest("username", "password");

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("username", "password")))
                .thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }
}