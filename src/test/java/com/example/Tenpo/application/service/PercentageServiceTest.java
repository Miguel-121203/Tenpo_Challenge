package com.example.Tenpo.application.service;

import com.example.Tenpo.domain.exception.ExternalServiceException;
import com.example.Tenpo.domain.exception.PercentageNotAvailableException;
import com.example.Tenpo.domain.port.output.PercentageCachePort;
import com.example.Tenpo.domain.port.output.PercentageProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PercentageService Tests")
class PercentageServiceTest {

    @Mock
    private PercentageProviderPort percentageProvider;

    @Mock
    private PercentageCachePort percentageCache;

    @InjectMocks
    private PercentageService percentageService;

    @Nested
    @DisplayName("Cuando el servicio externo funciona")
    class WhenExternalServiceWorks {

        @Test
        @DisplayName("Debe retornar el porcentaje del servicio externo")
        void getPercentage_ShouldReturnFromExternalService() {
            Double expectedPercentage = 10.0;
            when(percentageProvider.getPercentage()).thenReturn(expectedPercentage);

            Double result = percentageService.getPercentage();

            assertEquals(expectedPercentage, result);
            verify(percentageProvider, times(1)).getPercentage();
            verify(percentageCache, times(1)).save(expectedPercentage);
        }

        @Test
        @DisplayName("Debe guardar el porcentaje en caché")
        void getPercentage_ShouldSaveToCache() {
            Double percentage = 15.0;
            when(percentageProvider.getPercentage()).thenReturn(percentage);

            percentageService.getPercentage();

            verify(percentageCache, times(1)).save(percentage);
        }
    }

    @Nested
    @DisplayName("Cuando el servicio externo falla")
    class WhenExternalServiceFails {

        @BeforeEach
        void setUp() {
            when(percentageProvider.getPercentage())
                    .thenThrow(new ExternalServiceException("Servicio no disponible"));
        }

        @Test
        @DisplayName("Debe retornar el valor del caché si existe")
        void getPercentage_WhenExternalFails_ShouldReturnFromCache() {
            Double cachedPercentage = 5.0;
            when(percentageCache.get()).thenReturn(Optional.of(cachedPercentage));

            Double result = percentageService.getPercentage();

            assertEquals(cachedPercentage, result);
            verify(percentageProvider, times(1)).getPercentage();
            verify(percentageCache, times(1)).get();
        }

        @Test
        @DisplayName("Debe lanzar excepción si no hay valor en caché")
        void getPercentage_WhenExternalFailsAndNoCache_ShouldThrowException() {

            when(percentageCache.get()).thenReturn(Optional.empty());

            PercentageNotAvailableException exception = assertThrows(
                    PercentageNotAvailableException.class,
                    () -> percentageService.getPercentage()
            );

            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains("No se pudo obtener el porcentaje"));
            verify(percentageProvider, times(1)).getPercentage();
            verify(percentageCache, times(1)).get();
        }

        @Test
        @DisplayName("No debe guardar en caché cuando falla el servicio externo")
        void getPercentage_WhenExternalFails_ShouldNotSaveToCache() {
            when(percentageCache.get()).thenReturn(Optional.of(5.0));

            percentageService.getPercentage();

            verify(percentageCache, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Simulación de fallos intermitentes")
    class IntermittentFailures {

        @Test
        @DisplayName("Debe alternar entre servicio externo y caché según disponibilidad")
        void getPercentage_ShouldHandleIntermittentFailures() {
            when(percentageProvider.getPercentage()).thenReturn(10.0);
            Double result1 = percentageService.getPercentage();
            assertEquals(10.0, result1);
            verify(percentageCache, times(1)).save(10.0);

            reset(percentageProvider, percentageCache);
            when(percentageProvider.getPercentage())
                    .thenThrow(new ExternalServiceException("Fallo temporal"));
            when(percentageCache.get()).thenReturn(Optional.of(10.0));

            Double result2 = percentageService.getPercentage();
            assertEquals(10.0, result2);
            verify(percentageCache, times(1)).get();

            reset(percentageProvider, percentageCache);
            when(percentageProvider.getPercentage()).thenReturn(12.0);

            Double result3 = percentageService.getPercentage();
            assertEquals(12.0, result3);
            verify(percentageCache, times(1)).save(12.0);
        }
    }
}
