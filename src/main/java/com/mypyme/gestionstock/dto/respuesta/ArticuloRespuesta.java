package com.mypyme.gestionstock.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para artículos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloRespuesta {

    private Long id;
    private String nombre;
    private String descripcion;
    private CategoriaRespuesta categoria;
    private Integer cantidadStock;
    private Integer stockMinimo;
    private BigDecimal precioUnitario;
    private BigDecimal valorTotal;
    private Boolean stockBajo;
    private Boolean agotado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
