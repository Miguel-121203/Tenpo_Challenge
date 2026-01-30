package com.example.Tenpo.infraestructure.adapter.input.rest;

import com.example.Tenpo.domain.model.CallHistory;
import com.example.Tenpo.domain.port.input.CallHistoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoryController Tests")
class HistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CallHistoryUseCase callHistoryUseCase;

    @InjectMocks
    private HistoryController historyController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(historyController).build();
    }

    @Nested
    @DisplayName("GET /api/history")
    class GetHistoryEndpoint {

        @Test
        @DisplayName("Debe retornar historial paginado con valores por defecto")
        void getHistory_WithDefaultParams_ShouldReturnPagedHistory() throws Exception {
            List<CallHistory> historyList = List.of(
                    CallHistory.create("/api/calculation", "{\"num1\":5,\"num2\":5}", "{\"result\":11}", null),
                    CallHistory.create("/api/calculation", "{\"num1\":10,\"num2\":20}", "{\"result\":33}", null)
            );
            Page<CallHistory> historyPage = new PageImpl<>(historyList, PageRequest.of(0, 10), 2);

            when(callHistoryUseCase.getHistory(any(PageRequest.class))).thenReturn(historyPage);

            mockMvc.perform(get("/api/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(callHistoryUseCase, times(1)).getHistory(PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Debe retornar historial con parámetros personalizados")
        void getHistory_WithCustomParams_ShouldReturnPagedHistory() throws Exception {
            List<CallHistory> historyList = List.of(
                    CallHistory.create("/api/calculation", "{}", "{}", null)
            );
            Page<CallHistory> historyPage = new PageImpl<>(historyList, PageRequest.of(2, 5), 11);

            when(callHistoryUseCase.getHistory(PageRequest.of(2, 5))).thenReturn(historyPage);

            mockMvc.perform(get("/api/history")
                            .param("page", "2")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(2))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.totalElements").value(11))
                    .andExpect(jsonPath("$.totalPages").value(3));

            verify(callHistoryUseCase, times(1)).getHistory(PageRequest.of(2, 5));
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay registros")
        void getHistory_WhenEmpty_ShouldReturnEmptyList() throws Exception {
            Page<CallHistory> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(callHistoryUseCase.getHistory(any(PageRequest.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0));
        }

        @Test
        @DisplayName("Debe retornar historial con registros de error")
        void getHistory_WithErrorRecords_ShouldIncludeErrorField() throws Exception {
            List<CallHistory> historyList = List.of(
                    CallHistory.create("/api/calculation", "{\"num1\":5,\"num2\":5}", null, "Servicio no disponible")
            );
            Page<CallHistory> historyPage = new PageImpl<>(historyList, PageRequest.of(0, 10), 1);

            when(callHistoryUseCase.getHistory(any(PageRequest.class))).thenReturn(historyPage);

            mockMvc.perform(get("/api/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].error").value("Servicio no disponible"))
                    .andExpect(jsonPath("$.content[0].response").doesNotExist());
        }

        @Test
        @DisplayName("Debe retornar historial con registros exitosos")
        void getHistory_WithSuccessRecords_ShouldHaveNullError() throws Exception {
            List<CallHistory> historyList = List.of(
                    CallHistory.create("/api/calculation", "{\"num1\":5,\"num2\":5}", "{\"result\":11}", null)
            );
            Page<CallHistory> historyPage = new PageImpl<>(historyList, PageRequest.of(0, 10), 1);

            when(callHistoryUseCase.getHistory(any(PageRequest.class))).thenReturn(historyPage);

            mockMvc.perform(get("/api/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].endpoint").value("/api/calculation"))
                    .andExpect(jsonPath("$.content[0].parameters").value("{\"num1\":5,\"num2\":5}"))
                    .andExpect(jsonPath("$.content[0].response").value("{\"result\":11}"))
                    .andExpect(jsonPath("$.content[0].error").doesNotExist());
        }

        @Test
        @DisplayName("Debe manejar página grande correctamente")
        void getHistory_WithLargePageSize_ShouldHandleCorrectly() throws Exception {
            Page<CallHistory> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 100), 0);

            when(callHistoryUseCase.getHistory(PageRequest.of(0, 100))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/history")
                            .param("page", "0")
                            .param("size", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(100));

            verify(callHistoryUseCase, times(1)).getHistory(PageRequest.of(0, 100));
        }
    }
}
