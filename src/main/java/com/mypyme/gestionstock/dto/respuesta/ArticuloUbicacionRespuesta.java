package com.mypyme.gestionstock.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para asignaciones de artículos a ubicaciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloUbicacionRespuesta {

    private Long id;
    private Long articuloId;
    private String articuloNombre;
    private Long ubicacionId;
    private String ubicacionCodigo;
    private String ubicacionNombre;
    private Integer cantidad;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaActualizacion;
}
