package com.example.Mutantes.controller;

import com.example.Mutantes.dto.DnaRequest;
import com.example.Mutantes.dto.StatsResponse;
import com.example.Mutantes.service.MutantService;
import com.example.Mutantes.service.StatsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Tag(name = "Mutant API", description = "Endpoints para detectar mutantes y obtener estadísticas de ADN")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    public MutantController(MutantService mutantService, StatsService statsService) {
        this.mutantService = mutantService;
        this.statsService = statsService;
    }

    @PostMapping("/mutant")
    @Operation(
        summary = "Detecta si un ADN pertenece a un mutante",
        description = "Recibe una secuencia de ADN y determina si pertenece a un mutante. Retorna 200 si es mutante, 403 si no lo es."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Es un mutante"),
        @ApiResponse(responseCode = "403", description = "No es un mutante"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida - ADN mal formado")
    })
    public ResponseEntity<Void> isMutant(@Valid @RequestBody DnaRequest request) {
        boolean mutant = mutantService.analyzeDna(request.getDna());
        if (mutant) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/stats")
    @Operation(
        summary = "Obtiene estadísticas de verificaciones de ADN",
        description = "Retorna las estadísticas de mutantes vs humanos y el ratio"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<StatsResponse> stats() {
        StatsResponse response = statsService.getStats();
        return ResponseEntity.ok(response);
    }
}

