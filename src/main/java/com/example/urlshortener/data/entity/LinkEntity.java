package com.example.urlshortener.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "link")
public class LinkEntity {
    @Id
    @Column(name = "short_url", nullable = false)
    private String shortLink;

    @Column(name = "original_url", nullable = false)
    private String longLink;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @CreatedDate
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "visit_count")
    private long transitions;
}