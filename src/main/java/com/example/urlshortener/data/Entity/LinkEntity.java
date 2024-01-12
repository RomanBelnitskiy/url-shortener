package com.example.urlshortener.data.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "link")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_link")
    @NonNull
    private String shortLink;

    @Column(name = "long_link")
    @NonNull
    private String longLink;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @CreatedDate
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt = LocalDateTime.now();

    @Column
    private Integer transitions;
}
