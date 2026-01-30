package com.example.Tenpo.application.service;

import com.example.Tenpo.domain.exception.ExternalServiceException;
import com.example.Tenpo.domain.exception.PercentageNotAvailableException;
import com.example.Tenpo.domain.port.output.PercentageCachePort;
import com.example.Tenpo.domain.port.output.PercentageProviderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.Tenpo.application.constants.LogMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PercentageService {

    private final PercentageProviderPort percentageProvider;
    private final PercentageCachePort percentageCache;

    public Double getPercentage(){
        try{
            Double percentage = percentageProvider.getPercentage();
            percentageCache.save(percentage);
            log.info(PERCENTAGE_FROM_EXTERNAL, percentage);
            return percentage;
        }catch (ExternalServiceException e){
            log.warn(PERCENTAGE_EXTERNAL_FAILED);
            return getFromCacheOrThrow();
        }
    }


    private Double getFromCacheOrThrow(){
        return percentageCache.get()
                .map(value ->{
                    log.info(PERCENTAGE_FROM_CACHE, value);
                    return value;
                })
                .orElseThrow( () -> new PercentageNotAvailableException(
                        "No se pudo obtener el porcentaje del servicio externo y no hay valor en cache"));
    }
}
