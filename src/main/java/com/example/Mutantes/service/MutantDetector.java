package com.example.Mutantes.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Servicio optimizado para detección de mutantes mediante análisis de secuencias de ADN.
 *
 * Implementa un algoritmo de alto rendimiento con las siguientes características:
 * - Early Termination: Retorna inmediatamente al encontrar 2 secuencias
 * - Single Pass: Recorre la matriz una sola vez verificando todas las direcciones
 * - Space O(1): No usa estructuras auxiliares dinámicas
 * - Time O(N²) en el peor caso, pero optimizado con boundary checking
 *
 * @author Sistema de Detección de Mutantes
 * @version 1.0
 */
@Service
public class MutantDetector {

    /**
     * Set estático para validación rápida de caracteres válidos en O(1).
     * Contiene únicamente las bases nitrogenadas válidas del ADN.
     */
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    /**
     * Longitud de secuencia requerida para considerar un patrón mutante.
     */
    private static final int SEQUENCE_LENGTH = 4;

    /**
     * Número mínimo de secuencias mutantes requeridas para clasificar como mutante.
     */
    private static final int MUTANT_THRESHOLD = 2;

    /**
     * Determina si un ADN pertenece a un mutante.
     *
     * Un humano es considerado mutante si tiene MÁS DE UNA secuencia de 4 letras
     * idénticas consecutivas en cualquier dirección (horizontal, vertical, diagonal).
     *
     * Optimizaciones implementadas:
     * - Early Termination: Retorna true inmediatamente al encontrar 2 secuencias
     * - Conversión a char[][] para acceso O(1)
     * - Boundary checking para evitar búsquedas innecesarias
     * - Single pass con verificación de múltiples direcciones por celda
     *
     * @param dna Array de Strings representando cada fila del ADN (NxN)
     * @return true si es mutante (>1 secuencia), false en caso contrario
     * @throws IllegalArgumentException si la matriz no es cuadrada (NxN)
     */
    public boolean isMutant(String[] dna) {
        // VALIDACIÓN 1: Fail Fast - null o vacío
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;

        // VALIDACIÓN 2: Matriz debe ser NxN (cuadrada)
        // También validamos que ninguna fila sea null o tenga longitud incorrecta
        for (String row : dna) {
            if (row == null || row.length() != n) {
                throw new IllegalArgumentException(
                    "La matriz de ADN debe ser cuadrada (NxN). Tamaño esperado: " + n + "x" + n
                );
            }
        }

        // OPTIMIZACIÓN 1: Conversión a char[][] para acceso O(1)
        // Evita el overhead de String.charAt() en cada acceso
        char[][] matrix = new char[n][n];

        // VALIDACIÓN 3: Solo caracteres válidos (A, T, C, G)
        // Se valida durante la conversión para no hacer múltiples pasadas
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                char base = dna[i].charAt(j);
                if (!VALID_BASES.contains(base)) {
                    throw new IllegalArgumentException(
                        "Carácter inválido encontrado en posición [" + i + "][" + j + "]: '" + base +
                        "'. Solo se permiten: A, T, C, G"
                    );
                }
                matrix[i][j] = base;
            }
        }

        // OPTIMIZACIÓN 2: Contador para Early Termination
        // En cuanto sequenceCount > 1, retornamos true inmediatamente
        int sequenceCount = 0;

        // OPTIMIZACIÓN 3: Single Pass Algorithm
        // Recorremos la matriz una sola vez, verificando todas las direcciones posibles
        // desde cada celda con boundary checking
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                char currentBase = matrix[i][j];

                // DIRECCIÓN 1: HORIZONTAL (→)
                // Boundary Check: Solo buscar si quedan al menos 4 columnas
                if (j <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, i, j, 0, 1, currentBase)) {
                        sequenceCount++;
                        // EARLY TERMINATION: Retornar inmediatamente al encontrar 2da secuencia
                        if (sequenceCount >= MUTANT_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // DIRECCIÓN 2: VERTICAL (↓)
                // Boundary Check: Solo buscar si quedan al menos 4 filas
                if (i <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, i, j, 1, 0, currentBase)) {
                        sequenceCount++;
                        // EARLY TERMINATION
                        if (sequenceCount >= MUTANT_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // DIRECCIÓN 3: DIAGONAL PRINCIPAL (↘)
                // Boundary Check: Solo buscar si quedan al menos 4 filas Y 4 columnas
                if (i <= n - SEQUENCE_LENGTH && j <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, i, j, 1, 1, currentBase)) {
                        sequenceCount++;
                        // EARLY TERMINATION
                        if (sequenceCount >= MUTANT_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // DIRECCIÓN 4: DIAGONAL INVERTIDA (↙)
                // Boundary Check: Solo buscar si quedan al menos 4 filas Y hay al menos 3 columnas previas
                if (i <= n - SEQUENCE_LENGTH && j >= SEQUENCE_LENGTH - 1) {
                    if (checkSequence(matrix, i, j, 1, -1, currentBase)) {
                        sequenceCount++;
                        // EARLY TERMINATION
                        if (sequenceCount >= MUTANT_THRESHOLD) {
                            return true;
                        }
                    }
                }
            }
        }

        // Si terminamos el recorrido sin encontrar 2 o más secuencias, no es mutante
        return false;
    }

    /**
     * Verifica si existe una secuencia de 4 caracteres idénticos en una dirección específica.
     *
     * OPTIMIZACIÓN: Método inline-friendly con complejidad O(1) ya que siempre verifica
     * exactamente 4 caracteres. No usa bucles dinámicos para mejor performance.
     *
     * @param matrix Matriz de caracteres del ADN
     * @param startRow Fila inicial
     * @param startCol Columna inicial
     * @param rowDir Dirección en filas (-1, 0, 1)
     * @param colDir Dirección en columnas (-1, 0, 1)
     * @param expectedBase Carácter base esperado en la secuencia
     * @return true si se encuentra una secuencia válida de 4 caracteres idénticos
     */
    private boolean checkSequence(char[][] matrix, int startRow, int startCol,
                                   int rowDir, int colDir, char expectedBase) {
        // OPTIMIZACIÓN: Desenrollado de bucle (loop unrolling) para mejor performance
        // Verificamos exactamente 4 posiciones sin overhead de iteración

        // Posición 1 ya verificada (es expectedBase por definición)
        // Posición 2
        if (matrix[startRow + rowDir][startCol + colDir] != expectedBase) {
            return false;
        }
        // Posición 3
        if (matrix[startRow + 2 * rowDir][startCol + 2 * colDir] != expectedBase) {
            return false;
        }
        // Posición 4
        if (matrix[startRow + 3 * rowDir][startCol + 3 * colDir] != expectedBase) {
            return false;
        }

        // Si todas las 4 posiciones tienen el mismo carácter, es una secuencia válida
        return true;
    }

    /**
     * Método auxiliar para debugging y testing.
     * Cuenta el número total de secuencias mutantes sin early termination.
     *
     * @param dna Array de Strings representando cada fila del ADN
     * @return Número total de secuencias mutantes encontradas
     */
    public int countMutantSequences(String[] dna) {
        if (dna == null || dna.length == 0) {
            return 0;
        }

        int n = dna.length;

        for (String row : dna) {
            if (row == null || row.length() != n) {
                throw new IllegalArgumentException("La matriz de ADN debe ser cuadrada (NxN)");
            }
        }

        char[][] matrix = new char[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                char base = dna[i].charAt(j);
                if (!VALID_BASES.contains(base)) {
                    throw new IllegalArgumentException("Carácter inválido: " + base);
                }
                matrix[i][j] = base;
            }
        }

        int count = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                char currentBase = matrix[i][j];

                if (j <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, i, j, 0, 1, currentBase)) {
                        count++;
                    }
                }

                if (i <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, i, j, 1, 0, currentBase)) {
                        count++;
                    }
                }

                if (i <= n - SEQUENCE_LENGTH && j <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, i, j, 1, 1, currentBase)) {
                        count++;
                    }
                }

                if (i <= n - SEQUENCE_LENGTH && j >= SEQUENCE_LENGTH - 1) {
                    if (checkSequence(matrix, i, j, 1, -1, currentBase)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }
}

