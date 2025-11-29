package com.example.Mutantes.dto;

import com.example.Mutantes.validator.ValidDnaSequence;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnaRequest {

    @Schema(
        description = "Array de strings que representan la matriz NxN del ADN. Cada string es una fila y debe contener solo los caracteres A, T, C, G",
        example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
        required = true
    )
    @NotNull(message = "El campo 'dna' no puede ser nulo")
    @NotEmpty(message = "El campo 'dna' no puede estar vacío")
    @ValidDnaSequence
    private String[] dna;
}

