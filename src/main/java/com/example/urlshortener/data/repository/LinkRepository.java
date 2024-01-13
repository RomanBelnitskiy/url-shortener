package com.example.urlshortener.data.repository;

import com.example.urlshortener.data.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByShortLink(String shortLink);

    boolean existsByShortLink(String shortLink);
}