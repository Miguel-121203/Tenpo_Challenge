package com.example.Tenpo.domain.port.output;

import java.util.Optional;

public interface PercentageCachePort {
    Optional<Double> get();
    void save(Double percentage);
}
