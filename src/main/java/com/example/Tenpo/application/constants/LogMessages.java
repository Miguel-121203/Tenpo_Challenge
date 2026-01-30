package com.example.Tenpo.application.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogMessages {

    public static final String CALC_STARTING = "Calculando suma de {} + {}";
    public static final String CALC_RESULT = "Resultado: {} + {}% = {}";

    public static final String PERCENTAGE_FROM_EXTERNAL = "Porcentaje obtenido del servicio externo: {}";
    public static final String PERCENTAGE_FROM_CACHE = "Porcentaje obtenido de cache: {}";
    public static final String PERCENTAGE_EXTERNAL_FAILED = "Error al obtener porcentaje del servicio externo, intentando usar cache";

    public static final String EXTERNAL_CALLING = "Llamando al servicio externo para obtener porcentaje";
    public static final String EXTERNAL_SUCCESS = "Servicio externo respondio con porcentaje: {}";
    public static final String EXTERNAL_FAILED = "Servicio externo fallo (Simulacion)";

    public static final String CACHE_HIT = "Valor encontrado en cache: {}";
    public static final String CACHE_MISS = "No se encontro valor en cache";
    public static final String CACHE_SAVED = "Valor guardado en cache: {}";

    public static final String HISTORY_RECORDED = "Llamada registrada en el historial: {}";
    public static final String HISTORY_ERROR = "Error al registrar llamada en historial";
    public static final String HISTORY_JSON_ERROR = "Error al serializar objeto a JSON";
}
