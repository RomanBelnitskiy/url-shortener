package com.example.urlshortener.data.repository;

import com.example.urlshortener.data.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, String> {
    Optional<LinkEntity> findByShortUrl(String shortUrl);

    boolean existsByShortUrl(String shortUrl);
}
