package com.example.Mutantes.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER, METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = DnaSequenceValidator.class)
@Documented
public @interface ValidDnaSequence {
    String message() default "DNA sequence must be a non-empty NxN array containing only characters A,T,C,G";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

