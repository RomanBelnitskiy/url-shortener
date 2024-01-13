package com.example.urlshortener.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "link")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "short_url")
    @NonNull
    private String shortLink;

    @Column(name = "original_url")
    @NonNull
    private String longLink;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @CreatedDate
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt = LocalDateTime.now();

    @Column(name = "visit_count")
    private long transitions;
}
