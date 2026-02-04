package com.mypyme.gestionstock.dto.respuesta;

import com.mypyme.gestionstock.entidad.enums.TipoAjuste;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para ajustes de stock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteStockRespuesta {

    private Long id;
    private Long conteoStockId;
    private String numeroConteo;
    private Long articuloId;
    private String articuloNombre;
    private TipoAjuste tipoAjuste;
    private String tipoAjusteDescripcion;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private String motivo;
    private String aprobadoPor;
    private LocalDateTime fechaAjuste;
    private LocalDateTime fechaCreacion;
}
