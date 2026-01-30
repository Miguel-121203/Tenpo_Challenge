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
@Schema(description = "Respuesta de error")
public class ErrorResponse {

    @Schema(description = "Código de estado HTTP")
    private int status;

    @Schema(description = "Mensaje de error")
    private String message;

    @Schema(description = "Fecha y hora del error")
    private LocalDateTime timestamp;

    @Schema(description = "Ruta que generó el error")
    private String path;
}

