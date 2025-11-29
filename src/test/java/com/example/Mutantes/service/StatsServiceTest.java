package com.example.Mutantes.service;

import com.example.Mutantes.dto.StatsResponse;
import com.example.Mutantes.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para StatsService con Mockito.
 *
 * Verifica:
 * - Cálculo correcto de estadísticas
 * - Manejo de casos especiales (división por cero)
 * - Precisión del ratio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StatsService - Tests con Mocks")
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Ratio estándar: 40 mutantes, 100 humanos → Ratio 0.4")
    void testRatioStandard() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertNotNull(response, "La respuesta no debe ser null");
        assertEquals(40, response.getCount_mutant_dna(), "Debe tener 40 mutantes");
        assertEquals(100, response.getCount_human_dna(), "Debe tener 100 humanos");
        assertEquals(0.4, response.getRatio(), 0.001, "El ratio debe ser 0.4 (40/100)");

        // Verificar interacciones
        verify(dnaRecordRepository, times(1)).countByIsMutant(true);
        verify(dnaRecordRepository, times(1)).countByIsMutant(false);
    }

    @Test
    @DisplayName("División por cero: 10 mutantes, 0 humanos → Ratio 0.0 (no debe lanzar excepción)")
    void testRatioDivisionByZero() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertNotNull(response, "La respuesta no debe ser null");
        assertEquals(10, response.getCount_mutant_dna(), "Debe tener 10 mutantes");
        assertEquals(0, response.getCount_human_dna(), "Debe tener 0 humanos");
        assertEquals(0.0, response.getRatio(), "El ratio debe ser 0.0 cuando no hay humanos (evita división por cero)");

        // Verificar que no se lanzó excepción
        assertDoesNotThrow(() -> statsService.getStats(),
            "No debe lanzar excepción con división por cero");
    }

    @Test
    @DisplayName("Sin registros: 0 mutantes, 0 humanos → Ratio 0.0")
    void testZeroRecords() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertNotNull(response, "La respuesta no debe ser null");
        assertEquals(0, response.getCount_mutant_dna(), "Debe tener 0 mutantes");
        assertEquals(0, response.getCount_human_dna(), "Debe tener 0 humanos");
        assertEquals(0.0, response.getRatio(), "El ratio debe ser 0.0 cuando no hay registros");
    }

    @Test
    @DisplayName("Solo humanos: 0 mutantes, 50 humanos → Ratio 0.0")
    void testOnlyHumans() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertEquals(0, response.getCount_mutant_dna());
        assertEquals(50, response.getCount_human_dna());
        assertEquals(0.0, response.getRatio(), "El ratio debe ser 0.0 cuando no hay mutantes");
    }

    @Test
    @DisplayName("Solo mutantes: 30 mutantes, 0 humanos → Ratio 0.0")
    void testOnlyMutants() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(30L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertEquals(30, response.getCount_mutant_dna());
        assertEquals(0, response.getCount_human_dna());
        assertEquals(0.0, response.getRatio(), "El ratio debe ser 0.0 para evitar división por cero");
    }

    @Test
    @DisplayName("Ratio con decimales: 1 mutante, 3 humanos → Ratio 0.333...")
    void testRatioWithDecimals() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(3L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertEquals(1, response.getCount_mutant_dna());
        assertEquals(3, response.getCount_human_dna());
        assertEquals(0.333, response.getRatio(), 0.001, "El ratio debe ser aproximadamente 0.333 (1/3)");
    }

    @Test
    @DisplayName("Ratio igual a 1: 50 mutantes, 50 humanos → Ratio 1.0")
    void testRatioEqualsOne() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(50L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertEquals(50, response.getCount_mutant_dna());
        assertEquals(50, response.getCount_human_dna());
        assertEquals(1.0, response.getRatio(), 0.001, "El ratio debe ser 1.0 cuando hay igual cantidad");
    }

    @Test
    @DisplayName("Ratio mayor a 1: 200 mutantes, 100 humanos → Ratio 2.0")
    void testRatioGreaterThanOne() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(200L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertEquals(200, response.getCount_mutant_dna());
        assertEquals(100, response.getCount_human_dna());
        assertEquals(2.0, response.getRatio(), 0.001, "El ratio debe ser 2.0 (200/100)");
    }

    @Test
    @DisplayName("Números grandes: verificar que no hay overflow")
    void testLargeNumbers() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1_000_000L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(2_000_000L);

        // Act
        StatsResponse response = statsService.getStats();

        // Assert
        assertEquals(1_000_000, response.getCount_mutant_dna());
        assertEquals(2_000_000, response.getCount_human_dna());
        assertEquals(0.5, response.getRatio(), 0.001, "El ratio debe ser 0.5 con números grandes");
    }
}

