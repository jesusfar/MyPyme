package com.mypyme.gestionstock.controlador;

import com.mypyme.gestionstock.dto.solicitud.ConteoStockSolicitud;
import com.mypyme.gestionstock.dto.solicitud.AjusteStockSolicitud;
import com.mypyme.gestionstock.dto.respuesta.ConteoStockRespuesta;
import com.mypyme.gestionstock.dto.respuesta.AjusteStockRespuesta;
import com.mypyme.gestionstock.servicio.ConteoStockServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de conteos de stock.
 * RF03 - Conteo de existencias y registro
 */
@RestController
@RequestMapping("/api/conteos")
@RequiredArgsConstructor
@Tag(name = "Conteos de Stock", description = "API para conteo de existencias y ajustes")
public class ConteoStockControlador {

    private final ConteoStockServicio conteoServicio;

    @Operation(summary = "Obtener todos los conteos")
    @GetMapping
    public ResponseEntity<List<ConteoStockRespuesta>> obtenerTodos() {
        return ResponseEntity.ok(conteoServicio.obtenerTodosConteos());
    }

    @Operation(summary = "Obtener conteos con diferencias")
    @GetMapping("/con-diferencias")
    public ResponseEntity<List<ConteoStockRespuesta>> obtenerConDiferencias() {
        return ResponseEntity.ok(conteoServicio.obtenerConteosConDiferencias());
    }

    @Operation(summary = "Obtener conteos pendientes de ajuste")
    @GetMapping("/pendientes-ajuste")
    public ResponseEntity<List<ConteoStockRespuesta>> obtenerPendientesAjuste() {
        return ResponseEntity.ok(conteoServicio.obtenerConteosPendientesAjuste());
    }

    @Operation(summary = "Registrar un nuevo conteo de stock")
    @PostMapping
    public ResponseEntity<ConteoStockRespuesta> registrarConteo(
            @Valid @RequestBody ConteoStockSolicitud solicitud) {
        ConteoStockRespuesta respuesta = conteoServicio.registrarConteo(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Aplicar ajuste basado en conteo")
    @PostMapping("/{conteoId}/ajuste")
    public ResponseEntity<AjusteStockRespuesta> aplicarAjuste(
            @PathVariable Long conteoId,
            @Valid @RequestBody AjusteStockSolicitud solicitud) {
        AjusteStockRespuesta respuesta = conteoServicio.aplicarAjuste(conteoId, solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
