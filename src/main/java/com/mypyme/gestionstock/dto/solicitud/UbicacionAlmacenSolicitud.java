package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar ubicaciones de almacén.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionAlmacenSolicitud {

    @NotBlank(message = "El código es requerido")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Size(max = 50, message = "La zona no puede exceder 50 caracteres")
    private String zona;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;
}
