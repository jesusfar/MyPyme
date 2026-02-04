package com.mypyme.gestionstock.dto.respuesta;

import com.mypyme.gestionstock.entidad.enums.EstadoPlanificador;
import com.mypyme.gestionstock.entidad.enums.TipoPlanificador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para planificadores de inventario.
 * RF01/RF05 - Planificador de inventario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanificadorInventarioRespuesta {

    private Long id;
    private String codigoPlan;
    private String nombre;
    private String descripcion;
    private TipoPlanificador tipoPlanificador;
    private String tipoPlanificadorDescripcion;
    private EstadoPlanificador estado;
    private String estadoDescripcion;
    private Long ubicacionId;
    private String ubicacionNombre;
    private Long categoriaId;
    private String categoriaNombre;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCompletado;
    private String creadoPor;
    private String ejecutadoPor;
    private Integer totalItems;
    private Integer itemsCompletados;
    private Double porcentajeProgreso;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Incluido solo en respuestas detalladas
    private List<ItemPlanificadorRespuesta> items;
}
