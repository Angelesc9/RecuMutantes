package com.example.Mutantes.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 *
 * Intercepta excepciones lanzadas por los controladores y las convierte
 * en respuestas HTTP apropiadas con mensajes de error estructurados.
 *
 * Esto garantiza:
 * - Respuestas consistentes para todos los errores
 * - Información útil para debugging
 * - Códigos HTTP apropiados según el tipo de error
 * - Oculta detalles internos del servidor en producción
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de request body.
     *
     * Se activa cuando:
     * - Falla la validación de @Valid en el request body
     * - Alguna anotación de validación no se cumple (@NotNull, @NotEmpty, @ValidDnaSequence, etc.)
     *
     * EJEMPLO DE RESPUESTA:
     * {
     *   "timestamp": "2025-01-29T10:30:00",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Error de validación",
     *   "errors": {
     *     "dna": "El campo 'dna' no puede estar vacío"
     *   }
     * }
     *
     * @param ex Excepción de validación con detalles de los campos que fallaron
     * @return ResponseEntity con HTTP 400 BAD REQUEST y detalles de los errores
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        // Extraer todos los errores de validación
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        // Construir respuesta estructurada
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", "Error de validación en el request");
        response.put("errors", fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Maneja errores de argumentos inválidos (IllegalArgumentException).
     *
     * Se activa cuando:
     * - El servicio MutantDetector lanza IllegalArgumentException por DNA inválido
     * - Otros servicios lanzan esta excepción por argumentos incorrectos
     *
     * @param ex Excepción de argumento ilegal
     * @return ResponseEntity con HTTP 400 BAD REQUEST y mensaje de error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Maneja cualquier otra excepción no capturada específicamente.
     *
     * Este es el manejador de fallback para errores inesperados del servidor.
     *
     * NOTA DE SEGURIDAD:
     * En producción, no expongas detalles internos del servidor.
     * Este manejador debe loggear el error completo pero retornar un mensaje genérico.
     *
     * EJEMPLO DE RESPUESTA:
     * {
     *   "timestamp": "2025-01-29T10:30:00",
     *   "status": 500,
     *   "error": "Internal Server Error",
     *   "message": "Ha ocurrido un error interno en el servidor"
     * }
     *
     * @param ex Excepción genérica no manejada
     * @return ResponseEntity con HTTP 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.put("message", "Ha ocurrido un error interno en el servidor");

        // TODO: En producción, loggear el error completo para debugging
        // log.error("Error interno del servidor", ex);

        // En desarrollo, puedes incluir el mensaje de la excepción para debugging:
        // response.put("details", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * Maneja errores de acceso a datos (por si hay problemas con la base de datos).
     *
     * @param ex Excepción de acceso a datos
     * @return ResponseEntity con HTTP 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(
            org.springframework.dao.DataAccessException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.put("message", "Error al acceder a la base de datos");

        // TODO: Log completo del error
        // log.error("Error de base de datos", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}

