package com.mypyme.gestionstock.dto.solicitud;

import com.mypyme.gestionstock.entidad.enums.TipoPlanificador;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para crear/actualizar planificadores de inventario.
 * RF01 - Crear planificador de inventario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanificadorInventarioSolicitud {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El tipo de planificador es requerido")
    private TipoPlanificador tipoPlanificador;

    private LocalDateTime fechaProgramada;

    @NotBlank(message = "El creador es requerido")
    @Size(max = 100, message = "El creador no puede exceder 100 caracteres")
    private String creadoPor;

    // Para tipo POR_UBICACION
    private Long ubicacionId;

    // Para tipo POR_CATEGORIA
    private Long categoriaId;

    // Para tipo POR_ARTICULOS
    private List<Long> articuloIds;
}
