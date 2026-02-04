package com.mypyme.gestionstock.dto.solicitud;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear/actualizar artículos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloSolicitud {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La categoría es requerida")
    private Long categoriaId;

    @NotNull(message = "La cantidad en stock es requerida")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidadStock;

    @NotNull(message = "El stock mínimo es requerido")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal precioUnitario;
}
