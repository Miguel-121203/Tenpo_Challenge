package com.example.Tenpo.domain.port.output;

import com.example.Tenpo.domain.model.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CallHistoryRepositoryPort {
    void save(CallHistory callHistory);
    Page<CallHistory> findAll(Pageable pageable);

}
