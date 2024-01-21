package com.example.urlshortener.config;

import com.example.urlshortener.cache.CustomRemovalListener;
import com.example.urlshortener.service.service.LinkService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(LinkService service) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(false);
        cacheManager.registerCustomCache("links", linksCache(service, cacheManager));
        cacheManager.registerCustomCache("transitions", transitionsCache());
        return cacheManager;
    }

    @Bean
    public CustomRemovalListener removalListener(LinkService service, CacheManager manager) {
        return new CustomRemovalListener(service, manager);
    }

    Cache<Object, Object> linksCache(LinkService service, CacheManager cacheManager) {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(50)
                .expireAfterAccess(3, TimeUnit.MINUTES)
                .scheduler(Scheduler.systemScheduler())
                .removalListener(removalListener(service, cacheManager))
                .build();
    }

    Cache<Object, Object> transitionsCache() {
        return Caffeine.newBuilder()
                .build();
    }
}
