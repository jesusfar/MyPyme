package com.mypyme.gestionstock.controlador;

import com.mypyme.gestionstock.dto.solicitud.ArticuloSolicitud;
import com.mypyme.gestionstock.dto.respuesta.ArticuloRespuesta;
import com.mypyme.gestionstock.dto.respuesta.PaginaRespuesta;
import com.mypyme.gestionstock.servicio.ArticuloServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de artículos.
 * RF02 - Crear artículos
 */
@RestController
@RequestMapping("/api/articulos")
@RequiredArgsConstructor
@Tag(name = "Artículos", description = "API para gestión de artículos de inventario")
public class ArticuloControlador {

    private final ArticuloServicio articuloServicio;

    @Operation(summary = "Obtener todos los artículos con paginación")
    @GetMapping
    public ResponseEntity<PaginaRespuesta<ArticuloRespuesta>> obtenerTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion) {

        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();
        Pageable pageable = PageRequest.of(pagina, tamanio, sort);

        return ResponseEntity.ok(articuloServicio.obtenerTodos(pageable));
    }

    @Operation(summary = "Obtener un artículo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ArticuloRespuesta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(articuloServicio.obtenerPorId(id));
    }

    @Operation(summary = "Obtener artículos por categoría")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ArticuloRespuesta>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(articuloServicio.obtenerPorCategoria(categoriaId));
    }

    @Operation(summary = "Obtener artículos con stock bajo")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ArticuloRespuesta>> obtenerStockBajo() {
        return ResponseEntity.ok(articuloServicio.obtenerArticulosStockBajo());
    }

    @Operation(summary = "Obtener artículos agotados")
    @GetMapping("/agotados")
    public ResponseEntity<List<ArticuloRespuesta>> obtenerAgotados() {
        return ResponseEntity.ok(articuloServicio.obtenerArticulosAgotados());
    }

    @Operation(summary = "Buscar artículos por término")
    @GetMapping("/buscar")
    public ResponseEntity<List<ArticuloRespuesta>> buscar(@RequestParam String termino) {
        return ResponseEntity.ok(articuloServicio.buscar(termino));
    }

    @Operation(summary = "Crear un nuevo artículo")
    @PostMapping
    public ResponseEntity<ArticuloRespuesta> crear(@Valid @RequestBody ArticuloSolicitud solicitud) {
        ArticuloRespuesta respuesta = articuloServicio.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Actualizar un artículo existente")
    @PutMapping("/{id}")
    public ResponseEntity<ArticuloRespuesta> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ArticuloSolicitud solicitud) {
        return ResponseEntity.ok(articuloServicio.actualizar(id, solicitud));
    }

    @Operation(summary = "Eliminar un artículo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        articuloServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
