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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MutantService.
 *
 * Verifica la lógica de caché y la integración con MutantDetector y DnaRecordRepository.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MutantService - Tests de Lógica de Negocio")
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
        mutantDna = new String[]{
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

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
    @DisplayName("Debe analizar DNA y guardarlo cuando NO existe en caché")
    void testAnalyzeDna_NotInCache_SavesAndReturnsMutant() {
        // Given - No existe en caché
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = mutantService.analyzeDna(mutantDna);

        // Then
        assertTrue(result);
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe retornar resultado cacheado cuando DNA ya existe (MUTANTE)")
    void testAnalyzeDna_InCache_ReturnsCachedMutantResult() {
        // Given - Existe en caché como mutante
        DnaRecord cachedRecord = DnaRecord.builder()
                .id(1L)
                .dnaHash("some_hash")
                .isMutant(true)
                .build();

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        // When
        boolean result = mutantService.analyzeDna(mutantDna);

        // Then
        assertTrue(result);
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
        verify(mutantDetector, never()).isMutant(any()); // NO debe llamar al detector
        verify(dnaRecordRepository, never()).save(any()); // NO debe guardar
    }

    @Test
    @DisplayName("Debe retornar resultado cacheado cuando DNA ya existe (HUMANO)")
    void testAnalyzeDna_InCache_ReturnsCachedHumanResult() {
        // Given - Existe en caché como humano
        DnaRecord cachedRecord = DnaRecord.builder()
                .id(2L)
                .dnaHash("another_hash")
                .isMutant(false)
                .build();

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        // When
        boolean result = mutantService.analyzeDna(humanDna);

        // Then
        assertFalse(result);
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
        verify(mutantDetector, never()).isMutant(any()); // NO debe llamar al detector
        verify(dnaRecordRepository, never()).save(any()); // NO debe guardar
    }

    @Test
    @DisplayName("Debe analizar y guardar DNA humano cuando NO existe en caché")
    void testAnalyzeDna_NotInCache_SavesAndReturnsHuman() {
        // Given - No existe en caché
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = mutantService.analyzeDna(humanDna);

        // Then
        assertFalse(result);
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando el DNA es inválido")
    void testAnalyzeDna_InvalidDna_ThrowsException() {
        // Given - DNA inválido
        String[] invalidDna = {"ATGX", "CAGT", "TTAT", "AGAA"};
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(invalidDna)).thenThrow(new IllegalArgumentException("Carácter inválido"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> mutantService.analyzeDna(invalidDna));
        verify(dnaRecordRepository, never()).save(any()); // NO debe guardar si hay error
    }

    @Test
    @DisplayName("Debe generar el mismo hash para el mismo DNA")
    void testGetDnaHash_SameDna_GeneratesSameHash() {
        // When
        String hash1 = mutantService.getDnaHash(mutantDna);
        String hash2 = mutantService.getDnaHash(mutantDna);

        // Then
        assertNotNull(hash1);
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length()); // SHA-256 produce 64 caracteres hexadecimales
    }

    @Test
    @DisplayName("Debe generar diferentes hashes para DNAs diferentes")
    void testGetDnaHash_DifferentDna_GeneratesDifferentHashes() {
        // When
        String hash1 = mutantService.getDnaHash(mutantDna);
        String hash2 = mutantService.getDnaHash(humanDna);

        // Then
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Debe guardar registro con hash correcto")
    void testAnalyzeDna_SavesRecordWithCorrectHash() {
        // Given
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);

        DnaRecord[] capturedRecord = new DnaRecord[1];
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> {
            capturedRecord[0] = invocation.getArgument(0);
            return capturedRecord[0];
        });

        // When
        mutantService.analyzeDna(mutantDna);

        // Then
        assertNotNull(capturedRecord[0]);
        assertNotNull(capturedRecord[0].getDnaHash());
        assertEquals(64, capturedRecord[0].getDnaHash().length());
        assertTrue(capturedRecord[0].isMutant());
    }

    @Test
    @DisplayName("Debe llamar al detector solo una vez por DNA único")
    void testAnalyzeDna_CallsDetectorOnlyOncePerUniqueDna() {
        // Given
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Analizar el mismo DNA múltiples veces
        mutantService.analyzeDna(mutantDna);

        // Simular que ahora está en caché
        DnaRecord cachedRecord = DnaRecord.builder()
                .dnaHash("hash")
                .isMutant(true)
                .build();
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        mutantService.analyzeDna(mutantDna);
        mutantService.analyzeDna(mutantDna);

        // Then - El detector solo se llamó una vez (la primera)
        verify(mutantDetector, times(1)).isMutant(mutantDna);
    }

    @Test
    @DisplayName("Debe manejar DNA con diferentes tamaños de matriz")
    void testAnalyzeDna_HandlesMatricesOfDifferentSizes() {
        // Given - DNA 4x4
        String[] smallDna = {"ATGC", "CAGT", "TTAT", "AGAA"};
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(smallDna)).thenReturn(false);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = mutantService.analyzeDna(smallDna);

        // Then
        assertFalse(result);
        verify(mutantDetector, times(1)).isMutant(smallDna);
    }

    @Test
    @DisplayName("Hash debe ser consistente incluso con orden diferente (normalización)")
    void testGetDnaHash_NormalizesOrder() {
        // Given - Mismo DNA pero en diferente orden
        String[] dna1 = {"ATGC", "CAGT", "TTAT", "AGAA"};
        String[] dna2 = {"AGAA", "ATGC", "CAGT", "TTAT"};

        // When
        String hash1 = mutantService.getDnaHash(dna1);
        String hash2 = mutantService.getDnaHash(dna2);

        // Then - Deben generar el mismo hash debido a la normalización (sort)
        assertEquals(hash1, hash2);
    }
}

