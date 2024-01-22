package com.example.urlshortener.data.repository;

import com.example.urlshortener.data.entity.LinkEntity;
import com.example.urlshortener.service.dto.LinkDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, String> {
    Optional<LinkEntity> findByShortUrl(String shortUrl);

    @Query(value = "SELECT le FROM LinkEntity le WHERE le.shortUrl = :shortUrl AND le.user.id = :userId")
    Optional<LinkEntity> findByShortUrl(String shortUrl, Long userId);

    boolean existsByShortUrl(String shortUrl);

    @Modifying
    @Query(value = "update link set visit_count = visit_count + :transitions where short_url = :shortUrl",
            nativeQuery = true)
    void increaseTransitionsBy(@Param("shortUrl") String shortUrl, @Param("transitions") Long additionalTransitions);

    @Query(value = "SELECT le FROM LinkEntity le WHERE le.user.id = :userId")
    List<LinkEntity> findAll(Long userId);


}
