package com.example.Tenpo.application.service;

import com.example.Tenpo.domain.model.CallHistory;
import com.example.Tenpo.domain.port.output.CallHistoryRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CallHistoryServiceImpl Tests")
class CallHistoryServiceImplTest {

    @Mock
    private CallHistoryRepositoryPort callHistoryRepositoryPort;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CallHistoryServiceImpl callHistoryService;

    @Captor
    private ArgumentCaptor<CallHistory> callHistoryCaptor;

    @Nested
    @DisplayName("recordCall Tests")
    class RecordCallTests {

        @Test
        @DisplayName("Debe registrar una llamada exitosa")
        void recordCall_Success_ShouldSaveToRepository() throws JsonProcessingException {
            String endpoint = "/api/calculation";
            Object parameters = new TestRequest(5.0, 10.0);
            Object response = new TestResponse(15.75);
            String error = null;

            when(objectMapper.writeValueAsString(parameters)).thenReturn("{\"num1\":5.0,\"num2\":10.0}");
            when(objectMapper.writeValueAsString(response)).thenReturn("{\"result\":15.75}");

            callHistoryService.recordCall(endpoint, parameters, response, error);

            verify(callHistoryRepositoryPort, times(1)).save(callHistoryCaptor.capture());
            CallHistory saved = callHistoryCaptor.getValue();

            assertEquals(endpoint, saved.getEndpoint());
            assertEquals("{\"num1\":5.0,\"num2\":10.0}", saved.getParameters());
            assertEquals("{\"result\":15.75}", saved.getResponse());
            assertNull(saved.getError());
            assertNotNull(saved.getTimestamp());
        }

        @Test
        @DisplayName("Debe registrar una llamada con error")
        void recordCall_WithError_ShouldSaveErrorMessage() throws JsonProcessingException {
            String endpoint = "/api/calculation";
            Object parameters = new TestRequest(5.0, 10.0);
            String errorMessage = "Servicio externo no disponible";

            when(objectMapper.writeValueAsString(parameters)).thenReturn("{\"num1\":5.0,\"num2\":10.0}");

            callHistoryService.recordCall(endpoint, parameters, null, errorMessage);

            verify(callHistoryRepositoryPort, times(1)).save(callHistoryCaptor.capture());
            CallHistory saved = callHistoryCaptor.getValue();

            assertEquals(endpoint, saved.getEndpoint());
            assertEquals(errorMessage, saved.getError());
            assertNull(saved.getResponse());
        }

        @Test
        @DisplayName("Debe manejar error de serialización JSON")
        void recordCall_WhenJsonSerializationFails_ShouldUseToString() throws JsonProcessingException {
            String endpoint = "/api/calculation";
            Object parameters = new TestRequest(5.0, 10.0);

            when(objectMapper.writeValueAsString(parameters))
                    .thenThrow(new JsonProcessingException("Error de serialización") {});

            callHistoryService.recordCall(endpoint, parameters, null, null);

            verify(callHistoryRepositoryPort, times(1)).save(callHistoryCaptor.capture());
            CallHistory saved = callHistoryCaptor.getValue();

            assertNotNull(saved.getParameters());
        }

        @Test
        @DisplayName("Debe manejar parámetros null")
        void recordCall_WithNullParameters_ShouldHandleGracefully() {
            String endpoint = "/api/calculation";

            callHistoryService.recordCall(endpoint, null, null, null);

            verify(callHistoryRepositoryPort, times(1)).save(callHistoryCaptor.capture());
            CallHistory saved = callHistoryCaptor.getValue();

            assertNull(saved.getParameters());
            assertNull(saved.getResponse());
        }

        @Test
        @DisplayName("No debe propagar excepción si falla el guardado")
        void recordCall_WhenSaveFails_ShouldNotThrowException() throws JsonProcessingException {
            String endpoint = "/api/calculation";
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");
            doThrow(new RuntimeException("DB Error")).when(callHistoryRepositoryPort).save(any());

            assertDoesNotThrow(() ->
                callHistoryService.recordCall(endpoint, new TestRequest(1.0, 2.0), null, null)
            );
        }
    }

    @Nested
    @DisplayName("getHistory Tests")
    class GetHistoryTests {

        @Test
        @DisplayName("Debe retornar historial paginado")
        void getHistory_ShouldReturnPagedResults() {
            Pageable pageable = PageRequest.of(0, 10);
            List<CallHistory> historyList = List.of(
                    CallHistory.create("/api/calculation", "{}", "{}", null),
                    CallHistory.create("/api/calculation", "{}", "{}", null)
            );
            Page<CallHistory> expectedPage = new PageImpl<>(historyList, pageable, 2);

            when(callHistoryRepositoryPort.findAll(pageable)).thenReturn(expectedPage);

            Page<CallHistory> result = callHistoryService.getHistory(pageable);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            verify(callHistoryRepositoryPort, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Debe retornar página vacía si no hay registros")
        void getHistory_WhenEmpty_ShouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<CallHistory> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(callHistoryRepositoryPort.findAll(pageable)).thenReturn(emptyPage);

            Page<CallHistory> result = callHistoryService.getHistory(pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }
    }

    private record TestRequest(Double num1, Double num2) {}
    private record TestResponse(Double result) {}
}
