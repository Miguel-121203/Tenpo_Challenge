package com.example.Tenpo.infraestructure.adapter.input.rest;

import com.example.Tenpo.application.dto.CallHistoryResponse;
import com.example.Tenpo.domain.port.input.CallHistoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "History", description = "Endpoint para consultar historial de llamadas")
public class HistoryController {

    private final CallHistoryUseCase callHistoryUseCase;

    @GetMapping("/history")
    @Operation(summary = "Obtener historial de llamadas",
            description = "Devuelve el historial de llamadas paginado, ordenado por fecha descendente")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    public ResponseEntity<Page<CallHistoryResponse>> getHistory(
            @Parameter(description = "Numero de pagina (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "tamaño de pagina")
            @RequestParam(defaultValue = "10") int size) {

        Page<CallHistoryResponse> historyResponses = callHistoryUseCase.getHistory(PageRequest.of(page, size))
                .map(CallHistoryResponse::fromDomain);
        return ResponseEntity.ok(historyResponses);
    }
}
