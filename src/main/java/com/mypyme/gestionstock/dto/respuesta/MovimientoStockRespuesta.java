package com.mypyme.gestionstock.dto.respuesta;

import com.mypyme.gestionstock.entidad.enums.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para movimientos de stock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStockRespuesta {

    private Long id;
    private Long articuloId;
    private String articuloNombre;
    private TipoMovimiento tipoMovimiento;
    private String tipoMovimientoDescripcion;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private String motivo;
    private String referencia;
    private String creadoPor;
    private LocalDateTime fechaMovimiento;
    private LocalDateTime fechaCreacion;
}
