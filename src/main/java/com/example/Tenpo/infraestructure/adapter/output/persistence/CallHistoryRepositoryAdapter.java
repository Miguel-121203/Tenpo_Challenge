package com.example.Tenpo.infraestructure.adapter.output.persistence;

import com.example.Tenpo.domain.model.CallHistory;
import com.example.Tenpo.domain.port.output.CallHistoryRepositoryPort;
import com.example.Tenpo.infraestructure.adapter.output.persistence.mapper.CallHistoryMapper;
import com.example.Tenpo.infraestructure.adapter.output.persistence.repository.JpaCallHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallHistoryRepositoryAdapter implements CallHistoryRepositoryPort {
    private final JpaCallHistoryRepository jpaCallHistoryRepository;
    private final CallHistoryMapper mapper;

    @Override
    public void save(CallHistory callHistory) {
        jpaCallHistoryRepository.save(mapper.toEntity(callHistory));
    }

    @Override
    public Page<CallHistory> findAll(Pageable pageable) {
        return jpaCallHistoryRepository.findAllByOrderByTimestampDesc(pageable)
                .map(mapper::toDomain);
    }
}
