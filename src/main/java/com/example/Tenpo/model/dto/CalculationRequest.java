package com.example.Tenpo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para el cálculo con porcentaje dinámico")
public class CalculationRequest {

    @NotNull(message = "num1 es requerido")
    @Schema(description = "Primer numero para la suma",example = "5")
    private Double num1;

    @NotNull(message = "num2 es requerido")
    @Schema(description = "Segundo numero para la suma ", example = "5")
    private Double num2;
}
