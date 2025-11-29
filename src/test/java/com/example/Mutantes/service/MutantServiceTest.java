package com.example.Mutantes.service;

import com.example.Mutantes.entity.DnaRecord;
import com.example.Mutantes.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para MutantService con Mockito.
 *
 * Verifica:
 * - Detección de mutantes y humanos
 * - Persistencia de resultados en base de datos
 * - Sistema de caché (cache hit para evitar análisis duplicados)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MutantService - Tests con Mocks")
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    private String[] mutantDna;
    private String[] humanDna;

    @BeforeEach
    void setUp() {
        // DNA de mutante (ejemplo estándar)
        mutantDna = new String[]{
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        // DNA de humano (sin suficientes secuencias)
        humanDna = new String[]{
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
    }

    @Test
    @DisplayName("Analizar DNA mutante: debe guardar con isMutant=true")
    void testAnalyzeDnaMutant() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = mutantService.analyzeDna(mutantDna);

        // Assert
        assertTrue(result, "Debe retornar true para mutante");

        // Verificar que se llamó al detector
        verify(mutantDetector, times(1)).isMutant(mutantDna);

        // Verificar que se guardó el registro con isMutant=true
        verify(dnaRecordRepository, times(1)).save(argThat(record ->
            record.isMutant() == true
        ));
    }

    @Test
    @DisplayName("Analizar DNA humano: debe guardar con isMutant=false")
    void testAnalyzeDnaHuman() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = mutantService.analyzeDna(humanDna);

        // Assert
        assertFalse(result, "Debe retornar false para humano");

        // Verificar que se llamó al detector
        verify(mutantDetector, times(1)).isMutant(humanDna);

        // Verificar que se guardó el registro con isMutant=false
        verify(dnaRecordRepository, times(1)).save(argThat(record ->
            record.isMutant() == false
        ));
    }

    @Test
    @DisplayName("Cache Hit: no debe llamar al detector si el DNA ya existe en BD")
    void testCacheHit() {
        // Arrange
        DnaRecord cachedRecord = DnaRecord.builder()
            .dnaHash("abc123")
            .isMutant(true)
            .build();

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        // Act
        boolean result = mutantService.analyzeDna(mutantDna);

        // Assert
        assertTrue(result, "Debe retornar el resultado del caché");

        // CRÍTICO: Verificar que NO se llamó al detector (ahorro de cómputo)
        verify(mutantDetector, never()).isMutant(any());

        // CRÍTICO: Verificar que NO se guardó ningún registro nuevo
        verify(dnaRecordRepository, never()).save(any());

        // Verificar que SÍ se consultó el caché
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
    }

    @Test
    @DisplayName("Cache Hit para humano: debe retornar false del caché")
    void testCacheHitHuman() {
        // Arrange
        DnaRecord cachedRecord = DnaRecord.builder()
            .dnaHash("def456")
            .isMutant(false)
            .build();

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        // Act
        boolean result = mutantService.analyzeDna(humanDna);

        // Assert
        assertFalse(result, "Debe retornar false del caché para humano");
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Hash único: diferentes DNAs deben generar hashes diferentes")
    void testDifferentDnasGenerateDifferentHashes() {
        // Arrange
        String[] dna1 = {"ATGC", "CGTA", "TACG", "GCAT"};
        String[] dna2 = {"AAAA", "TTTT", "CCCC", "GGGG"};

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any())).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        mutantService.analyzeDna(dna1);
        mutantService.analyzeDna(dna2);

        // Assert - Se debe llamar findByDnaHash 2 veces con diferentes hashes
        verify(dnaRecordRepository, times(2)).findByDnaHash(anyString());
        verify(dnaRecordRepository, times(2)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Mismo DNA múltiples veces: solo debe analizar una vez")
    void testSameDnaMultipleTimes() {
        // Arrange - Primera llamada no tiene caché
        when(dnaRecordRepository.findByDnaHash(anyString()))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(DnaRecord.builder().dnaHash("hash").isMutant(true).build()));

        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Llamar dos veces con el mismo DNA
        mutantService.analyzeDna(mutantDna);
        mutantService.analyzeDna(mutantDna);

        // Assert - El detector solo debe llamarse UNA vez
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }
}

