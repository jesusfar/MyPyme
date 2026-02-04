package com.mypyme.gestionstock.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para categorías.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaRespuesta {

    private Long id;
    private String nombre;
    private String descripcion;
    private Long cantidadArticulos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
