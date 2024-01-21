package com.example.urlshortener.cache;

import com.example.urlshortener.service.service.LinkService;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Slf4j
@RequiredArgsConstructor
public class CustomRemovalListener implements RemovalListener<Object, Object> {
    private final LinkService linkService;
    private final CacheManager cacheManager;

    @Override
    public void onRemoval(@Nullable Object key, @Nullable Object value, RemovalCause cause) {
        log.info("Removal listener called with key [{}], cause [{}], evicted [{}]",
                key, cause.toString(), cause.wasEvicted());

        if (key == null) return;

        String shortUrl = (String) key;
        Cache transitionsCache = cacheManager.getCache("transitions");
        Long transitions = transitionsCache.get(shortUrl, Long.class);

        if (transitions != null) {
            linkService.updateTransitions(shortUrl, transitions);
            transitionsCache.evict(shortUrl);
        }
    }
}
