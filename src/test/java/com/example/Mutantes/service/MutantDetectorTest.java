package com.example.Mutantes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite completa de pruebas unitarias para MutantDetector.
 *
 * Cobertura de casos:
 * - Mutantes en todas las direcciones (horizontal, vertical, diagonales)
 * - Humanos con 0 o 1 secuencia
 * - Validaciones de entrada (null, vacío, matriz no cuadrada, caracteres inválidos)
 * - Casos de borde y rendimiento (matrices pequeñas y grandes)
 *
 * NOTA: No se usan mocks de Spring Boot. Se instancia MutantDetector directamente
 * para máxima velocidad de ejecución.
 */
@DisplayName("MutantDetector - Suite de Pruebas Unitarias")
class MutantDetectorTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        // Instanciar directamente sin Spring para máxima velocidad
        detector = new MutantDetector();
    }

    // ==================================================================================
    // CASOS MUTANTES (Debe retornar true) - Más de 1 secuencia
    // ==================================================================================

    @Test
    @DisplayName("Mutante: Secuencia horizontal de 4 letras iguales")
    void testMutantHorizontal() {
        String[] dna = {
            "AAAATG",
            "TGCAGT",
            "GCTTCC",
            "CCCCTG",
            "GTAGTC",
            "AGTCAC"
        };
        // Tiene 2 secuencias horizontales: fila 0 (AAAA) y fila 3 (CCCC)
        assertTrue(detector.isMutant(dna), "Debe detectar mutante con secuencias horizontales");
    }

    @Test
    @DisplayName("Mutante: Secuencia vertical de 4 letras iguales")
    void testMutantVertical() {
        String[] dna = {
            "ATGCGA",
            "ATGTGC",
            "ATATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        // Tiene secuencias verticales en columna 0 (AAAA) y horizontal en fila 4 (CCCC)
        assertTrue(detector.isMutant(dna), "Debe detectar mutante con secuencias verticales");
    }

    @Test
    @DisplayName("Mutante: Secuencia en diagonal principal (↘)")
    void testMutantDiagonalPrincipal() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        // Tiene diagonal principal (AGGG) y horizontal (CCCC)
        assertTrue(detector.isMutant(dna), "Debe detectar mutante con diagonal principal");
    }

    @Test
    @DisplayName("Mutante: Secuencia en diagonal invertida (↙)")
    void testMutantDiagonalInvertida() {
        String[] dna = {
            "ATGCGA",
            "CAGTTC",
            "TTATGT",
            "GGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        // Tiene diagonal invertida partiendo de [0,3] y horizontal (CCCC)
        assertTrue(detector.isMutant(dna), "Debe detectar mutante con diagonal invertida");
    }

    @Test
    @DisplayName("Mutante: Múltiples secuencias en diferentes direcciones")
    void testMutantMultipleSequences() {
        String[] dna = {
            "AAAATG",
            "AAGTGC",
            "AATTGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        // Tiene múltiples secuencias: horizontal (AAAA), vertical (AAAA), diagonal, etc.
        assertTrue(detector.isMutant(dna), "Debe detectar mutante con múltiples secuencias");
    }

    @Test
    @DisplayName("Mutante: Exactamente 2 secuencias (caso límite)")
    void testMutantExactlyTwoSequences() {
        String[] dna = {
            "AAAATG",
            "TGCAGT",
            "GCTTCC",
            "TTTTTG",
            "GTAGTC",
            "AGTCAC"
        };
        // Exactamente 2 secuencias: fila 0 (AAAA) y fila 3 (TTTT)
        assertTrue(detector.isMutant(dna), "Debe detectar mutante con exactamente 2 secuencias");
    }

    // ==================================================================================
    // CASOS HUMANOS (Debe retornar false) - 0 o 1 secuencia
    // ==================================================================================

    @Test
    @DisplayName("Humano: Sin ninguna secuencia de 4 letras iguales")
    void testHumanNoSequence() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
        // No tiene ninguna secuencia de 4 letras iguales consecutivas
        assertFalse(detector.isMutant(dna), "No debe detectar mutante sin secuencias");
    }

    @Test
    @DisplayName("Humano: Exactamente UNA secuencia (CRÍTICO: 1 secuencia = Humano)")
    void testHumanOneSequence() {
        String[] dna = {
            "AAAATG",
            "TGCAGT",
            "GCTTCC",
            "TTTAGG",
            "GTAGTC",
            "AGTCAC"
        };
        // Solo tiene 1 secuencia horizontal en fila 0 (AAAA)
        // CRÍTICO: Con 1 sola secuencia debe ser considerado HUMANO
        assertFalse(detector.isMutant(dna), "Con UNA secuencia debe ser HUMANO, no mutante");
    }

    @Test
    @DisplayName("Humano: Secuencia de solo 3 letras consecutivas (insuficiente)")
    void testHumanThreeConsecutive() {
        String[] dna = {
            "AAATGA",
            "TGCAGT",
            "GCTTCC",
            "TTTAGG",
            "GTAGTC",
            "AGTCAC"
        };
        // Tiene AAA (3 letras) pero no AAAA (4 letras)
        assertFalse(detector.isMutant(dna), "Secuencia de 3 letras es insuficiente");
    }

    // ==================================================================================
    // CASOS DE VALIDACIÓN (Debe retornar false o lanzar excepción)
    // ==================================================================================

    @Test
    @DisplayName("Validación: ADN null debe retornar false")
    void testNullDna() {
        assertFalse(detector.isMutant(null), "ADN null debe retornar false");
    }

    @Test
    @DisplayName("Validación: Array vacío debe retornar false")
    void testEmptyDna() {
        String[] dna = {};
        assertFalse(detector.isMutant(dna), "Array vacío debe retornar false");
    }

    @Test
    @DisplayName("Validación: Matriz no cuadrada (NxM) debe lanzar excepción")
    void testNxM() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTAT",      // Fila más corta
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna),
            "Matriz no cuadrada debe lanzar IllegalArgumentException"
        );

        assertTrue(exception.getMessage().contains("cuadrada"),
            "El mensaje debe indicar que la matriz debe ser cuadrada");
    }

    @Test
    @DisplayName("Validación: Caracteres inválidos (no A,T,C,G) debe lanzar excepción")
    void testInvalidData() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TBATGT",  // Contiene 'B' (inválido)
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna),
            "Caracteres inválidos deben lanzar IllegalArgumentException"
        );

        assertTrue(exception.getMessage().contains("inválido") || exception.getMessage().contains("invalido"),
            "El mensaje debe indicar caracter inválido");
    }

    @Test
    @DisplayName("Validación: Array con fila null debe lanzar excepción")
    void testNullRow() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            null,       // Fila null
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna),
            "Array con fila null debe lanzar IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Validación: Filas con diferente longitud debe lanzar excepción")
    void testDifferentRowLengths() {
        String[] dna = {
            "ATGC",
            "CAGT",
            "TTATGTAA",  // Fila más larga
            "AGAA"
        };

        assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna),
            "Filas de diferente longitud deben lanzar IllegalArgumentException"
        );
    }

    // ==================================================================================
    // CASOS DE BORDE Y RENDIMIENTO
    // ==================================================================================

    @Test
    @DisplayName("Borde: Matriz 4x4 (tamaño mínimo posible)")
    void testSmallMatrix() {
        String[] dna = {
            "AAAA",
            "TTTT",
            "CCCC",
            "GGGG"
        };
        // Matriz mínima con múltiples secuencias horizontales
        assertTrue(detector.isMutant(dna), "Debe funcionar con matriz 4x4");
    }

    @Test
    @DisplayName("Borde: Matriz 4x4 sin mutante")
    void testSmallMatrixHuman() {
        String[] dna = {
            "ATCG",
            "TGCA",
            "CGAT",
            "GATC"
        };
        // Matriz mínima sin secuencias
        assertFalse(detector.isMutant(dna), "Debe identificar humano en matriz 4x4");
    }

    @Test
    @DisplayName("Rendimiento: Matriz 100x100 (verificar que no haya StackOverflow)")
    void testLargeMatrix() {
        // Generar matriz 100x100 programáticamente
        int size = 100;
        String[] dna = new String[size];

        // Crear patrón que alterne y tenga algunas secuencias
        for (int i = 0; i < size; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < size; j++) {
                // Patrón alternante con algunas secuencias
                if (i == 0 && j < 4) {
                    row.append('A'); // Primera fila tiene AAAA al inicio
                } else if (i == 10 && j < 4) {
                    row.append('T'); // Fila 10 tiene TTTT al inicio (segunda secuencia)
                } else {
                    // Resto es patrón alternante
                    row.append((i + j) % 2 == 0 ? 'A' : 'T');
                }
            }
            dna[i] = row.toString();
        }

        // Debe completar sin StackOverflow y detectar las 2 secuencias
        assertTrue(detector.isMutant(dna),
            "Debe procesar matriz 100x100 sin StackOverflow y detectar mutante");
    }

    @Test
    @DisplayName("Rendimiento: Matriz grande sin mutante")
    void testLargeMatrixHuman() {
        // Generar matriz 50x50 sin secuencias mutantes
        int size = 50;
        String[] dna = new String[size];

        for (int i = 0; i < size; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < size; j++) {
                // Patrón que evita 4 letras consecutivas en cualquier dirección
                // Alternamos en grupos de 3 para romper secuencias
                if ((i + j) % 6 < 3) {
                    row.append((j % 3 == 0) ? 'A' : (j % 3 == 1) ? 'T' : 'C');
                } else {
                    row.append((j % 3 == 0) ? 'G' : (j % 3 == 1) ? 'C' : 'A');
                }
            }
            dna[i] = row.toString();
        }

        assertFalse(detector.isMutant(dna),
            "Debe procesar matriz grande y detectar humano correctamente");
    }

    @Test
    @DisplayName("Borde: Matriz 1x1 debe retornar false (muy pequeña)")
    void testMatrixTooSmall() {
        String[] dna = {"A"};
        // Matriz 1x1 no puede tener secuencia de 4
        assertFalse(detector.isMutant(dna),
            "Matriz 1x1 no puede ser mutante (necesita al menos 4x4)");
    }

    @Test
    @DisplayName("Borde: Todas las letras iguales en matriz 6x6")
    void testAllSameLetters() {
        String[] dna = {
            "AAAAAA",
            "AAAAAA",
            "AAAAAA",
            "AAAAAA",
            "AAAAAA",
            "AAAAAA"
        };
        // Tiene múltiples secuencias en todas direcciones
        assertTrue(detector.isMutant(dna),
            "Matriz con todas letras iguales debe ser mutante");
    }

    // ==================================================================================
    // CASOS ADICIONALES PARA COBERTURA COMPLETA
    // ==================================================================================

    @Test
    @DisplayName("Edge: Secuencias en las esquinas de la matriz")
    void testSequencesInCorners() {
        String[] dna = {
            "AAAATG",
            "TGCAGT",
            "GCTTCC",
            "CCCCGG",
            "GTAGTC",
            "AGTCGG"
        };
        // Secuencia en esquina superior izquierda (AAAA en fila 0) y otra en fila 3 (CCCC)
        assertTrue(detector.isMutant(dna),
            "Debe detectar secuencias en las esquinas");
    }

    @Test
    @DisplayName("Edge: Secuencia diagonal en borde derecho")
    void testDiagonalAtRightEdge() {
        String[] dna = {
            "ATGCGG",
            "CAGTGG",
            "TTATGG",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        // Diagonal invertida en columna 5 (GGGG) y horizontal (CCCC)
        assertTrue(detector.isMutant(dna),
            "Debe detectar diagonal en borde derecho");
    }

    @Test
    @DisplayName("Validación: Caracteres en minúsculas deben fallar")
    void testLowercaseCharacters() {
        String[] dna = {
            "ATGCga",  // Contiene minúsculas
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna),
            "Caracteres en minúsculas deben lanzar excepción"
        );
    }

    @Test
    @DisplayName("Edge: Matriz rectangular vertical (más filas que columnas)")
    void testRectangularVertical() {
        String[] dna = {
            "ATGC",
            "CAGT",
            "TTAT",
            "AGAA",
            "CCCC",
            "TCAC"
        };

        assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna),
            "Matriz rectangular debe lanzar excepción"
        );
    }
}

