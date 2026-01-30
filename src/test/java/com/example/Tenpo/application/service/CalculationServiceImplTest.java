package com.example.Tenpo.application.service;

import com.example.Tenpo.domain.model.Calculation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculationServiceImpl Tests")
class CalculationServiceImplTest {

    @Mock
    private PercentageService percentageService;

    @InjectMocks
    private CalculationServiceImpl calculationService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Debe calcular correctamente la suma con porcentaje")
    void calculate_ShouldReturnCorrectResult() {
        Double num1 = 5.0;
        Double num2 = 5.0;
        Double percentage = 10.0;
        when(percentageService.getPercentage()).thenReturn(percentage);

        Calculation result = calculationService.calculate(num1, num2);

        assertNotNull(result);
        assertEquals(10.0, result.getSum());
        assertEquals(11.0, result.getResult());
        assertEquals(10.0, result.getPercentage());
        verify(percentageService, times(1)).getPercentage();
    }

    @Test
    @DisplayName("Debe calcular correctamente con números negativos")
    void calculate_WithNegativeNumbers_ShouldReturnCorrectResult() {
        Double num1 = -10.0;
        Double num2 = 5.0;
        Double percentage = 10.0;
        when(percentageService.getPercentage()).thenReturn(percentage);

        Calculation result = calculationService.calculate(num1, num2);

        assertNotNull(result);
        assertEquals(-5.0, result.getSum());
        assertEquals(-5.5, result.getResult());
        verify(percentageService, times(1)).getPercentage();
    }

    @Test
    @DisplayName("Debe calcular correctamente con decimales")
    void calculate_WithDecimals_ShouldReturnCorrectResult() {
        Double num1 = 10.5;
        Double num2 = 4.5;
        Double percentage = 5.0;
        when(percentageService.getPercentage()).thenReturn(percentage);

        Calculation result = calculationService.calculate(num1, num2);

        assertNotNull(result);
        assertEquals(15.0, result.getSum());
        assertEquals(15.75, result.getResult());
        verify(percentageService, times(1)).getPercentage();
    }

    @Test
    @DisplayName("Debe calcular correctamente con ceros")
    void calculate_WithZeros_ShouldReturnZero() {

        Double num1 = 0.0;
        Double num2 = 0.0;
        Double percentage = 10.0;
        when(percentageService.getPercentage()).thenReturn(percentage);


        Calculation result = calculationService.calculate(num1, num2);

        assertNotNull(result);
        assertEquals(0.0, result.getSum());
        assertEquals(0.0, result.getResult());
        verify(percentageService, times(1)).getPercentage();
    }

    @Test
    @DisplayName("Debe calcular correctamente con porcentaje cero")
    void calculate_WithZeroPercentage_ShouldReturnSumOnly() {
        Double num1 = 100.0;
        Double num2 = 50.0;
        Double percentage = 0.0;
        when(percentageService.getPercentage()).thenReturn(percentage);

        Calculation result = calculationService.calculate(num1, num2);

        assertNotNull(result);
        assertEquals(150.0, result.getSum());
        assertEquals(150.0, result.getResult());
        verify(percentageService, times(1)).getPercentage();
    }
}
