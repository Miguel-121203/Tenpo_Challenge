package com.example.Tenpo.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registro del historial de llamadas")
public class CallHistoryResponse {

    @Schema(description = "ID del registro")
    private Long id;

    @Schema(description = "Fecha y hora de la llamada")
    private LocalDateTime timestamp;

    @Schema(description = "Endpoint invocado")
    private String endpoint;

    @Schema(description = "Parámetros de la llamada en formato JSON")
    private String parameters;

    @Schema(description = "Respuesta de la llamada en formato JSON")
    private String response;

    @Schema(description = "Error si la llamada falló")
    private String error;
}
