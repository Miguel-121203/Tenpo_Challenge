package com.example.Tenpo.application.service;

import com.example.Tenpo.domain.exception.ExternalServiceException;
import com.example.Tenpo.domain.exception.PercentageNotAvailableException;
import com.example.Tenpo.domain.port.output.PercentageCachePort;
import com.example.Tenpo.domain.port.output.PercentageProviderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            log.info("Porcentaje obtenido del servicio externo {}", percentage);
            return percentage;
        }catch (ExternalServiceException e){
            log.warn("Error al obtener porcentaje del servicio externo, intentando usar cache");
            return getFromCacheOrThrow();
        }
    }


    private Double getFromCacheOrThrow(){
        return percentageCache.get()
                .map(value ->{
                    log.info("Porcentaje obtenido de cache {}", value);
                    return value;
                })
                .orElseThrow( () -> new PercentageNotAvailableException(
                        "No se pudo obtener el porcentaje del servicio externo y no hay valor en cache"));
    }
}
