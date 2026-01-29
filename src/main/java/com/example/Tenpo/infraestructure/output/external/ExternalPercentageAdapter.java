package com.example.Tenpo.infraestructure.output.external;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class ExternalPercentageAdapter {
    private final double percentageValue;
    private final double failtureRate;
    private final Random random;

    public ExternalPercentageAdapter(
            @Value("${external.percentage.value}") double percentageValue,
            @Value("${external.percentage.failture-rate}") double failtureRate){
        this.percentageValue = percentageValue;
        this.failtureRate = failtureRate;
        this.random = new Random();
    }




    private void simulateNetworkDelay(){

        try{
            Thread.sleep(100+ random.nextInt(200));
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    private boolean shouldFail(){return random.nextDouble() < failtureRate;}

}
