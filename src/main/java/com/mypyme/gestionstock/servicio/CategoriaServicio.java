package com.mypyme.gestionstock.servicio;

import com.mypyme.gestionstock.dto.solicitud.CategoriaSolicitud;
import com.mypyme.gestionstock.dto.respuesta.CategoriaRespuesta;
import com.mypyme.gestionstock.entidad.Categoria;
import com.mypyme.gestionstock.excepcion.RecursoDuplicadoExcepcion;
import com.mypyme.gestionstock.excepcion.RecursoNoEncontradoExcepcion;
import com.mypyme.gestionstock.repositorio.ArticuloRepositorio;
import com.mypyme.gestionstock.repositorio.CategoriaRepositorio;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de categorías.
 * Contiene la lógica de negocio para operaciones CRUD de categorías.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServicio {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaServicio.class);

    private final CategoriaRepositorio categoriaRepositorio;
    private final ArticuloRepositorio articuloRepositorio;

    /**
     * Obtiene todas las categorías.
     * 
     * @return lista de categorías
     */
    @Transactional(readOnly = true)
    public List<CategoriaRespuesta> obtenerTodas() {
        logger.info("Obteniendo todas las categorías");
        return categoriaRepositorio.findAll()
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una categoría por su ID.
     * 
     * @param id ID de la categoría
     * @return categoría encontrada
     * @throws RecursoNoEncontradoExcepcion si no existe
     */
    @Transactional(readOnly = true)
    public CategoriaRespuesta obtenerPorId(Long id) {
        logger.info("Buscando categoría con ID: {}", id);
        Categoria categoria = buscarCategoriaPorId(id);
        return convertirARespuesta(categoria);
    }

    /**
     * Crea una nueva categoría.
     * 
     * @param solicitud datos de la categoría
     * @return categoría creada
     * @throws RecursoDuplicadoExcepcion si ya existe una categoría con el mismo
     *                                   nombre
     */
    public CategoriaRespuesta crear(CategoriaSolicitud solicitud) {
        logger.info("Creando nueva categoría: {}", solicitud.getNombre());

        // Verificar nombre duplicado
        if (categoriaRepositorio.existsByNombre(solicitud.getNombre())) {
            throw new RecursoDuplicadoExcepcion("Categoría", "nombre", solicitud.getNombre());
        }

        Categoria categoria = Categoria.builder()
                .nombre(solicitud.getNombre())
                .descripcion(solicitud.getDescripcion())
                .build();

        Categoria guardada = categoriaRepositorio.save(categoria);
        logger.info("Categoría creada con ID: {}", guardada.getId());

        return convertirARespuesta(guardada);
    }

    /**
     * Actualiza una categoría existente.
     * 
     * @param id        ID de la categoría
     * @param solicitud nuevos datos
     * @return categoría actualizada
     */
    public CategoriaRespuesta actualizar(Long id, CategoriaSolicitud solicitud) {
        logger.info("Actualizando categoría con ID: {}", id);

        Categoria categoria = buscarCategoriaPorId(id);

        // Verificar nombre duplicado (excluyendo la categoría actual)
        if (categoriaRepositorio.existsByNombreAndIdNot(solicitud.getNombre(), id)) {
            throw new RecursoDuplicadoExcepcion("Categoría", "nombre", solicitud.getNombre());
        }

        categoria.setNombre(solicitud.getNombre());
        categoria.setDescripcion(solicitud.getDescripcion());

        Categoria actualizada = categoriaRepositorio.save(categoria);
        logger.info("Categoría actualizada: {}", actualizada.getNombre());

        return convertirARespuesta(actualizada);
    }

    /**
     * Elimina una categoría.
     * 
     * @param id ID de la categoría
     */
    public void eliminar(Long id) {
        logger.info("Eliminando categoría con ID: {}", id);

        Categoria categoria = buscarCategoriaPorId(id);

        // Verificar si hay artículos asociados
        long cantidadArticulos = articuloRepositorio.countByCategoriaId(id);
        if (cantidadArticulos > 0) {
            throw new com.mypyme.gestionstock.excepcion.NegocioExcepcion(
                    "No se puede eliminar la categoría porque tiene " + cantidadArticulos + " artículos asociados");
        }

        categoriaRepositorio.delete(categoria);
        logger.info("Categoría eliminada: {}", categoria.getNombre());
    }

    /**
     * Busca una categoría por ID o lanza excepción.
     */
    public Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Categoría", "id", id));
    }

    /**
     * Convierte entidad a DTO de respuesta.
     */
    private CategoriaRespuesta convertirARespuesta(Categoria categoria) {
        return CategoriaRespuesta.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .cantidadArticulos((long) categoria.getArticulos().size())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .build();
    }
}
