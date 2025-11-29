package com.example.Mutantes.service;

import com.example.Mutantes.dto.StatsResponse;
import com.example.Mutantes.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para StatsService.
 *
 * Verifica el cálculo de estadísticas y el manejo de edge cases como división por cero.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StatsService - Tests de Estadísticas")
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        // Configuración común si es necesaria
    }

    @Test
    @DisplayName("Debe calcular estadísticas correctamente con mutantes y humanos")
    void testGetStats_WithMutantsAndHumans() {
        // Given
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertNotNull(stats);
        assertEquals(40L, stats.getCount_mutant_dna());
        assertEquals(100L, stats.getCount_human_dna());
        assertEquals(0.4, stats.getRatio(), 0.001); // 40/100 = 0.4

        verify(dnaRecordRepository, times(1)).countByIsMutant(true);
        verify(dnaRecordRepository, times(1)).countByIsMutant(false);
    }

    @Test
    @DisplayName("Debe manejar división por cero cuando no hay humanos")
    void testGetStats_NoHumans_RatioIsZero() {
        // Given - Solo mutantes, sin humanos
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertNotNull(stats);
        assertEquals(10L, stats.getCount_mutant_dna());
        assertEquals(0L, stats.getCount_human_dna());
        assertEquals(0.0, stats.getRatio()); // Debe ser 0, no lanzar excepción
    }

    @Test
    @DisplayName("Debe retornar ratio 0 cuando no hay mutantes")
    void testGetStats_NoMutants_RatioIsZero() {
        // Given - Solo humanos, sin mutantes
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertNotNull(stats);
        assertEquals(0L, stats.getCount_mutant_dna());
        assertEquals(50L, stats.getCount_human_dna());
        assertEquals(0.0, stats.getRatio()); // 0/50 = 0.0
    }

    @Test
    @DisplayName("Debe retornar todos ceros cuando no hay registros")
    void testGetStats_NoRecords_AllZeros() {
        // Given - Base de datos vacía
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertNotNull(stats);
        assertEquals(0L, stats.getCount_mutant_dna());
        assertEquals(0L, stats.getCount_human_dna());
        assertEquals(0.0, stats.getRatio());
    }

    @Test
    @DisplayName("Debe calcular ratio correctamente con números grandes")
    void testGetStats_LargeNumbers() {
        // Given
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1000L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(2500L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertEquals(1000L, stats.getCount_mutant_dna());
        assertEquals(2500L, stats.getCount_human_dna());
        assertEquals(0.4, stats.getRatio(), 0.001); // 1000/2500 = 0.4
    }

    @Test
    @DisplayName("Debe calcular ratio con decimales precisos")
    void testGetStats_PreciseDecimalRatio() {
        // Given - Ratio que resulta en decimal periódico
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(3L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertEquals(1L, stats.getCount_mutant_dna());
        assertEquals(3L, stats.getCount_human_dna());
        assertEquals(0.3333333333333333, stats.getRatio(), 0.0001); // 1/3 ≈ 0.333...
    }

    @Test
    @DisplayName("Debe retornar ratio 1.0 cuando hay igual cantidad de mutantes y humanos")
    void testGetStats_EqualMutantsAndHumans() {
        // Given
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(50L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertEquals(50L, stats.getCount_mutant_dna());
        assertEquals(50L, stats.getCount_human_dna());
        assertEquals(1.0, stats.getRatio(), 0.001); // 50/50 = 1.0
    }

    @Test
    @DisplayName("Debe retornar ratio mayor a 1 cuando hay más mutantes que humanos")
    void testGetStats_MoreMutantsThanHumans() {
        // Given
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(150L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertEquals(150L, stats.getCount_mutant_dna());
        assertEquals(100L, stats.getCount_human_dna());
        assertEquals(1.5, stats.getRatio(), 0.001); // 150/100 = 1.5
    }

    @Test
    @DisplayName("getMutantCount debe retornar el conteo correcto")
    void testGetMutantCount() {
        // Given
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(25L);

        // When
        long count = statsService.getMutantCount();

        // Then
        assertEquals(25L, count);
        verify(dnaRecordRepository, times(1)).countByIsMutant(true);
    }

    @Test
    @DisplayName("getHumanCount debe retornar el conteo correcto")
    void testGetHumanCount() {
        // Given
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(75L);

        // When
        long count = statsService.getHumanCount();

        // Then
        assertEquals(75L, count);
        verify(dnaRecordRepository, times(1)).countByIsMutant(false);
    }

    @Test
    @DisplayName("getTotalAnalysisCount debe retornar el total de análisis")
    void testGetTotalAnalysisCount() {
        // Given
        when(dnaRecordRepository.count()).thenReturn(100L);

        // When
        long count = statsService.getTotalAnalysisCount();

        // Then
        assertEquals(100L, count);
        verify(dnaRecordRepository, times(1)).count();
    }

    @Test
    @DisplayName("Debe construir StatsResponse con Builder correctamente")
    void testStatsResponseBuilder() {
        // Given
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(20L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then - Verificar que el builder de Lombok funcionó correctamente
        assertNotNull(stats);
        assertTrue(stats.getCount_mutant_dna() >= 0);
        assertTrue(stats.getCount_human_dna() >= 0);
        assertTrue(stats.getRatio() >= 0);
    }

    @Test
    @DisplayName("Debe manejar caso extremo de 1 mutante y 1 humano")
    void testGetStats_OneOfEach() {
        // Given
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(1L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertEquals(1L, stats.getCount_mutant_dna());
        assertEquals(1L, stats.getCount_human_dna());
        assertEquals(1.0, stats.getRatio(), 0.001); // 1/1 = 1.0
    }

    @Test
    @DisplayName("Debe manejar caso con muchos más humanos que mutantes")
    void testGetStats_FewMutantsManyHumans() {
        // Given - Proporción muy baja
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(5L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(1000L);

        // When
        StatsResponse stats = statsService.getStats();

        // Then
        assertEquals(5L, stats.getCount_mutant_dna());
        assertEquals(1000L, stats.getCount_human_dna());
        assertEquals(0.005, stats.getRatio(), 0.0001); // 5/1000 = 0.005 (0.5%)
    }
}

