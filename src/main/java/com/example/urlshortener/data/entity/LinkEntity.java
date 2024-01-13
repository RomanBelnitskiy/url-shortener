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
    Long id;

    @Column(name = "short_url")
    @NonNull
    String shortLink;

    @Column(name = "original_link")
    @NonNull
    String longLink;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createAt = LocalDateTime.now();

    @CreatedDate
    @Column(name = "expired_at", nullable = false)
    LocalDateTime expiredAt = LocalDateTime.now();

    @Column(name = "visit_count")
    Long transitions;
}
