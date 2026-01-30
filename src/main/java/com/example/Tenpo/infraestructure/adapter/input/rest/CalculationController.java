package com.example.Tenpo.infraestructure.adapter.input.rest;

import com.example.Tenpo.application.dto.CalculationRequest;
import com.example.Tenpo.application.dto.CalculationResponse;
import com.example.Tenpo.application.dto.ErrorResponse;
import com.example.Tenpo.domain.model.Calculation;
import com.example.Tenpo.domain.port.input.CalculationUseCase;
import com.example.Tenpo.domain.port.input.CallHistoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Calculation", description = "Endpoint para calculo con porcentaje dimanico")
public class CalculationController {

    private static final String ENDPOINT = "/api/calculation";

    private final CalculationUseCase calculationUseCase;
    private final CallHistoryUseCase callHistoryUseCase;

    @PostMapping("/calculate")
    @Operation(summary = "Calcular suma con porcentaje",
            description = "Suma dos números y aplica un porcentaje adicional obtenido de un servicio externo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cálculo exitoso",
                    content = @Content(schema = @Schema(implementation = CalculationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Servicio externo no disponible",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CalculationResponse> calculate(@Valid @RequestBody CalculationRequest request){
        CalculationResponse response = null;
        String error = null;

        try{
            Calculation calculation = calculationUseCase.calculate(request.getNum1(), request.getNum2());
            response = CalculationResponse.fromDomain(calculation);
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            error = e.getMessage();
            throw e;
        } finally {
            callHistoryUseCase.recordCall(ENDPOINT, request, response, error);
        }

    }
}
