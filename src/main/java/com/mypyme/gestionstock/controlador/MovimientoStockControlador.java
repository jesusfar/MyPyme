package com.mypyme.gestionstock.controlador;

import com.mypyme.gestionstock.dto.solicitud.MovimientoStockSolicitud;
import com.mypyme.gestionstock.dto.respuesta.MovimientoStockRespuesta;
import com.mypyme.gestionstock.servicio.MovimientoStockServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de movimientos de stock.
 * RF06 - Registrar existencias (entradas/salidas)
 */
@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos de Stock", description = "API para registro de entradas y salidas de stock")
public class MovimientoStockControlador {

    private final MovimientoStockServicio movimientoServicio;

    @Operation(summary = "Registrar entrada de stock")
    @PostMapping("/entrada")
    public ResponseEntity<MovimientoStockRespuesta> registrarEntrada(
            @Valid @RequestBody MovimientoStockSolicitud solicitud) {
        MovimientoStockRespuesta respuesta = movimientoServicio.registrarEntrada(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Registrar salida de stock")
    @PostMapping("/salida")
    public ResponseEntity<MovimientoStockRespuesta> registrarSalida(
            @Valid @RequestBody MovimientoStockSolicitud solicitud) {
        MovimientoStockRespuesta respuesta = movimientoServicio.registrarSalida(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Obtener movimientos por artículo")
    @GetMapping("/articulo/{articuloId}")
    public ResponseEntity<List<MovimientoStockRespuesta>> obtenerPorArticulo(@PathVariable Long articuloId) {
        return ResponseEntity.ok(movimientoServicio.obtenerPorArticulo(articuloId));
    }

    @Operation(summary = "Obtener movimientos por rango de fechas")
    @GetMapping("/rango")
    public ResponseEntity<List<MovimientoStockRespuesta>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(movimientoServicio.obtenerPorRangoFechas(fechaInicio, fechaFin));
    }

    @Operation(summary = "Obtener movimientos recientes")
    @GetMapping("/recientes")
    public ResponseEntity<List<MovimientoStockRespuesta>> obtenerRecientes() {
        return ResponseEntity.ok(movimientoServicio.obtenerRecientes());
    }
}
