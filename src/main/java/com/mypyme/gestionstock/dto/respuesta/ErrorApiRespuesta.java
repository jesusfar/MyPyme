package com.mypyme.gestionstock.dto.respuesta;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para errores de la API.
 * Utilizado por el ManejadorGlobalExcepciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorApiRespuesta {

    private Integer estado;
    private String error;
    private String mensaje;
    private String ruta;
    private LocalDateTime marca;
    private List<ErrorCampo> erroresCampo;

    /**
     * Error de validación de campo específico
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorCampo {
        private String campo;
        private String mensaje;
        private Object valorRechazado;
    }

    /**
     * Constructor simplificado para errores simples
     */
    public ErrorApiRespuesta(Integer estado, String error, String mensaje) {
        this.estado = estado;
        this.error = error;
        this.mensaje = mensaje;
        this.marca = LocalDateTime.now();
    }
}
