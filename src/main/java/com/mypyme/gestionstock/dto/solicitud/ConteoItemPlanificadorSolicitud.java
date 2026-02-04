package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para contar un item en un planificador de inventario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoItemPlanificadorSolicitud {

    @NotNull(message = "La cantidad contada es requerida")
    @Min(value = 0, message = "La cantidad contada no puede ser negativa")
    private Integer cantidadContada;

    @NotBlank(message = "El usuario que cuenta es requerido")
    @Size(max = 100, message = "El usuario no puede exceder 100 caracteres")
    private String contadoPor;
}
