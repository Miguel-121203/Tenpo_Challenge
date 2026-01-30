package com.example.Tenpo.infraestructure.adapter.output.persistence.mapper;

import com.example.Tenpo.domain.model.CallHistory;
import com.example.Tenpo.infraestructure.adapter.output.persistence.entity.CallHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CallHistoryMapper {

    public CallHistoryEntity toEntity(CallHistory domain) {
        return CallHistoryEntity.builder()
                .id(domain.getId())
                .timestamp(domain.getTimestamp())
                .endpoint(domain.getEndpoint())
                .parameters(domain.getParameters())
                .response(domain.getResponse())
                .error(domain.getError())
                .build();
    }

    public CallHistory toDomain(CallHistoryEntity entity) {
        return CallHistory.builder()
                .id(entity.getId())
                .timestamp(entity.getTimestamp())
                .endpoint(entity.getEndpoint())
                .parameters(entity.getParameters())
                .response(entity.getResponse())
                .error(entity.getError())
                .build();
    }
}
