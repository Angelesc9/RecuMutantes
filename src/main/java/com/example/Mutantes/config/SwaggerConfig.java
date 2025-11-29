package com.example.Mutantes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API.
 *
 * Esta configuración genera automáticamente la documentación interactiva
 * de la API que estará disponible en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 *
 * La documentación permite:
 * - Visualizar todos los endpoints disponibles
 * - Probar los endpoints directamente desde el navegador
 * - Ver los modelos de datos (DTOs)
 * - Consultar códigos de respuesta HTTP
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura el bean OpenAPI con la información de la API.
     *
     * Define metadatos como:
     * - Título y descripción de la API
     * - Versión
     * - Información de contacto
     * - Licencia
     * - Servidores disponibles
     *
     * @return Bean de OpenAPI configurado
     */
    @Bean
    public OpenAPI mutantDetectorOpenAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers());
    }

    /**
     * Configura la información básica de la API.
     *
     * @return Objeto Info con metadatos de la API
     */
    private Info getApiInfo() {
        return new Info()
                .title("Mutant Detector API")
                .description(
                    "API REST para detección de mutantes mediante análisis de secuencias de ADN. " +
                    "\n\nFuncionalidades principales:\n" +
                    "- Analizar secuencias de ADN para detectar mutantes\n" +
                    "- Obtener estadísticas de verificaciones realizadas\n" +
                    "- Sistema de caché para optimizar análisis repetidos\n" +
                    "\n\nUn humano es considerado mutante si tiene más de una secuencia de " +
                    "cuatro letras iguales consecutivas (horizontal, vertical o diagonal) en su ADN."
                )
                .version("1.0")
                .contact(getContactInfo())
                .license(getLicenseInfo());
    }

    /**
     * Configura la información de contacto del desarrollador/equipo.
     *
     * @return Objeto Contact con información de contacto
     */
    private Contact getContactInfo() {
        return new Contact()
                .name("Sistema de Detección de Mutantes")
                .email("mutant-detector@example.com")
                .url("https://github.com/ejemplo/mutant-detector");
    }

    /**
     * Configura la información de licencia de la API.
     *
     * @return Objeto License con información de licencia
     */
    private License getLicenseInfo() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Configura los servidores disponibles para la API.
     *
     * Útil cuando la API está desplegada en múltiples ambientes
     * (desarrollo, staging, producción).
     *
     * @return Lista de servidores disponibles
     */
    private List<Server> getServers() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de desarrollo local");

        // Puedes agregar más servidores según sea necesario:
        // Server productionServer = new Server()
        //         .url("https://api.mutant-detector.com")
        //         .description("Servidor de producción");

        return List.of(localServer);
    }
}

