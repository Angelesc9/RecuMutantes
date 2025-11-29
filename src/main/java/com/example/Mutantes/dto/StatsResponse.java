package com.example.Mutantes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de estadísticas del sistema de detección de mutantes.
 *
 * Representa el formato JSON requerido por el endpoint /stats con los campos:
 * - count_mutant_dna: Cantidad de ADN mutantes detectados
 * - count_human_dna: Cantidad de ADN humanos detectados
 * - ratio: Proporción de mutantes vs humanos
 *
 * Ejemplo de respuesta JSON:
 * {
 *   "count_mutant_dna": 40,
 *   "count_human_dna": 100,
 *   "ratio": 0.4
 * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsResponse {

    /**
     * Cantidad total de ADN de mutantes detectados en el sistema.
     */
    private long count_mutant_dna;

    /**
     * Cantidad total de ADN de humanos (no mutantes) detectados en el sistema.
     */
    private long count_human_dna;

    /**
     * Ratio calculado como: count_mutant_dna / count_human_dna
     *
     * Representa la proporción de mutantes respecto a humanos.
     * Ejemplo: 0.4 significa que hay 40% de mutantes respecto al total de humanos.
     *
     * Si no hay humanos registrados (count_human_dna = 0), el ratio se establece en 0
     * para evitar división por cero.
     */
    private double ratio;
}

