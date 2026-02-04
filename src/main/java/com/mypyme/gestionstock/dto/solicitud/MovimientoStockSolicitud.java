package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar movimientos de stock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStockSolicitud {

    @NotNull(message = "El artículo es requerido")
    private Long articuloId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivo;

    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    private String referencia;

    @Size(max = 100, message = "El usuario no puede exceder 100 caracteres")
    private String creadoPor;
}
