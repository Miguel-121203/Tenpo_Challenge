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
@Schema(description = "Respuesta del cálculo con porcentaje aplicado")
public class CalculationResponse {

    @Schema(description = "Resultado final con porcentaje aplicado", example = "10.5")
    private Double result;

    @Schema(description = "Porcentaje aplicado", example = "5")
    private Double percentage;

    @Schema(description = "Suma sin porcentaje", example = "10")
    private Double originalSum;
}
