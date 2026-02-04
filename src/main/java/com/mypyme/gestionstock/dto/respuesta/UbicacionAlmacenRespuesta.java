package com.mypyme.gestionstock.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para ubicaciones de almacén.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionAlmacenRespuesta {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String zona;
    private Integer capacidad;
    private Integer capacidadUsada;
    private Boolean activa;
    private Long cantidadArticulos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
