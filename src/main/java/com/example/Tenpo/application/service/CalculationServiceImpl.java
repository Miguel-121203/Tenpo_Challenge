package com.example.Tenpo.application.service;

import com.example.Tenpo.domain.model.Calculation;
import com.example.Tenpo.domain.port.input.CalculationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.Tenpo.application.constants.LogMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationServiceImpl implements CalculationUseCase {

    private final PercentageService percentageService;

    @Override
    public Calculation calculate(Double num1, Double num2) {
        log.info(CALC_STARTING, num1, num2);

        Double percentage = percentageService.getPercentage();
        Calculation calculation = Calculation.create(num1, num2, percentage);

        log.info(CALC_RESULT, calculation.getSum(), percentage, calculation.getResult());

        return calculation;
    }
}
