package com.mypyme.gestionstock.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para items del planificador de inventario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPlanificadorRespuesta {

    private Long id;
    private Long articuloId;
    private String articuloNombre;
    private Long ubicacionId;
    private String ubicacionCodigo;
    private String ubicacionNombre;
    private Integer cantidadEsperada;
    private Integer cantidadContada;
    private Integer diferencia;
    private Boolean completado;
    private String contadoPor;
    private LocalDateTime fechaCompletado;
}
