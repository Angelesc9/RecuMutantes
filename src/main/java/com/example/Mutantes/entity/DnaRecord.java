package com.example.Mutantes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro de análisis de ADN.
 *
 * Almacena el hash único del ADN analizado, el resultado de si es mutante o no,
 * y la fecha de creación del registro.
 *
 * El campo dnaHash tiene restricción de unicidad a nivel de base de datos para
 * evitar el análisis duplicado del mismo ADN.
 */
@Entity
@Table(name = "dna_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnaRecord {

    /**
     * Identificador único del registro.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hash único del ADN analizado.
     *
     * Este campo es crítico para evitar duplicados:
     * - unique = true: Garantiza unicidad a nivel de base de datos
     * - nullable = false: No permite valores nulos
     *
     * Se genera a partir del array de DNA para identificar de manera única
     * cada secuencia analizada.
     */
    @Column(name = "dna_hash", unique = true, nullable = false, length = 64)
    private String dnaHash;

    /**
     * Indica si el ADN corresponde a un mutante.
     *
     * - true: El ADN tiene más de una secuencia de 4 letras iguales (es mutante)
     * - false: El ADN no cumple con la condición de mutante (es humano)
     */
    @Column(name = "is_mutant", nullable = false)
    private boolean isMutant;

    /**
     * Fecha y hora en que se creó el registro.
     *
     * Se inicializa automáticamente en el momento de la persistencia
     * mediante el método prePersist.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Método callback ejecutado antes de persistir la entidad.
     *
     * Inicializa el campo createdAt con la fecha y hora actual
     * si aún no ha sido establecido.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Constructor de conveniencia para crear un registro sin ID.
     * Útil para crear nuevas entidades antes de persistirlas.
     *
     * @param dnaHash Hash único del ADN
     * @param isMutant Indica si es mutante
     */
    public DnaRecord(String dnaHash, boolean isMutant) {
        this.dnaHash = dnaHash;
        this.isMutant = isMutant;
    }
}

