package com.example.Mutantes.controller;

import com.example.Mutantes.dto.StatsResponse;
import com.example.Mutantes.service.MutantService;
import com.example.Mutantes.service.StatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de pruebas de integración para MutantController.
 *
 * Usa @WebMvcTest para cargar solo el contexto web (más rápido que @SpringBootTest).
 *
 * Verifica:
 * - Códigos de estado HTTP correctos (200, 403, 400)
 * - Validación de entrada (JSON inválido, caracteres incorrectos)
 * - Respuestas JSON correctas
 *
 * CRÍTICO PARA LOS 12 PUNTOS DE API REST:
 * - POST /mutant retorna 200 si es mutante
 * - POST /mutant retorna 403 si es humano
 * - POST /mutant retorna 400 si el JSON es inválido
 * - GET /stats retorna 200 con JSON correcto
 */
@WebMvcTest(MutantController.class)
@DisplayName("MutantController - Tests de Integración")
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /mutant con DNA mutante debe retornar 200 OK")
    void testMutantReturns200() throws Exception {
        // Arrange
        String mutantDnaJson = """
            {
                "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
            }
            """;

        when(mutantService.analyzeDna(any(String[].class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutantDnaJson))
                .andExpect(status().isOk());

        // Verificar que se llamó al servicio
        verify(mutantService, times(1)).analyzeDna(any(String[].class));
    }

    @Test
    @DisplayName("POST /mutant con DNA humano debe retornar 403 FORBIDDEN")
    void testHumanReturns403() throws Exception {
        // Arrange
        String humanDnaJson = """
            {
                "dna": ["ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"]
            }
            """;

        when(mutantService.analyzeDna(any(String[].class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(humanDnaJson))
                .andExpect(status().isForbidden());

        // Verificar que se llamó al servicio
        verify(mutantService, times(1)).analyzeDna(any(String[].class));
    }

    @Test
    @DisplayName("POST /mutant con caracteres inválidos (Z) debe retornar 400 BAD REQUEST")
    void testInvalidDnaReturns400() throws Exception {
        // Arrange - DNA con caracteres inválidos
        String invalidDnaJson = """
            {
                "dna": ["ATGCGA", "CAZTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidDnaJson))
                .andExpect(status().isBadRequest());

        // Verificar que NO se llamó al servicio (falló en validación)
        verify(mutantService, never()).analyzeDna(any(String[].class));
    }

    @Test
    @DisplayName("POST /mutant con DNA null debe retornar 400 BAD REQUEST")
    void testNullDnaReturns400() throws Exception {
        // Arrange
        String nullDnaJson = """
            {
                "dna": null
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nullDnaJson))
                .andExpect(status().isBadRequest());

        verify(mutantService, never()).analyzeDna(any(String[].class));
    }

    @Test
    @DisplayName("POST /mutant con DNA vacío debe retornar 400 BAD REQUEST")
    void testEmptyDnaReturns400() throws Exception {
        // Arrange
        String emptyDnaJson = """
            {
                "dna": []
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyDnaJson))
                .andExpect(status().isBadRequest());

        verify(mutantService, never()).analyzeDna(any(String[].class));
    }

    @Test
    @DisplayName("POST /mutant con matriz no cuadrada debe retornar 400 BAD REQUEST")
    void testNonSquareMatrixReturns400() throws Exception {
        // Arrange
        String nonSquareDnaJson = """
            {
                "dna": ["ATGC", "CAGT", "TTAT", "AGAA", "CCCC", "TCAC"]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nonSquareDnaJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant con JSON malformado debe retornar 400 BAD REQUEST")
    void testMalformedJsonReturns400() throws Exception {
        // Arrange - JSON inválido
        String malformedJson = """
            {
                "dna": ["ATGCGA", "CAGTGC", "TTATGT"
            """;

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /stats debe retornar 200 OK con JSON correcto")
    void testStatsReturns200() throws Exception {
        // Arrange
        StatsResponse statsResponse = StatsResponse.builder()
                .count_mutant_dna(40)
                .count_human_dna(100)
                .ratio(0.4)
                .build();

        when(statsService.getStats()).thenReturn(statsResponse);

        // Act & Assert
        mockMvc.perform(get("/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));

        // Verificar que se llamó al servicio
        verify(statsService, times(1)).getStats();
    }

    @Test
    @DisplayName("GET /stats con 0 registros debe retornar 200 OK")
    void testStatsWithZeroRecords() throws Exception {
        // Arrange
        StatsResponse statsResponse = StatsResponse.builder()
                .count_mutant_dna(0)
                .count_human_dna(0)
                .ratio(0.0)
                .build();

        when(statsService.getStats()).thenReturn(statsResponse);

        // Act & Assert
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));
    }

    @Test
    @DisplayName("POST /mutant con Content-Type incorrecto debe retornar 415")
    void testWrongContentType() throws Exception {
        // Arrange
        String dnaJson = """
            {
                "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.TEXT_PLAIN)
                .content(dnaJson))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /mutant con campo dna con mayúsculas debe funcionar")
    void testDnaFieldCaseSensitivity() throws Exception {
        // Arrange - Usar exactamente el campo "dna" como espera el DTO
        String dnaJson = """
            {
                "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
            }
            """;

        when(mutantService.analyzeDna(any(String[].class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dnaJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant con minúsculas en DNA debe retornar 400")
    void testLowercaseDnaReturns400() throws Exception {
        // Arrange
        String lowercaseDnaJson = """
            {
                "dna": ["atgcga", "cagtgc", "ttatgt", "agaagg", "ccccta", "tcactg"]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lowercaseDnaJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /stats debe retornar JSON con estructura correcta")
    void testStatsJsonStructure() throws Exception {
        // Arrange
        StatsResponse statsResponse = StatsResponse.builder()
                .count_mutant_dna(10)
                .count_human_dna(25)
                .ratio(0.4)
                .build();

        when(statsService.getStats()).thenReturn(statsResponse);

        // Act & Assert - Verificar que los campos tienen el formato correcto
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").exists())
                .andExpect(jsonPath("$.count_human_dna").exists())
                .andExpect(jsonPath("$.ratio").exists())
                .andExpect(jsonPath("$.count_mutant_dna").isNumber())
                .andExpect(jsonPath("$.count_human_dna").isNumber())
                .andExpect(jsonPath("$.ratio").isNumber());
    }
}

