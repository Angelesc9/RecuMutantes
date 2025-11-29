package com.example.Mutantes.controller;

import com.example.Mutantes.dto.DnaRequest;
import com.example.Mutantes.dto.StatsResponse;
import com.example.Mutantes.service.MutantService;
import com.example.Mutantes.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la detección de mutantes y estadísticas del sistema.
 *
 * Expone dos endpoints principales:
 * - POST /mutant: Analiza una secuencia de ADN y determina si es mutante
 * - GET /stats: Retorna estadísticas del sistema (cantidad de mutantes vs humanos)
 *
 * Los códigos de respuesta HTTP siguen las especificaciones del examen:
 * - 200 OK: ADN mutante detectado o estadísticas retornadas
 * - 403 FORBIDDEN: ADN humano (no mutante)
 * - 400 BAD REQUEST: Request inválido (validación fallida)
 * - 500 INTERNAL SERVER ERROR: Error del servidor
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Mutant Detector API", description = "API para detección de mutantes mediante análisis de ADN")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    /**
     * Analiza una secuencia de ADN para determinar si pertenece a un mutante.
     *
     * Un humano es mutante si tiene más de una secuencia de cuatro letras iguales
     * consecutivas (horizontal, vertical o diagonal) en su ADN.
     *
     * CÓDIGOS DE RESPUESTA:
     * - 200 OK: El ADN analizado pertenece a un mutante
     * - 403 FORBIDDEN: El ADN analizado pertenece a un humano (no mutante)
     * - 400 BAD REQUEST: El formato del ADN es inválido
     *
     * @param request DTO con la secuencia de ADN a analizar
     * @return ResponseEntity con código 200 si es mutante, 403 si no lo es
     */
    @PostMapping("/mutant")
    @Operation(
        summary = "Detectar si un ADN es mutante",
        description = "Analiza una secuencia de ADN representada como una matriz NxN y determina si pertenece a un mutante. " +
                      "Un mutante tiene más de una secuencia de 4 letras iguales consecutivas (horizontal, vertical o diagonal)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "ADN mutante detectado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "ADN humano (no mutante)",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Request inválido - El formato del ADN no cumple con los requisitos (debe ser matriz NxN con solo caracteres A, T, C, G)",
            content = @Content
        )
    })
    public ResponseEntity<Void> detectMutant(@Valid @RequestBody DnaRequest request) {
        // Analizar el ADN usando el servicio
        boolean isMutant = mutantService.analyzeDna(request.getDna());

        // Retornar respuesta según el resultado
        if (isMutant) {
            // 200 OK: Es mutante
            return ResponseEntity.ok().build();
        } else {
            // 403 FORBIDDEN: No es mutante (humano)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Obtiene las estadísticas del sistema de detección de mutantes.
     *
     * Retorna información agregada sobre:
     * - Cantidad de ADN mutantes verificados
     * - Cantidad de ADN humanos verificados
     * - Ratio (proporción) de mutantes respecto a humanos
     *
     * EJEMPLO DE RESPUESTA:
     * {
     *   "count_mutant_dna": 40,
     *   "count_human_dna": 100,
     *   "ratio": 0.4
     * }
     *
     * @return ResponseEntity con las estadísticas del sistema
     */
    @GetMapping("/stats")
    @Operation(
        summary = "Obtener estadísticas del sistema",
        description = "Retorna las estadísticas de verificaciones de ADN realizadas, incluyendo conteos de mutantes, " +
                      "humanos y el ratio calculado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estadísticas obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StatsResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<StatsResponse> getStats() {
        // Obtener estadísticas del servicio
        StatsResponse stats = statsService.getStats();

        // Retornar estadísticas con código 200 OK
        return ResponseEntity.ok(stats);
    }
}

