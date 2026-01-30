package com.example.Tenpo.infraestructure.adapter.output.cache;

import com.example.Tenpo.domain.port.output.PercentageCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.Tenpo.application.constants.LogMessages.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PercentageCacheAdapter implements PercentageCachePort {

    private static final String CACHE_NAME = "percentageCache";
    private static final String CACHE_KEY = "current";

    private final CacheManager cacheManager;

    @Override
    public Optional<Double> get() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            Double value = cache.get(CACHE_KEY, Double.class);
            if (value != null) {
                log.info(CACHE_HIT, value);
                return Optional.of(value);
            }
        }
        log.info(CACHE_MISS);
        return Optional.empty();
    }

    @Override
    public void save(Double percentage) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(CACHE_KEY, percentage);
            log.info(CACHE_SAVED, percentage);
        }

    }
}
