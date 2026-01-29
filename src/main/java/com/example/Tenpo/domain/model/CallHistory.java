package com.example.Tenpo.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CallHistory {
    private final Long id;
    private final LocalDateTime timestamp;
    private final String endpoint;
    private final String parameters;
    private final String response;
    private final String error;

    public static CallHistory create(String endpoint, String parameters, String response, String error) {
        return CallHistory.builder()
                .timestamp(LocalDateTime.now())
                .endpoint(endpoint)
                .parameters(parameters)
                .response(response)
                .error(error)
                .build();
    }
}
