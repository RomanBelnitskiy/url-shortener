package com.example.urlshortener.service.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@DisplayName("JwtService tests")
class JwtServiceImplTest {
    private static final String SECRET_KEY = "7f5aa1e2e4cce1e41cf9b93db36f87c82a790cedb958bcd7f711c305d21e8db2";  // "super_secret_key"
    private static final long EXPIRATION_TIME = 2 * 1000 * 60;  // 2 min
    private static final String TOKEN_EXPIRED = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTcwNjA0NzQzNywiZXhwIjoxNzA2MDQ3NTU3fQ.yNC5Mxy8p8Gcgtys8cCASauka4VQFiEWASu2O5wzsuQ";

    private JwtServiceImpl service;

    @BeforeEach
    void init() {
        service = new JwtServiceImpl();
        setField(service, "SECRET_KEY", SECRET_KEY);
        setField(service, "JWT_EXPIRATION_TIME", EXPIRATION_TIME);
    }

    @Test
    @DisplayName("Generated token contains username in subject")
    void generatedTokenContainsUsernameInSubject() {
        UserDetails userDetails = userDetails();

        String token = service.generateToken(userDetails);

        Claims claims = extractAllClaims(token);
        assertEquals(userDetails.getUsername(), claims.getSubject());
    }

    @Test
    @DisplayName("Generated token is not expired")
    void generatedTokenIsNotExpired() {
        UserDetails userDetails = userDetails();

        String token = service.generateToken(userDetails);

        Claims claims = extractAllClaims(token);
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    @DisplayName("Token is expired")
    void tokenIsExpired() {
        assertThrows(ExpiredJwtException.class, () -> extractAllClaims(TOKEN_EXPIRED));
    }

    @Test
    @DisplayName("Generated token IssuedAt is today")
    void generatedTokenIssuedAtIsToday() {
        UserDetails userDetails = userDetails();

        String token = service.generateToken(userDetails);

        Claims claims = extractAllClaims(token);
        assertTrue(isSameDay(new Date(), claims.getIssuedAt()));
    }

    @Test
    @DisplayName("Generated token contains userId when pass it in additional claims")
    void generatedTokenContainsUserId_WhenPassInInAdditionalClaims() {
        final Long userId = 1L;
        UserDetails userDetails = userDetails();
        String token = service.generateToken(Map.of("userId", userId), userDetails);

        assertEquals(userId, service.extractUserId(token));
    }

    @Test
    @DisplayName("Generated token is valid")
    void generatedTokenIsValid() {
        UserDetails userDetails = userDetails();
        String token = service.generateToken(userDetails);

        assertTrue(service.isTokenValid(token, userDetails));
    }

    private Claims extractAllClaims(String token) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private UserDetails userDetails() {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return "username";
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }
}