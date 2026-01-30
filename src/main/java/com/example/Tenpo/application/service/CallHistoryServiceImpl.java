package com.example.Tenpo.application.service;

import com.example.Tenpo.domain.model.CallHistory;
import com.example.Tenpo.domain.port.input.CallHistoryUseCase;
import com.example.Tenpo.domain.port.output.CallHistoryRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.example.Tenpo.application.constants.LogMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallHistoryServiceImpl implements CallHistoryUseCase {

    private final CallHistoryRepositoryPort callHistoryRepositoryPort;
    private final ObjectMapper objectMapper;


    @Override
    @Async("historyExecutor")
    public void recordCall(String endpoint, Object parameters, Object response, String error) {
        try{
            CallHistory callHistory = CallHistory.create(
                    endpoint,
                    toJson(parameters),
                    toJson(response),
                    error
            );
            callHistoryRepositoryPort.save(callHistory);
            log.info(HISTORY_RECORDED, callHistory.toString());
        }catch (Exception e){
            log.error(HISTORY_ERROR, e);
        }
    }

    @Override
    public Page<CallHistory> getHistory(Pageable pageable) {
        return callHistoryRepositoryPort.findAll(pageable);
    }


    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn(HISTORY_JSON_ERROR, e);
            return obj.toString();
        }
    }
}
