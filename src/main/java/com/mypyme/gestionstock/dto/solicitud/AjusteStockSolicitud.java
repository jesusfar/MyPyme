package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear ajustes de stock desde conteos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteStockSolicitud {

    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivo;

    @NotBlank(message = "El aprobador es requerido")
    @Size(max = 100, message = "El aprobador no puede exceder 100 caracteres")
    private String aprobadoPor;
}
