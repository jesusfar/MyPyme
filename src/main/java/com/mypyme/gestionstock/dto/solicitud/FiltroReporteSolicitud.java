package com.mypyme.gestionstock.dto.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para filtrar reportes.
 * RF07 - Reportes de existencias
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltroReporteSolicitud {

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long categoriaId;
    private Long ubicacionId;
    private Long articuloId;
}
