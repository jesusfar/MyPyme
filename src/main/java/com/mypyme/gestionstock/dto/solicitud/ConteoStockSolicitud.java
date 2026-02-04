package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear conteos de stock.
 * RF03 - Conteo de existencias
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoStockSolicitud {

    @NotNull(message = "El artículo es requerido")
    private Long articuloId;

    private Long ubicacionId;

    @NotNull(message = "La cantidad contada es requerida")
    @Min(value = 0, message = "La cantidad contada no puede ser negativa")
    private Integer cantidadContada;

    @NotBlank(message = "El usuario responsable es requerido")
    @Size(max = 100, message = "El usuario responsable no puede exceder 100 caracteres")
    private String usuarioResponsable;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}
