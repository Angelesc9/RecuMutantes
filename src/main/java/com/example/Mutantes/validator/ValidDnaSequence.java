package com.example.Mutantes.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación de validación personalizada para secuencias de ADN.
 *
 * Valida que una secuencia de ADN cumpla con los siguientes criterios:
 * - No sea null ni vacía
 * - Sea una matriz cuadrada (NxN)
 * - Solo contenga los caracteres válidos: A, T, C, G
 *
 * Ejemplo de uso:
 * <pre>
 * public class DnaRequest {
 *     {@literal @}ValidDnaSequence
 *     private String[] dna;
 * }
 * </pre>
 *
 * @author Sistema de Detección de Mutantes
 * @version 1.0
 */
@Documented
@Constraint(validatedBy = DnaSequenceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDnaSequence {

    /**
     * Mensaje de error por defecto cuando la validación falla.
     *
     * @return Mensaje de error
     */
    String message() default "La secuencia de ADN no es válida. Debe ser una matriz NxN con solo caracteres A, T, C, G";

    /**
     * Permite especificar grupos de validación.
     *
     * @return Array de grupos
     */
    Class<?>[] groups() default {};

    /**
     * Información adicional sobre el tipo de error.
     *
     * @return Array de payloads
     */
    Class<? extends Payload>[] payload() default {};
}

