package com.example.Mutantes.service;

import com.example.Mutantes.dto.StatsResponse;
import com.example.Mutantes.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión y cálculo de estadísticas del sistema de detección de mutantes.
 *
 * Proporciona información agregada sobre:
 * - Cantidad de ADN de mutantes detectados
 * - Cantidad de ADN de humanos detectados
 * - Ratio (proporción) de mutantes respecto a humanos
 *
 * Este servicio es fundamental para el endpoint /stats del Nivel 3 del examen.
 */
@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Obtiene las estadísticas actuales del sistema.
     *
     * Calcula y retorna:
     * - count_mutant_dna: Total de ADN mutantes detectados
     * - count_human_dna: Total de ADN humanos detectados
     * - ratio: Proporción calculada como mutantes / humanos
     *
     * MANEJO DE EDGE CASES:
     * - Si no hay humanos (count_human_dna = 0):
     *   → ratio = 0 (evita ArithmeticException por división por cero)
     * - Si no hay mutantes pero sí humanos:
     *   → ratio = 0.0
     * - Si no hay ningún registro:
     *   → count_mutant_dna = 0, count_human_dna = 0, ratio = 0.0
     *
     * CASTING A DOUBLE:
     * Se realiza casting explícito a double antes de la división para
     * garantizar precisión decimal en el resultado.
     *
     * @return StatsResponse con las estadísticas actuales del sistema
     */
    public StatsResponse getStats() {
        // Consultar cantidad de mutantes
        long mutantCount = dnaRecordRepository.countByIsMutant(true);

        // Consultar cantidad de humanos
        long humanCount = dnaRecordRepository.countByIsMutant(false);

        // Calcular ratio con manejo de división por cero
        double ratio = calculateRatio(mutantCount, humanCount);

        // Construir y retornar el DTO de respuesta
        return StatsResponse.builder()
                .count_mutant_dna(mutantCount)
                .count_human_dna(humanCount)
                .ratio(ratio)
                .build();
    }

    /**
     * Calcula el ratio (proporción) de mutantes respecto a humanos.
     *
     * FÓRMULA: ratio = mutantes / humanos
     *
     * CASOS ESPECIALES:
     * - humanos = 0: Retorna 0.0 para evitar división por cero
     * - mutantes = 0: Retorna 0.0 (proporción válida)
     * - Ambos > 0: Retorna el ratio calculado
     *
     * EJEMPLOS:
     * - calculateRatio(40, 100) → 0.4 (40% de mutantes)
     * - calculateRatio(10, 0) → 0.0 (evita división por cero)
     * - calculateRatio(0, 100) → 0.0 (0% de mutantes)
     *
     * @param mutantCount Cantidad de mutantes detectados
     * @param humanCount Cantidad de humanos detectados
     * @return Ratio calculado como double, o 0.0 si humanCount es 0
     */
    private double calculateRatio(long mutantCount, long humanCount) {
        // Manejo de división por cero
        if (humanCount == 0) {
            return 0.0;
        }

        // Casting explícito a double para garantizar precisión decimal
        // Si no hacemos el cast, la división sería entera (ej: 1/2 = 0)
        return (double) mutantCount / (double) humanCount;
    }

    /**
     * Método auxiliar para obtener solo el conteo de mutantes.
     * Útil para métricas y monitoreo.
     *
     * @return Cantidad total de mutantes detectados
     */
    public long getMutantCount() {
        return dnaRecordRepository.countByIsMutant(true);
    }

    /**
     * Método auxiliar para obtener solo el conteo de humanos.
     * Útil para métricas y monitoreo.
     *
     * @return Cantidad total de humanos detectados
     */
    public long getHumanCount() {
        return dnaRecordRepository.countByIsMutant(false);
    }

    /**
     * Método auxiliar para obtener el total de análisis realizados.
     * Útil para métricas y monitoreo.
     *
     * @return Cantidad total de análisis de ADN realizados (mutantes + humanos)
     */
    public long getTotalAnalysisCount() {
        return dnaRecordRepository.count();
    }
}

