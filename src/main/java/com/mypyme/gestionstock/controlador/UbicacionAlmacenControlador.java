package com.mypyme.gestionstock.controlador;

import com.mypyme.gestionstock.dto.solicitud.UbicacionAlmacenSolicitud;
import com.mypyme.gestionstock.dto.respuesta.UbicacionAlmacenRespuesta;
import com.mypyme.gestionstock.servicio.UbicacionAlmacenServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de ubicaciones de almacén.
 * RF04 - Crear ubicaciones
 * RF08 - Registrar ubicación
 */
@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
@Tag(name = "Ubicaciones de Almacén", description = "API para gestión de ubicaciones de almacenamiento")
public class UbicacionAlmacenControlador {

    private final UbicacionAlmacenServicio ubicacionServicio;

    @Operation(summary = "Obtener todas las ubicaciones")
    @GetMapping
    public ResponseEntity<List<UbicacionAlmacenRespuesta>> obtenerTodas() {
        return ResponseEntity.ok(ubicacionServicio.obtenerTodas());
    }

    @Operation(summary = "Obtener ubicaciones activas")
    @GetMapping("/activas")
    public ResponseEntity<List<UbicacionAlmacenRespuesta>> obtenerActivas() {
        return ResponseEntity.ok(ubicacionServicio.obtenerActivas());
    }

    @Operation(summary = "Obtener una ubicación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UbicacionAlmacenRespuesta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionServicio.obtenerPorId(id));
    }

    @Operation(summary = "Obtener ubicaciones por zona")
    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<UbicacionAlmacenRespuesta>> obtenerPorZona(@PathVariable String zona) {
        return ResponseEntity.ok(ubicacionServicio.obtenerPorZona(zona));
    }

    @Operation(summary = "Obtener lista de zonas")
    @GetMapping("/zonas")
    public ResponseEntity<List<String>> obtenerZonas() {
        return ResponseEntity.ok(ubicacionServicio.obtenerZonas());
    }

    @Operation(summary = "Crear una nueva ubicación")
    @PostMapping
    public ResponseEntity<UbicacionAlmacenRespuesta> crear(
            @Valid @RequestBody UbicacionAlmacenSolicitud solicitud) {
        UbicacionAlmacenRespuesta respuesta = ubicacionServicio.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Actualizar una ubicación existente")
    @PutMapping("/{id}")
    public ResponseEntity<UbicacionAlmacenRespuesta> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UbicacionAlmacenSolicitud solicitud) {
        return ResponseEntity.ok(ubicacionServicio.actualizar(id, solicitud));
    }

    @Operation(summary = "Activar una ubicación")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<UbicacionAlmacenRespuesta> activar(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionServicio.activar(id));
    }

    @Operation(summary = "Desactivar una ubicación")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UbicacionAlmacenRespuesta> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionServicio.desactivar(id));
    }

    @Operation(summary = "Eliminar una ubicación")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ubicacionServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
