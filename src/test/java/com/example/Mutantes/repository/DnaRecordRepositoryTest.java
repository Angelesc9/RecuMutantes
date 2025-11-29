package com.example.Mutantes.repository;

import com.example.Mutantes.entity.DnaRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para DnaRecordRepository.
 *
 * Usa @DataJpaTest que configura automáticamente:
 * - Una base de datos H2 en memoria
 * - Spring Data JPA
 * - Transaction management (cada test se rollback automáticamente)
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("DnaRecordRepository - Tests de Persistencia")
class DnaRecordRepositoryTest {

    @Autowired
    private DnaRecordRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private DnaRecord mutantRecord;
    private DnaRecord humanRecord;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test
        repository.deleteAll();

        // Crear registros de prueba
        mutantRecord = DnaRecord.builder()
                .dnaHash("hash_mutant_123")
                .isMutant(true)
                .createdAt(LocalDateTime.now())
                .build();

        humanRecord = DnaRecord.builder()
                .dnaHash("hash_human_456")
                .isMutant(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debe guardar un registro de ADN correctamente")
    void testSaveRecord() {
        // When
        DnaRecord saved = repository.save(mutantRecord);

        // Then
        assertNotNull(saved.getId());
        assertEquals("hash_mutant_123", saved.getDnaHash());
        assertTrue(saved.isMutant());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Debe encontrar un registro por su hash")
    void testFindByDnaHash() {
        // Given
        repository.save(mutantRecord);
        entityManager.flush();

        // When
        Optional<DnaRecord> found = repository.findByDnaHash("hash_mutant_123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("hash_mutant_123", found.get().getDnaHash());
        assertTrue(found.get().isMutant());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty() cuando el hash no existe")
    void testFindByDnaHashNotFound() {
        // When
        Optional<DnaRecord> found = repository.findByDnaHash("hash_inexistente");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Debe contar correctamente los registros de mutantes")
    void testCountByIsMutantTrue() {
        // Given
        repository.save(mutantRecord);
        repository.save(humanRecord);

        DnaRecord anotherMutant = DnaRecord.builder()
                .dnaHash("hash_mutant_789")
                .isMutant(true)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(anotherMutant);

        entityManager.flush();

        // When
        long mutantCount = repository.countByIsMutant(true);

        // Then
        assertEquals(2, mutantCount);
    }

    @Test
    @DisplayName("Debe contar correctamente los registros de humanos")
    void testCountByIsMutantFalse() {
        // Given
        repository.save(mutantRecord);
        repository.save(humanRecord);

        DnaRecord anotherHuman = DnaRecord.builder()
                .dnaHash("hash_human_789")
                .isMutant(false)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(anotherHuman);

        entityManager.flush();

        // When
        long humanCount = repository.countByIsMutant(false);

        // Then
        assertEquals(2, humanCount);
    }

    @Test
    @DisplayName("Debe retornar 0 cuando no hay registros del tipo solicitado")
    void testCountByIsMutantZero() {
        // Given - solo guardamos humanos
        repository.save(humanRecord);
        entityManager.flush();

        // When
        long mutantCount = repository.countByIsMutant(true);

        // Then
        assertEquals(0, mutantCount);
    }

    @Test
    @DisplayName("Debe respetar la restricción de unicidad del hash")
    void testUniqueConstraintOnDnaHash() {
        // Given
        repository.save(mutantRecord);
        entityManager.flush();
        entityManager.clear();

        // When - intentar guardar otro registro con el mismo hash
        DnaRecord duplicate = DnaRecord.builder()
                .dnaHash("hash_mutant_123") // Mismo hash
                .isMutant(false) // Diferente resultado
                .createdAt(LocalDateTime.now())
                .build();

        // Then - debe lanzar excepción por violación de constraint
        assertThrows(Exception.class, () -> {
            repository.save(duplicate);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debe inicializar createdAt automáticamente con @PrePersist")
    void testCreatedAtAutoInitialization() {
        // Given - crear registro sin createdAt
        DnaRecord record = new DnaRecord("hash_test_auto", true);
        assertNull(record.getCreatedAt()); // Aún no tiene fecha

        // When
        DnaRecord saved = repository.save(record);
        entityManager.flush();

        // Then - debe tener createdAt inicializado
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Debe permitir múltiples registros con diferentes hashes")
    void testMultipleRecordsWithDifferentHashes() {
        // Given
        DnaRecord record1 = DnaRecord.builder()
                .dnaHash("hash_1")
                .isMutant(true)
                .createdAt(LocalDateTime.now())
                .build();

        DnaRecord record2 = DnaRecord.builder()
                .dnaHash("hash_2")
                .isMutant(false)
                .createdAt(LocalDateTime.now())
                .build();

        DnaRecord record3 = DnaRecord.builder()
                .dnaHash("hash_3")
                .isMutant(true)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        repository.save(record1);
        repository.save(record2);
        repository.save(record3);
        entityManager.flush();

        // Then
        assertEquals(3, repository.count());
        assertEquals(2, repository.countByIsMutant(true));
        assertEquals(1, repository.countByIsMutant(false));
    }

    @Test
    @DisplayName("Debe buscar correctamente después de guardar múltiples registros")
    void testFindAfterMultipleSaves() {
        // Given
        repository.save(mutantRecord);
        repository.save(humanRecord);
        entityManager.flush();

        // When
        Optional<DnaRecord> foundMutant = repository.findByDnaHash("hash_mutant_123");
        Optional<DnaRecord> foundHuman = repository.findByDnaHash("hash_human_456");

        // Then
        assertTrue(foundMutant.isPresent());
        assertTrue(foundMutant.get().isMutant());

        assertTrue(foundHuman.isPresent());
        assertFalse(foundHuman.get().isMutant());
    }

    @Test
    @DisplayName("Debe actualizar un registro existente")
    void testUpdateExistingRecord() {
        // Given
        DnaRecord saved = repository.save(mutantRecord);
        Long id = saved.getId();
        entityManager.flush();
        entityManager.clear();

        // When - buscar y actualizar
        Optional<DnaRecord> found = repository.findById(id);
        assertTrue(found.isPresent());

        DnaRecord toUpdate = found.get();
        toUpdate.setMutant(false); // Cambiar el resultado (solo para test)
        repository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        // Then - verificar que se actualizó
        Optional<DnaRecord> updated = repository.findById(id);
        assertTrue(updated.isPresent());
        assertFalse(updated.get().isMutant());
    }

    @Test
    @DisplayName("Debe eliminar un registro correctamente")
    void testDeleteRecord() {
        // Given
        DnaRecord saved = repository.save(mutantRecord);
        Long id = saved.getId();
        entityManager.flush();

        // When
        repository.deleteById(id);
        entityManager.flush();

        // Then
        Optional<DnaRecord> deleted = repository.findById(id);
        assertFalse(deleted.isPresent());
    }
}

