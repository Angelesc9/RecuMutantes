package com.example.Mutantes.dto;

import com.example.Mutantes.validator.ValidDnaSequence;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de análisis de ADN en el endpoint POST /mutant.
 *
 * Contiene la secuencia de ADN a analizar con validaciones automáticas
 * para garantizar que el formato sea correcto antes de procesarlo.
 *
 * Ejemplo de request JSON válido:
 * <pre>
 * {
 *   "dna": [
 *     "ATGCGA",
 *     "CAGTGC",
 *     "TTATGT",
 *     "AGAAGG",
 *     "CCCCTA",
 *     "TCACTG"
 *   ]
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de análisis de ADN para detectar mutantes")
public class DnaRequest {

    /**
     * Secuencia de ADN representada como un array de Strings.
     *
     * Cada string representa una fila de la matriz NxN de ADN.
     * Solo se permiten los caracteres A, T, C, G (bases nitrogenadas).
     *
     * VALIDACIONES APLICADAS:
     * - @NotNull: El campo no puede ser null
     * - @NotEmpty: El array no puede estar vacío
     * - @ValidDnaSequence: Validación personalizada que verifica:
     *   · Matriz cuadrada (NxN)
     *   · Solo caracteres A, T, C, G
     *   · Todas las filas tienen la misma longitud
     */
    @Schema(
        description = "Matriz NxN de ADN representada como array de strings. Cada string es una fila que debe contener solo los caracteres A, T, C, G",
        example = "[\"ATGCGA\", \"CAGTGC\", \"TTATGT\", \"AGAAGG\", \"CCCCTA\", \"TCACTG\"]",
        required = true,
        minLength = 1
    )
    @NotNull(message = "El campo 'dna' no puede ser null")
    @NotEmpty(message = "El campo 'dna' no puede estar vacío")
    @ValidDnaSequence
    private String[] dna;
}

