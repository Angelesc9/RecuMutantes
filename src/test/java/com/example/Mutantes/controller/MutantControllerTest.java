package com.example.Mutantes.controller;

import com.example.Mutantes.dto.DnaRequest;
import com.example.Mutantes.service.MutantService;
import com.example.Mutantes.service.StatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para MutantController.
 *
 * Usa @WebMvcTest para tests enfocados en la capa web sin levantar el servidor completo.
 */
@WebMvcTest(MutantController.class)
@DisplayName("MutantController - Tests de API REST")
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MutantService mutantService;

    @MockitoBean
    private StatsService statsService;

    @Test
    @DisplayName("POST /mutant debe retornar 200 OK cuando el DNA es mutante")
    void testDetectMutant_ReturnsOkWhenMutant() throws Exception {
        // Given
        String[] mutantDna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        DnaRequest request = new DnaRequest(mutantDna);

        when(mutantService.analyzeDna(any(String[].class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 403 FORBIDDEN cuando el DNA es humano")
    void testDetectMutant_ReturnsForbiddenWhenHuman() throws Exception {
        // Given
        String[] humanDna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        DnaRequest request = new DnaRequest(humanDna);

        when(mutantService.analyzeDna(any(String[].class))).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST cuando el DNA es null")
    void testDetectMutant_ReturnsBadRequestWhenDnaIsNull() throws Exception {
        // Given
        DnaRequest request = new DnaRequest(null);

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dna").exists());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST cuando el DNA está vacío")
    void testDetectMutant_ReturnsBadRequestWhenDnaIsEmpty() throws Exception {
        // Given
        DnaRequest request = new DnaRequest(new String[]{});

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dna").exists());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST cuando la matriz no es cuadrada")
    void testDetectMutant_ReturnsBadRequestWhenMatrixNotSquare() throws Exception {
        // Given
        String[] invalidDna = {
                "ATGC",
                "CAGTG",  // Esta fila tiene 5 caracteres
                "TTAT",
                "AGAA"
        };
        DnaRequest request = new DnaRequest(invalidDna);

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dna").exists());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST cuando hay caracteres inválidos")
    void testDetectMutant_ReturnsBadRequestWhenInvalidCharacters() throws Exception {
        // Given
        String[] invalidDna = {
                "ATGX",  // X es inválido
                "CAGT",
                "TTAT",
                "AGAA"
        };
        DnaRequest request = new DnaRequest(invalidDna);

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dna").exists());
    }

    @Test
    @DisplayName("GET /stats debe retornar 200 OK con las estadísticas")
    void testGetStats_ReturnsOkWithStats() throws Exception {
        // Given
        com.example.Mutantes.dto.StatsResponse stats =
            com.example.Mutantes.dto.StatsResponse.builder()
                .count_mutant_dna(40L)
                .count_human_dna(100L)
                .ratio(0.4)
                .build();

        when(statsService.getStats()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }

    @Test
    @DisplayName("GET /stats debe retornar estadísticas incluso cuando no hay registros")
    void testGetStats_ReturnsStatsWhenNoRecords() throws Exception {
        // Given
        com.example.Mutantes.dto.StatsResponse stats =
            com.example.Mutantes.dto.StatsResponse.builder()
                .count_mutant_dna(0L)
                .count_human_dna(0L)
                .ratio(0.0)
                .build();

        when(statsService.getStats()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));
    }

    @Test
    @DisplayName("POST /mutant debe aceptar DNA 4x4 válido")
    void testDetectMutant_Accepts4x4Matrix() throws Exception {
        // Given
        String[] dna4x4 = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAA"
        };
        DnaRequest request = new DnaRequest(dna4x4);

        when(mutantService.analyzeDna(any(String[].class))).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());  // No es mutante, pero el request es válido
    }

    @Test
    @DisplayName("POST /mutant debe rechazar DNA con minúsculas")
    void testDetectMutant_RejectsLowercaseCharacters() throws Exception {
        // Given
        String[] invalidDna = {
                "atgc",  // Minúsculas no permitidas
                "CAGT",
                "TTAT",
                "AGAA"
        };
        DnaRequest request = new DnaRequest(invalidDna);

        // When & Then
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dna").exists());
    }
}

