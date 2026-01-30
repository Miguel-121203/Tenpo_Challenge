package com.example.Tenpo.infraestructure.adapter.input.rest;

import com.example.Tenpo.application.dto.CalculationRequest;
import com.example.Tenpo.domain.exception.PercentageNotAvailableException;
import com.example.Tenpo.domain.model.Calculation;
import com.example.Tenpo.domain.port.input.CalculationUseCase;
import com.example.Tenpo.domain.port.input.CallHistoryUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculationController Tests")
class CalculationControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CalculationUseCase calculationUseCase;

    @Mock
    private CallHistoryUseCase callHistoryUseCase;

    @InjectMocks
    private CalculationController calculationController;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler(callHistoryUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(calculationController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Nested
    @DisplayName("POST /api/calculate")
    class CalculateEndpoint {

        @Test
        @DisplayName("Debe retornar 200 con cálculo exitoso")
        void calculate_Success_ShouldReturn200() throws Exception {
            CalculationRequest request = new CalculationRequest(5.0, 5.0);
            Calculation calculation = Calculation.create(5.0, 5.0, 10.0);

            when(calculationUseCase.calculate(5.0, 5.0)).thenReturn(calculation);

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(11.0))
                    .andExpect(jsonPath("$.percentage").value(10.0))
                    .andExpect(jsonPath("$.originalSum").value(10.0));

            verify(calculationUseCase, times(1)).calculate(5.0, 5.0);
            verify(callHistoryUseCase, times(1)).recordCall(anyString(), any(), any(), isNull());
        }

        @Test
        @DisplayName("Debe retornar 400 cuando num1 es null")
        void calculate_WithNullNum1_ShouldReturn400() throws Exception {
            String requestJson = "{\"num1\": null, \"num2\": 5.0}";

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());

            verify(calculationUseCase, never()).calculate(any(), any());
        }

        @Test
        @DisplayName("Debe retornar 400 cuando num2 es null")
        void calculate_WithNullNum2_ShouldReturn400() throws Exception {
            String requestJson = "{\"num1\": 5.0, \"num2\": null}";

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());

            verify(calculationUseCase, never()).calculate(any(), any());
        }

        @Test
        @DisplayName("Debe retornar 400 con JSON inválido")
        void calculate_WithInvalidJson_ShouldReturn400() throws Exception {
            String invalidJson = "{invalid}";

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(calculationUseCase, never()).calculate(any(), any());
        }

        @Test
        @DisplayName("Debe retornar 503 cuando servicio externo no está disponible")
        void calculate_WhenServiceUnavailable_ShouldReturn503() throws Exception {
            CalculationRequest request = new CalculationRequest(5.0, 5.0);

            when(calculationUseCase.calculate(5.0, 5.0))
                    .thenThrow(new PercentageNotAvailableException("No se pudo obtener el porcentaje"));

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isServiceUnavailable());

            verify(callHistoryUseCase, times(1)).recordCall(anyString(), any(), isNull(), anyString());
        }

        @Test
        @DisplayName("Debe registrar en historial incluso cuando hay error")
        void calculate_WhenError_ShouldRecordInHistory() throws Exception {
            CalculationRequest request = new CalculationRequest(5.0, 5.0);

            when(calculationUseCase.calculate(5.0, 5.0))
                    .thenThrow(new PercentageNotAvailableException("Error de servicio"));


            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isServiceUnavailable());

            verify(callHistoryUseCase, times(1)).recordCall(
                    eq("/api/calculation"),
                    any(CalculationRequest.class),
                    isNull(),
                    eq("Error de servicio")
            );
        }

        @Test
        @DisplayName("Debe aceptar números negativos")
        void calculate_WithNegativeNumbers_ShouldReturn200() throws Exception {
            CalculationRequest request = new CalculationRequest(-10.0, 5.0);
            Calculation calculation = Calculation.create(-10.0, 5.0, 10.0);

            when(calculationUseCase.calculate(-10.0, 5.0)).thenReturn(calculation);

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.originalSum").value(-5.0));
        }

        @Test
        @DisplayName("Debe aceptar números decimales")
        void calculate_WithDecimalNumbers_ShouldReturn200() throws Exception {

            CalculationRequest request = new CalculationRequest(10.55, 4.45);
            Calculation calculation = Calculation.create(10.55, 4.45, 5.0);

            when(calculationUseCase.calculate(10.55, 4.45)).thenReturn(calculation);

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }
}
