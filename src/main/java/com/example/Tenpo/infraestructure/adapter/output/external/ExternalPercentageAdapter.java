package com.example.Tenpo.infraestructure.adapter.output.external;

import com.example.Tenpo.domain.exception.ExternalServiceException;
import com.example.Tenpo.domain.port.output.PercentageProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class ExternalPercentageAdapter implements PercentageProviderPort {
    private final double percentageValue;
    private final double failureRate;
    private final Random random;

    public ExternalPercentageAdapter(
            @Value("${external.percentage.value}") double percentageValue,
            @Value("${external.percentage.failure-rate}") double failureRate) {
        this.percentageValue = percentageValue;
        this.failureRate = failureRate;
        this.random = new Random();
    }

    @Override
    public Double getPercentage() {
        log.info("Llamando al servicio externo para obtener porcentaje");

        simulateNetworkDelay();

        if (shouldFail()){
            log.warn("Servicio externo fallo (Simulacion)");
            throw new ExternalServiceException("Servicio externo no esta disponible");
        }

        log.info("Servicio externo respondio con porcentaje {}", percentageValue);
        return percentageValue;
    }

    private void simulateNetworkDelay(){
        try{
            Thread.sleep(100+ random.nextInt(200));
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    private boolean shouldFail(){return random.nextDouble() < failureRate;}


}
