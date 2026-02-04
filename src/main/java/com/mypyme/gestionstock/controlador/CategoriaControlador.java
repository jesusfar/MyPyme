package com.mypyme.gestionstock.controlador;

import com.mypyme.gestionstock.dto.solicitud.CategoriaSolicitud;
import com.mypyme.gestionstock.dto.respuesta.CategoriaRespuesta;
import com.mypyme.gestionstock.servicio.CategoriaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de categorías.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "API para gestión de categorías de artículos")
public class CategoriaControlador {

    private final CategoriaServicio categoriaServicio;

    @Operation(summary = "Obtener todas las categorías")
    @GetMapping
    public ResponseEntity<List<CategoriaRespuesta>> obtenerTodas() {
        return ResponseEntity.ok(categoriaServicio.obtenerTodas());
    }

    @Operation(summary = "Obtener una categoría por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaRespuesta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaServicio.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva categoría")
    @PostMapping
    public ResponseEntity<CategoriaRespuesta> crear(@Valid @RequestBody CategoriaSolicitud solicitud) {
        CategoriaRespuesta respuesta = categoriaServicio.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Actualizar una categoría existente")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaRespuesta> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaSolicitud solicitud) {
        return ResponseEntity.ok(categoriaServicio.actualizar(id, solicitud));
    }

    @Operation(summary = "Eliminar una categoría")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
