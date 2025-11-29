package com.example.Mutantes.repository;

import com.example.Mutantes.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la gestión de registros de ADN.
 *
 * Proporciona métodos CRUD automáticos a través de JpaRepository
 * y métodos de consulta personalizados para:
 * - Buscar registros por hash de ADN (evitar análisis duplicados)
 * - Contar registros de mutantes/humanos (para estadísticas)
 *
 * Spring Data JPA genera automáticamente la implementación de esta interfaz
 * basándose en las convenciones de nombres de métodos.
 */
@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    /**
     * Busca un registro de ADN por su hash único.
     *
     * Este método es fundamental para:
     * - Evitar análisis duplicados del mismo ADN
     * - Retornar resultados cacheados de análisis previos
     *
     * Query generada automáticamente:
     * SELECT * FROM dna_records WHERE dna_hash = ?
     *
     * @param dnaHash Hash único del ADN a buscar
     * @return Optional con el registro si existe, Optional.empty() si no existe
     */
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    /**
     * Cuenta la cantidad de registros según el tipo (mutante o humano).
     *
     * Este método es esencial para las estadísticas del sistema:
     * - countByIsMutant(true) → Cantidad de mutantes detectados
     * - countByIsMutant(false) → Cantidad de humanos detectados
     *
     * Query generada automáticamente:
     * SELECT COUNT(*) FROM dna_records WHERE is_mutant = ?
     *
     * @param isMutant true para contar mutantes, false para contar humanos
     * @return Cantidad de registros que coinciden con el criterio
     */
    long countByIsMutant(boolean isMutant);
}

