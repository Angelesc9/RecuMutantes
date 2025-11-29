package com.example.Mutantes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de tests unitarios para MutantDetector.
 *
 * Tests puros sin Spring Boot ni Mocks para máxima velocidad.
 * Instancia directa de la clase para testear el algoritmo puro.
 *
 * Cobertura:
 * - Casos mutantes (horizontal, vertical, diagonales, múltiples)
 * - Casos humanos (sin secuencias, una secuencia exacta)
 * - Validaciones (null, vacío, matriz no cuadrada, caracteres inválidos)
 * - Casos de borde (matrices pequeñas y grandes)
 */
@DisplayName("MutantDetector - Suite Completa de Tests Unitarios")
class MutantDetectorTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        // Instancia directa sin Spring para tests rápidos
        detector = new MutantDetector();
    }

    // ========== CASOS MUTANTES (Debe retornar true) ==========

    @Test
    @DisplayName("Debe detectar mutante con secuencia HORIZONTAL")
    void testMutantHorizontal() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTTTGT",  // Secuencia horizontal de 4 T's
                "AGAAGG",
                "CCCCTA",  // Otra secuencia horizontal de 4 C's
                "TCACTG"
        };

        assertTrue(detector.isMutant(dna), "Debe detectar mutante con secuencias horizontales");
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencia VERTICAL")
    void testMutantVertical() {
        String[] dna = {
                "ATGCGA",
                "ACGTGC",
                "ATATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        // Columna 0: A-A-A-A (primera secuencia)
        // Hay otra secuencia en fila 4

        assertTrue(detector.isMutant(dna), "Debe detectar mutante con secuencias verticales");
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencia DIAGONAL PRINCIPAL (↘)")
    void testMutantDiagonalPrincipal() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGTAGG",
                "CCCCTA",
                "TCACTG"
        };
        // Diagonal: A(0,0) - A(1,1) - A(2,2) - A(3,3)
        // Y secuencia horizontal en fila 4

        assertTrue(detector.isMutant(dna), "Debe detectar mutante con diagonal principal");
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencia DIAGONAL INVERTIDA (↙)")
    void testMutantDiagonalInvertida() {
        String[] dna = {
                "ATGCGA",
                "CAATGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        // Diagonal invertida desde posición (0,3): G-T-T-A
        // Y secuencia horizontal en fila 4

        assertTrue(detector.isMutant(dna), "Debe detectar mutante con diagonal invertida");
    }

    @Test
    @DisplayName("Debe detectar mutante con MÚLTIPLES SECUENCIAS en distintas direcciones")
    void testMutantMultipleSequences() {
        String[] dna = {
                "AAAATG",  // Horizontal: 4 A's
                "CAGTGC",
                "TTTTGT",  // Horizontal: 4 T's
                "AGAAGG",
                "CCCCTA",  // Horizontal: 4 C's
                "TCACTG"
        };

        assertTrue(detector.isMutant(dna), "Debe detectar mutante con múltiples secuencias");
    }

    @Test
    @DisplayName("Debe detectar mutante en matriz 6x6 - CASO EJEMPLO DEL EXAMEN")
    void testMutantExampleFromExam() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        assertTrue(detector.isMutant(dna), "Debe detectar el caso ejemplo del examen como mutante");
    }

    // ========== CASOS HUMANOS (Debe retornar false) ==========

    @Test
    @DisplayName("Debe retornar false cuando NO hay ninguna secuencia")
    void testHumanNoSequence() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };

        assertFalse(detector.isMutant(dna), "Debe retornar false cuando no hay secuencias mutantes");
    }

    @Test
    @DisplayName("Debe retornar false cuando hay EXACTAMENTE UNA secuencia (CRÍTICO: 1 secuencia = Humano)")
    void testHumanOneSequence() {
        String[] dna = {
                "AAAATG",  // Solo UNA secuencia de 4 A's
                "CAGTGC",
                "TTATGT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };

        assertFalse(detector.isMutant(dna), "CRÍTICO: Una sola secuencia significa HUMANO (necesita >1 para ser mutante)");
    }

    @Test
    @DisplayName("Debe retornar false cuando las secuencias tienen solo 3 caracteres iguales")
    void testHumanSequenceOfThree() {
        String[] dna = {
                "AAATGA",  // Solo 3 A's (no es secuencia válida)
                "CAGTGC",
                "TTATGT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };

        assertFalse(detector.isMutant(dna), "Secuencias de 3 caracteres no cuentan");
    }

    // ========== CASOS DE VALIDACIÓN ==========

    @Test
    @DisplayName("Debe retornar false cuando el DNA es NULL")
    void testNullDna() {
        assertFalse(detector.isMutant(null), "DNA null debe retornar false (fail fast)");
    }

    @Test
    @DisplayName("Debe retornar false cuando el DNA está VACÍO")
    void testEmptyDna() {
        String[] emptyDna = {};
        assertFalse(detector.isMutant(emptyDna), "DNA vacío debe retornar false");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la matriz NO es cuadrada (NxM)")
    void testNxM() {
        String[] nonSquareDna = {
                "ATGC",
                "CAGTG",   // Esta fila tiene 5 caracteres (4x5 no es cuadrada)
                "TTAT",
                "AGAA"
        };

        assertThrows(IllegalArgumentException.class,
                () -> detector.isMutant(nonSquareDna),
                "Debe lanzar excepción cuando la matriz no es cuadrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando hay caracteres INVÁLIDOS")
    void testInvalidData() {
        String[] invalidDna = {
                "ATGC",
                "CXGT",  // 'X' es inválido
                "TTAT",
                "AGAA"
        };

        assertThrows(IllegalArgumentException.class,
                () -> detector.isMutant(invalidDna),
                "Debe lanzar excepción con caracteres inválidos");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando una fila es NULL")
    void testNullRow() {
        String[] dnaWithNullRow = {
                "ATGC",
                null,     // Fila null
                "TTAT",
                "AGAA"
        };

        assertThrows(IllegalArgumentException.class,
                () -> detector.isMutant(dnaWithNullRow),
                "Debe lanzar excepción cuando hay una fila null");
    }

    @Test
    @DisplayName("Debe rechazar letras MINÚSCULAS")
    void testLowercaseCharacters() {
        String[] lowercaseDna = {
                "atgc",  // Minúsculas no permitidas
                "CAGT",
                "TTAT",
                "AGAA"
        };

        assertThrows(IllegalArgumentException.class,
                () -> detector.isMutant(lowercaseDna),
                "Debe rechazar caracteres en minúscula");
    }

    // ========== CASOS DE BORDE Y RENDIMIENTO ==========

    @Test
    @DisplayName("Debe funcionar con matriz MÍNIMA 4x4 - Mutante")
    void testSmallMatrixMutant() {
        String[] dna4x4 = {
                "AAAA",  // Secuencia horizontal
                "AAAA",  // Otra secuencia horizontal
                "TGCA",
                "CGTA"
        };

        assertTrue(detector.isMutant(dna4x4), "Debe detectar mutante en matriz 4x4");
    }

    @Test
    @DisplayName("Debe funcionar con matriz MÍNIMA 4x4 - Humano")
    void testSmallMatrixHuman() {
        String[] dna4x4 = {
                "ATGC",
                "CAGT",
                "TGCA",
                "CGTA"
        };

        assertFalse(detector.isMutant(dna4x4), "Debe retornar false cuando no hay secuencias en 4x4");
    }

    @Test
    @DisplayName("Debe manejar matriz GRANDE 100x100 sin StackOverflow")
    void testLargeMatrix() {
        // Generar matriz 100x100 programáticamente
        int size = 100;
        String[] largeDna = new String[size];

        // Crear filas con patrón alternado para evitar secuencias mutantes
        for (int i = 0; i < size; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < size; j++) {
                // Patrón: A-T-C-G repetido para evitar secuencias
                char[] bases = {'A', 'T', 'C', 'G'};
                row.append(bases[(i + j) % 4]);
            }
            largeDna[i] = row.toString();
        }

        // Solo verificamos que NO lance StackOverflowError
        assertDoesNotThrow(() -> detector.isMutant(largeDna),
                "Debe manejar matrices grandes sin StackOverflow");
    }

    @Test
    @DisplayName("Matriz 100x100 con mutante debe detectarlo eficientemente")
    void testLargeMatrixWithMutant() {
        int size = 100;
        String[] largeDna = new String[size];

        for (int i = 0; i < size; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < size; j++) {
                if (i == 0 && j < 4) {
                    row.append('A');  // Primera secuencia horizontal
                } else if (i == 1 && j < 4) {
                    row.append('T');  // Segunda secuencia horizontal
                } else {
                    char[] bases = {'A', 'T', 'C', 'G'};
                    row.append(bases[(i + j) % 4]);
                }
            }
            largeDna[i] = row.toString();
        }

        assertTrue(detector.isMutant(largeDna),
                "Debe detectar mutante en matriz grande eficientemente");
    }

    @Test
    @DisplayName("Matriz 3x3 NO puede tener secuencias de 4")
    void testTooSmallMatrix() {
        String[] dna3x3 = {
                "AAA",
                "TTT",
                "CCC"
        };

        assertFalse(detector.isMutant(dna3x3),
                "Matriz 3x3 no puede tener secuencias de 4");
    }

    @Test
    @DisplayName("Matriz 1x1 debe retornar false")
    void testSingleCell() {
        String[] dna1x1 = {"A"};

        assertFalse(detector.isMutant(dna1x1), "Matriz 1x1 debe retornar false");
    }

    @Test
    @DisplayName("Todas las bases iguales debe detectar múltiples secuencias")
    void testAllSameBases() {
        String[] allSame = {
                "AAAA",
                "AAAA",
                "AAAA",
                "AAAA"
        };

        assertTrue(detector.isMutant(allSame),
                "Matriz con todas las bases iguales debe tener múltiples secuencias");
    }
}

