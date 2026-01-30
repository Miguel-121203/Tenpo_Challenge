package com.example.Tenpo.domain.port.input;

import com.example.Tenpo.domain.model.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CallHistoryUseCase {
    void recordCall(String endpoint, Object parameters, Object response, String error);
    Page<CallHistory> getHistory(Pageable pageable);
}