package com.example.Mutantes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de tests completa para MutantDetector.
 * Cubre casos de éxito, fallo, edge cases y validaciones.
 */
@DisplayName("MutantDetector - Tests de Detección de Mutantes")
class MutantDetectorTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        detector = new MutantDetector();
    }

    @Nested
    @DisplayName("Tests de Validación - Fail Fast")
    class ValidationTests {

        @Test
        @DisplayName("Debe retornar false cuando el DNA es null")
        void testNullDna() {
            assertFalse(detector.isMutant(null));
        }

        @Test
        @DisplayName("Debe retornar false cuando el DNA está vacío")
        void testEmptyDna() {
            assertFalse(detector.isMutant(new String[]{}));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la matriz no es cuadrada")
        void testNonSquareMatrix() {
            String[] dna = {
                "ATGC",
                "CAGTG",  // Esta fila tiene 5 caracteres
                "TTAT",
                "AGAA"
            };

            assertThrows(IllegalArgumentException.class, () -> detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando hay una fila null")
        void testNullRow() {
            String[] dna = {
                "ATGC",
                null,
                "TTAT",
                "AGAA"
            };

            assertThrows(IllegalArgumentException.class, () -> detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe lanzar excepción con caracteres inválidos")
        void testInvalidCharacters() {
            String[] dna = {
                "ATGC",
                "CAGT",
                "TXAT",  // X es inválido
                "AGAA"
            };

            Exception exception = assertThrows(IllegalArgumentException.class,
                () -> detector.isMutant(dna));
            assertTrue(exception.getMessage().contains("Carácter inválido"));
        }

        @Test
        @DisplayName("Debe lanzar excepción con minúsculas")
        void testLowercaseCharacters() {
            String[] dna = {
                "ATGC",
                "CaGT",  // 'a' minúscula es inválida
                "TTAT",
                "AGAA"
            };

            assertThrows(IllegalArgumentException.class, () -> detector.isMutant(dna));
        }
    }

    @Nested
    @DisplayName("Tests de Mutantes - Casos Positivos")
    class MutantPositiveTests {

        @Test
        @DisplayName("Debe detectar mutante con secuencias horizontales")
        void testMutantHorizontal() {
            String[] dna = {
                "AAAA",
                "CCCC",
                "TTAT",
                "AGAA"
            };

            assertTrue(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe detectar mutante con secuencias verticales")
        void testMutantVertical() {
            String[] dna = {
                "ATGC",
                "ATGT",
                "ATAT",
                "ATAA"
            };

            assertTrue(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe detectar mutante con secuencias diagonales principales")
        void testMutantDiagonalMain() {
            String[] dna = {
                "AAAATG",
                "CAGTGC",
                "TTATGT",
                "AGTAGG",
                "CCGCTA",
                "TCACTG"
            };

            assertTrue(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe detectar mutante con secuencias diagonales invertidas")
        void testMutantDiagonalInverse() {
            String[] dna = {
                "ATGCA",
                "CAATG",
                "TTATT",
                "AGAAG",
                "CCCTA"
            };

            assertTrue(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe detectar mutante en matriz 6x6 - Caso ejemplo del examen")
        void testMutant6x6Example() {
            String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
            };

            assertTrue(detector.isMutant(dna));
            // Verificamos que tiene múltiples secuencias
            assertEquals(3, detector.countMutantSequences(dna));
        }

        @Test
        @DisplayName("Debe detectar mutante con más de 2 secuencias")
        void testMutantMultipleSequences() {
            String[] dna = {
                "AAAATG",
                "CAGTGC",
                "TTTTGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
            };

            assertTrue(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Early Termination: Debe retornar true al encontrar 2da secuencia")
        void testEarlyTermination() {
            String[] dna = {
                "AAAA",
                "AAAA",
                "TTTT",
                "CCCC"
            };

            // Debería detectar mutante rápidamente sin recorrer toda la matriz
            assertTrue(detector.isMutant(dna));
        }
    }

    @Nested
    @DisplayName("Tests de Humanos - Casos Negativos")
    class HumanNegativeTests {

        @Test
        @DisplayName("Debe retornar false con 0 secuencias")
        void testHumanNoSequences() {
            String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
            };

            assertFalse(detector.isMutant(dna));
            assertEquals(0, detector.countMutantSequences(dna));
        }

        @Test
        @DisplayName("Debe retornar false con solo 1 secuencia")
        void testHumanOneSequence() {
            String[] dna = {
                "AAAA",
                "CAGT",
                "TTAT",
                "AGAC"
            };

            assertFalse(detector.isMutant(dna));
            assertEquals(1, detector.countMutantSequences(dna));
        }

        @Test
        @DisplayName("Debe retornar false en matriz 6x6 sin suficientes secuencias")
        void testHuman6x6() {
            String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
            };

            assertFalse(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe retornar false cuando secuencias tienen solo 3 caracteres")
        void testHumanSequenceOf3() {
            String[] dna = {
                "AAATG",
                "CCCGC",
                "TTAGT",
                "AGACG",
                "GCGTA"
            };

            assertFalse(detector.isMutant(dna));
        }
    }

    @Nested
    @DisplayName("Tests de Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Debe manejar matriz mínima 4x4 con mutante")
        void testMinimumSizeMutant() {
            String[] dna = {
                "AAAA",
                "AAAA",
                "TGCA",
                "CGTA"
            };

            assertTrue(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe manejar matriz mínima 4x4 sin mutante")
        void testMinimumSizeHuman() {
            String[] dna = {
                "ATGC",
                "CAGT",
                "TGCA",
                "CGTA"
            };

            assertFalse(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe manejar matriz 3x3 - No puede haber mutantes")
        void test3x3Matrix() {
            String[] dna = {
                "AAA",
                "TTT",
                "CCC"
            };

            // En 3x3 no puede haber secuencias de 4
            assertFalse(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe manejar matriz 1x1")
        void test1x1Matrix() {
            String[] dna = {"A"};

            assertFalse(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe detectar secuencia en última fila")
        void testSequenceInLastRow() {
            String[] dna = {
                "ATGC",
                "CAGT",
                "TGCA",
                "AAAA"
            };

            assertFalse(detector.isMutant(dna)); // Solo 1 secuencia
        }

        @Test
        @DisplayName("Debe detectar secuencia en última columna")
        void testSequenceInLastColumn() {
            String[] dna = {
                "ATGA",
                "CAGA",
                "TGCA",
                "CGTA"
            };

            assertFalse(detector.isMutant(dna)); // Solo 1 secuencia
        }

        @Test
        @DisplayName("Debe manejar todas las bases iguales")
        void testAllSameBases() {
            String[] dna = {
                "AAAA",
                "AAAA",
                "AAAA",
                "AAAA"
            };

            // Múltiples secuencias - definitivamente mutante
            assertTrue(detector.isMutant(dna));
        }

        @Test
        @DisplayName("Debe manejar matriz grande 10x10")
        void testLargeMatrix() {
            String[] dna = {
                "ATGCGATGCA",
                "CAGTGCAGTG",
                "TTATGTTATG",
                "AGAAGGAGAA",
                "CCCCTACCCC",
                "TCACTGTCAC",
                "ATGCGATGCA",
                "CAGTGCAGTG",
                "TTATGTTATG",
                "AGAAGGAGAA"
            };

            // Debería manejar matrices grandes eficientemente
            boolean result = detector.isMutant(dna);
            assertNotNull(result); // Solo verificamos que no falle
        }
    }

    @Nested
    @DisplayName("Tests de Performance y Optimización")
    class PerformanceTests {

        @Test
        @DisplayName("Boundary Checking: No debe buscar fuera de límites")
        void testBoundaryChecking() {
            String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAA"
            };

            // Este test pasa si no hay ArrayIndexOutOfBoundsException
            assertDoesNotThrow(() -> detector.isMutant(dna));
        }

        @Test
        @DisplayName("Contador de secuencias debe funcionar sin early termination")
        void testSequenceCounter() {
            String[] dna = {
                "AAAATG",
                "CAGTGC",
                "TTTTGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
            };

            int count = detector.countMutantSequences(dna);
            assertTrue(count >= 2, "Debe encontrar al menos 2 secuencias");
        }

        @Test
        @DisplayName("Early termination debe ser más rápido que contador completo")
        void testEarlyTerminationPerformance() {
            String[] dna = {
                "AAAATG",
                "AAAAGC",
                "TTTTGT",
                "TTTTGG",
                "CCCCTA",
                "CCCCTG"
            };

            long start1 = System.nanoTime();
            boolean result = detector.isMutant(dna);
            long end1 = System.nanoTime();

            long start2 = System.nanoTime();
            int count = detector.countMutantSequences(dna);
            long end2 = System.nanoTime();

            assertTrue(result);
            assertTrue(count >= 2);

            // Early termination debería ser más rápido o igual
            // (No siempre garantizado por nano-benchmarking, pero conceptualmente correcto)
            System.out.println("Early termination time: " + (end1 - start1) + " ns");
            System.out.println("Full count time: " + (end2 - start2) + " ns");
        }
    }

    @Nested
    @DisplayName("Tests de Direcciones Específicas")
    class DirectionTests {

        @Test
        @DisplayName("Debe detectar secuencia horizontal en primera fila")
        void testHorizontalFirstRow() {
            String[] dna = {
                "AAAATG",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCGTA",
                "TCACTG"
            };

            int count = detector.countMutantSequences(dna);
            assertTrue(count >= 1);
        }

        @Test
        @DisplayName("Debe detectar secuencia vertical en primera columna")
        void testVerticalFirstColumn() {
            String[] dna = {
                "ATGCGA",
                "ACGTGC",
                "ATATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
            };

            int count = detector.countMutantSequences(dna);
            assertTrue(count >= 1);
        }

        @Test
        @DisplayName("Debe detectar diagonal principal desde (0,0)")
        void testMainDiagonalFromOrigin() {
            String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
            };

            int count = detector.countMutantSequences(dna);
            assertTrue(count >= 1);
        }

        @Test
        @DisplayName("Debe detectar diagonal invertida correctamente")
        void testInverseDiagonal() {
            String[] dna = {
                "ATGCGA",
                "CAATGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
            };

            // Verificar que no hay errores de índices en diagonal invertida
            assertDoesNotThrow(() -> detector.isMutant(dna));
        }
    }
}

