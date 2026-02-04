package com.mypyme.gestionstock.controlador;

import com.mypyme.gestionstock.dto.solicitud.PlanificadorInventarioSolicitud;
import com.mypyme.gestionstock.dto.solicitud.EjecutarPlanificadorSolicitud;
import com.mypyme.gestionstock.dto.solicitud.ConteoItemPlanificadorSolicitud;
import com.mypyme.gestionstock.dto.respuesta.PlanificadorInventarioRespuesta;
import com.mypyme.gestionstock.dto.respuesta.ItemPlanificadorRespuesta;
import com.mypyme.gestionstock.entidad.enums.EstadoPlanificador;
import com.mypyme.gestionstock.servicio.PlanificadorInventarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de planificadores de inventario.
 * RF01 - Crear planificador de inventario
 * RF05 - Ejecutar planificador
 */
@RestController
@RequestMapping("/api/planificadores")
@RequiredArgsConstructor
@Tag(name = "Planificadores de Inventario", description = "API para planificación y ejecución de inventarios")
public class PlanificadorInventarioControlador {

    private final PlanificadorInventarioServicio planificadorServicio;

    @Operation(summary = "Obtener todos los planificadores")
    @GetMapping
    public ResponseEntity<List<PlanificadorInventarioRespuesta>> obtenerTodos() {
        return ResponseEntity.ok(planificadorServicio.obtenerTodos());
    }

    @Operation(summary = "Obtener un planificador por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PlanificadorInventarioRespuesta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(planificadorServicio.obtenerPorId(id));
    }

    @Operation(summary = "Obtener planificadores por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PlanificadorInventarioRespuesta>> obtenerPorEstado(
            @PathVariable EstadoPlanificador estado) {
        return ResponseEntity.ok(planificadorServicio.obtenerPorEstado(estado));
    }

    @Operation(summary = "Crear un nuevo planificador")
    @PostMapping
    public ResponseEntity<PlanificadorInventarioRespuesta> crear(
            @Valid @RequestBody PlanificadorInventarioSolicitud solicitud) {
        PlanificadorInventarioRespuesta respuesta = planificadorServicio.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Ejecutar un planificador")
    @PostMapping("/{id}/ejecutar")
    public ResponseEntity<PlanificadorInventarioRespuesta> ejecutar(
            @PathVariable Long id,
            @Valid @RequestBody EjecutarPlanificadorSolicitud solicitud) {
        return ResponseEntity.ok(planificadorServicio.ejecutar(id, solicitud));
    }

    @Operation(summary = "Registrar conteo de un item del planificador")
    @PostMapping("/{planificadorId}/items/{itemId}/contar")
    public ResponseEntity<ItemPlanificadorRespuesta> contarItem(
            @PathVariable Long planificadorId,
            @PathVariable Long itemId,
            @Valid @RequestBody ConteoItemPlanificadorSolicitud solicitud) {
        ItemPlanificadorRespuesta respuesta = planificadorServicio.contarItem(planificadorId, itemId, solicitud);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Cancelar un planificador")
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PlanificadorInventarioRespuesta> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(planificadorServicio.cancelar(id));
    }
}
