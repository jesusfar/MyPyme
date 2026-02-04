package com.mypyme.gestionstock.controlador;

import com.mypyme.gestionstock.dto.solicitud.FiltroReporteSolicitud;
import com.mypyme.gestionstock.dto.respuesta.ReporteStockRespuesta;
import com.mypyme.gestionstock.servicio.ReporteStockServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la generación de reportes de stock.
 * RF07 - Reportes de existencias consolidados
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes de Stock", description = "API para generación de reportes de inventario")
public class ReporteStockControlador {

    private final ReporteStockServicio reporteServicio;

    @Operation(summary = "Generar reporte de stock consolidado")
    @GetMapping("/stock")
    public ResponseEntity<ReporteStockRespuesta> generarReporteStock(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long ubicacionId,
            @RequestParam(required = false) Long articuloId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        FiltroReporteSolicitud filtro = FiltroReporteSolicitud.builder()
                .categoriaId(categoriaId)
                .ubicacionId(ubicacionId)
                .articuloId(articuloId)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .build();

        return ResponseEntity.ok(reporteServicio.generarReporteStock(filtro));
    }

    @Operation(summary = "Obtener alertas de stock bajo")
    @GetMapping("/alertas/stock-bajo")
    public ResponseEntity<List<ReporteStockRespuesta.ArticuloStockBajo>> obtenerAlertasStockBajo() {
        return ResponseEntity.ok(reporteServicio.obtenerAlertasStockBajo());
    }

    @Operation(summary = "Obtener estadísticas rápidas")
    @GetMapping("/estadisticas")
    public ResponseEntity<ReporteStockRespuesta.ResumenReporte> obtenerEstadisticas() {
        return ResponseEntity.ok(reporteServicio.obtenerEstadisticasRapidas());
    }
}
