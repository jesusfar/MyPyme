package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para ejecutar un planificador de inventario.
 * RF05 - Ejecutar planificador
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EjecutarPlanificadorSolicitud {

    @NotBlank(message = "El ejecutor es requerido")
    @Size(max = 100, message = "El ejecutor no puede exceder 100 caracteres")
    private String ejecutadoPor;
}
