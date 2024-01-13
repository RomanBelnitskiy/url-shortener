package com.example.urlshortener.data.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "link")
public class LinkEntity {
    @Id
    @Column(name = "short_url")
    @NonNull
    private String shortLink;

    @Column(name = "original_url")
    @NonNull
    private String longLink;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @CreatedDate
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "visit_count")
    private long transitions;

    public LinkEntity() {
    }
}