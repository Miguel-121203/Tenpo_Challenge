package com.example.Tenpo.infraestructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PERCENTAGE_CACHE_NAME = "percentageCache";

    @Value("${cache.percentage.ttl-minutes}")
    private int ttlMinutes;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager CacheManager = new CaffeineCacheManager(PERCENTAGE_CACHE_NAME);
        CacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .maximumSize(100));
        return CacheManager;
    }

}
