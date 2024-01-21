package com.example.urlshortener.data.repository;

import com.example.urlshortener.data.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, String> {
    Optional<LinkEntity> findByShortUrl(String shortUrl);

    boolean existsByShortUrl(String shortUrl);

    @Modifying
    @Query(value = "update link set visit_count = visit_count + :transitions where short_url = :shortUrl",
            nativeQuery = true)
    void increaseTransitionsBy(@Param("shortUrl") String shortUrl, @Param("transitions") Long additionalTransitions);
}
