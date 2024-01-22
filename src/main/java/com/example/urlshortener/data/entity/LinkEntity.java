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
    private String shortUrl;

    @Column(name = "original_url", nullable = false)
    private String longUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedDate
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "visit_count")
    private long transitions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;
}