package com.example.Tenpo.domain.port.input;

import com.example.Tenpo.domain.model.Calculation;

public interface CalculationUseCase {
    Calculation calculate(Double num1, Double num2);
}
