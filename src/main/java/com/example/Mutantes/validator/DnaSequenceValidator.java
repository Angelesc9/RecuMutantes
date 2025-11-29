package com.example.Mutantes.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Implementación del validador para la anotación {@link ValidDnaSequence}.
 *
 * Verifica que una secuencia de ADN cumpla con los criterios requeridos:
 * 1. No sea null ni vacía
 * 2. Sea una matriz cuadrada (NxN): todas las filas tienen la misma longitud que el número de filas
 * 3. Solo contenga caracteres válidos: A, T, C, G (mayúsculas)
 *
 * La validación se ejecuta antes de que el request llegue al controlador,
 * permitiendo detectar errores de formato tempranamente.
 */
public class DnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    /**
     * Patrón regex para validar que una cadena solo contenga los caracteres A, T, C, G.
     * ^[ATCG]+$ significa:
     * - ^ : inicio de la cadena
     * - [ATCG] : solo estos caracteres
     * - + : uno o más caracteres
     * - $ : fin de la cadena
     */
    private static final Pattern DNA_PATTERN = Pattern.compile("^[ATCG]+$");

    /**
     * Inicializa el validador.
     * Puede usarse para configuraciones adicionales basadas en la anotación.
     *
     * @param constraintAnnotation La anotación ValidDnaSequence
     */
    @Override
    public void initialize(ValidDnaSequence constraintAnnotation) {
        // No requiere inicialización especial
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Valida una secuencia de ADN.
     *
     * CRITERIOS DE VALIDACIÓN:
     * 1. Array no null ni vacío
     * 2. Matriz cuadrada (NxN)
     * 3. Cada fila solo contiene A, T, C, G
     *
     * @param dna Array de Strings representando el ADN
     * @param context Contexto de validación (permite personalizar mensajes)
     * @return true si la secuencia es válida, false en caso contrario
     */
    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        // VALIDACIÓN 1: Array no debe ser null ni vacío
        if (dna == null || dna.length == 0) {
            addCustomMessage(context, "El array de ADN no puede ser null o vacío");
            return false;
        }

        int n = dna.length;

        // VALIDACIÓN 2: Matriz debe ser cuadrada (NxN)
        for (int i = 0; i < n; i++) {
            String row = dna[i];

            // Verificar que la fila no sea null
            if (row == null) {
                addCustomMessage(context, "La fila " + i + " no puede ser null");
                return false;
            }

            // Verificar que la longitud de la fila coincida con N
            if (row.length() != n) {
                addCustomMessage(context,
                    "La matriz debe ser cuadrada (NxN). Esperado: " + n + "x" + n +
                    ", pero la fila " + i + " tiene longitud " + row.length());
                return false;
            }

            // VALIDACIÓN 3: Solo caracteres válidos A, T, C, G
            if (!DNA_PATTERN.matcher(row).matches()) {
                addCustomMessage(context,
                    "La fila " + i + " contiene caracteres inválidos. Solo se permiten: A, T, C, G");
                return false;
            }
        }

        // Todas las validaciones pasaron
        return true;
    }

    /**
     * Agrega un mensaje de error personalizado al contexto de validación.
     *
     * Esto permite que el mensaje de error sea más específico sobre qué falló.
     *
     * @param context Contexto de validación
     * @param message Mensaje de error personalizado
     */
    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}

