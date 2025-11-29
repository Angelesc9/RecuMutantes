package com.example.Mutantes.service;

import com.example.Mutantes.entity.DnaRecord;
import com.example.Mutantes.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Servicio principal para el análisis de ADN y detección de mutantes.
 *
 * Implementa la lógica de negocio del Nivel 3 con sistema de caché:
 * - Genera un hash único para cada secuencia de ADN
 * - Consulta primero la base de datos para evitar análisis duplicados
 * - Si no existe, analiza el ADN y guarda el resultado
 *
 * Esta estrategia mejora significativamente el rendimiento al evitar
 * análisis repetidos de la misma secuencia de ADN.
 */
@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Analiza una secuencia de ADN y determina si pertenece a un mutante.
     *
     * Implementa un sistema de caché basado en hash para optimizar el rendimiento:
     *
     * FLUJO DE EJECUCIÓN:
     * 1. Genera un hash SHA-256 único del ADN
     * 2. Consulta la base de datos por el hash
     * 3. Si existe: Retorna el resultado cacheado (evita reprocesar)
     * 4. Si no existe:
     *    a. Ejecuta el algoritmo de detección
     *    b. Persiste el resultado en la BD
     *    c. Retorna el resultado
     *
     * @param dna Array de Strings representando la secuencia de ADN (NxN)
     * @return true si es mutante, false si es humano
     * @throws IllegalArgumentException si el ADN es inválido (propagado desde MutantDetector)
     */
    public boolean analyzeDna(String[] dna) {
        // PASO 1: Generar hash único del ADN
        String dnaHash = calculateHash(dna);

        // PASO 2: Consultar caché (base de datos)
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);

        // PASO 3: Si existe en caché, retornar resultado sin procesar
        if (existingRecord.isPresent()) {
            return existingRecord.get().isMutant();
        }

        // PASO 4: No existe en caché - Analizar el ADN
        boolean isMutant = mutantDetector.isMutant(dna);

        // PASO 5: Persistir el resultado para futuros análisis
        DnaRecord newRecord = DnaRecord.builder()
                .dnaHash(dnaHash)
                .isMutant(isMutant)
                .build();

        dnaRecordRepository.save(newRecord);

        // PASO 6: Retornar el resultado
        return isMutant;
    }

    /**
     * Calcula un hash SHA-256 único para una secuencia de ADN.
     *
     * ESTRATEGIA:
     * - Ordena el array para normalizar (evita duplicados por orden diferente)
     * - Concatena todos los strings del array
     * - Aplica SHA-256 para generar un hash de 64 caracteres hexadecimales
     *
     * VENTAJAS DE SHA-256:
     * - Longitud fija de 256 bits (64 caracteres hex)
     * - Extremadamente baja probabilidad de colisiones
     * - Rendimiento aceptable para strings de ADN típicos
     *
     * @param dna Array de Strings representando el ADN
     * @return String hexadecimal de 64 caracteres representando el hash SHA-256
     * @throws RuntimeException si el algoritmo SHA-256 no está disponible (muy raro)
     */
    private String calculateHash(String[] dna) {
        try {
            // Normalizar: ordenar el array para que el mismo ADN siempre genere el mismo hash
            // independientemente del orden de entrada
            String[] sortedDna = Arrays.copyOf(dna, dna.length);
            Arrays.sort(sortedDna);

            // Concatenar todas las secuencias en un solo string
            String concatenated = String.join("", sortedDna);

            // Obtener instancia del algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calcular el hash
            byte[] hashBytes = digest.digest(concatenated.getBytes(StandardCharsets.UTF_8));

            // Convertir bytes a representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Esto nunca debería ocurrir ya que SHA-256 es un algoritmo estándar
            // pero es necesario manejarlo por el contrato de MessageDigest.getInstance()
            throw new RuntimeException("Error al calcular hash SHA-256: Algoritmo no disponible", e);
        }
    }

    /**
     * Método auxiliar para obtener el hash de un ADN sin procesarlo.
     * Útil para testing y debugging.
     *
     * @param dna Array de Strings representando el ADN
     * @return Hash SHA-256 del ADN
     */
    public String getDnaHash(String[] dna) {
        return calculateHash(dna);
    }
}

