package com.mypyme.gestionstock.dto.respuesta;

import com.mypyme.gestionstock.entidad.enums.EstadoConteo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para conteos de stock.
 * RF03 - Conteo de existencias
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoStockRespuesta {

    private Long id;
    private String numeroConteo;
    private Long articuloId;
    private String articuloNombre;
    private Long ubicacionId;
    private String ubicacionCodigo;
    private String ubicacionNombre;
    private Integer cantidadSistema;
    private Integer cantidadContada;
    private Integer diferencia;
    private EstadoConteo estado;
    private String estadoDescripcion;
    private String usuarioResponsable;
    private String notas;
    private LocalDateTime fechaConteo;
    private Boolean tieneAjuste;
    private LocalDateTime fechaCreacion;
}
