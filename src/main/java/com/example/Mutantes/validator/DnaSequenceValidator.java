package com.example.Mutantes.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class DnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[ATCG]+$");

    @Override
    public void initialize(ValidDnaSequence constraintAnnotation) {
        // no initialization needed
    }

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        // Array no debe ser nulo ni vacío
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;

        // Validar matriz cuadrada NxN y caracteres válidos
        for (String row : dna) {
            if (row == null) {
                return false;
            }
            // Verificar que sea cuadrada: cada fila debe tener longitud == n
            if (row.length() != n) {
                return false;
            }
            // Verificar que solo contenga A, T, C, G
            if (!VALID_PATTERN.matcher(row).matches()) {
                return false;
            }
        }
        return true;
    }
}

