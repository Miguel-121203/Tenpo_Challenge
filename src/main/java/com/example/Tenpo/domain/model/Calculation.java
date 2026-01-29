package com.example.Tenpo.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Calculation {

    private final Double num1;
    private final Double num2;
    private final Double sum;
    private final Double percentage;
    private final Double result;

    public static Calculation create(Double num1, Double num2, Double percentage){
        Double sum = num1 + num2 ;
        Double result = sum * (1 + percentage / 100);

        return Calculation.builder()
                .num1(num1)
                .num2(num2)
                .sum(sum)
                .percentage(percentage)
                .result(result)
                .build();

    }
}
