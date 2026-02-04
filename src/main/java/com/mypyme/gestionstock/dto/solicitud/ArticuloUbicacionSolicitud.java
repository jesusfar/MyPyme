package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para asignar artículos a ubicaciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloUbicacionSolicitud {

    @NotNull(message = "El artículo es requerido")
    private Long articuloId;

    @NotNull(message = "La ubicación es requerida")
    private Long ubicacionId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
}
