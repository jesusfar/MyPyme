package com.mypyme.gestionstock.servicio;

import com.mypyme.gestionstock.dto.solicitud.ArticuloSolicitud;
import com.mypyme.gestionstock.dto.respuesta.ArticuloRespuesta;
import com.mypyme.gestionstock.dto.respuesta.CategoriaRespuesta;
import com.mypyme.gestionstock.dto.respuesta.PaginaRespuesta;
import com.mypyme.gestionstock.entidad.Articulo;
import com.mypyme.gestionstock.entidad.Categoria;
import com.mypyme.gestionstock.excepcion.RecursoDuplicadoExcepcion;
import com.mypyme.gestionstock.excepcion.RecursoNoEncontradoExcepcion;
import com.mypyme.gestionstock.repositorio.ArticuloRepositorio;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de artículos.
 * Contiene la lógica de negocio para operaciones CRUD de artículos.
 * RF02 - Crear artículos
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ArticuloServicio {

    private static final Logger logger = LoggerFactory.getLogger(ArticuloServicio.class);

    private final ArticuloRepositorio articuloRepositorio;
    private final CategoriaServicio categoriaServicio;

    /**
     * Obtiene todos los artículos con paginación.
     * 
     * @param pageable configuración de paginación
     * @return página de artículos
     */
    @Transactional(readOnly = true)
    public PaginaRespuesta<ArticuloRespuesta> obtenerTodos(Pageable pageable) {
        logger.info("Obteniendo artículos - página: {}, tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Articulo> pagina = articuloRepositorio.findAll(pageable);
        return convertirAPaginaRespuesta(pagina);
    }

    /**
     * Obtiene un artículo por su ID.
     * 
     * @param id ID del artículo
     * @return artículo encontrado
     */
    @Transactional(readOnly = true)
    public ArticuloRespuesta obtenerPorId(Long id) {
        logger.info("Buscando artículo con ID: {}", id);
        Articulo articulo = buscarArticuloPorId(id);
        return convertirARespuesta(articulo);
    }

    /**
     * Obtiene artículos por categoría.
     * 
     * @param categoriaId ID de la categoría
     * @return lista de artículos
     */
    @Transactional(readOnly = true)
    public List<ArticuloRespuesta> obtenerPorCategoria(Long categoriaId) {
        logger.info("Obteniendo artículos de categoría ID: {}", categoriaId);
        return articuloRepositorio.findByCategoriaId(categoriaId)
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene artículos con stock bajo.
     * 
     * @return lista de artículos con stock bajo
     */
    @Transactional(readOnly = true)
    public List<ArticuloRespuesta> obtenerArticulosStockBajo() {
        logger.info("Obteniendo artículos con stock bajo");
        return articuloRepositorio.buscarArticulosStockBajo()
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene artículos agotados.
     * 
     * @return lista de artículos agotados
     */
    @Transactional(readOnly = true)
    public List<ArticuloRespuesta> obtenerArticulosAgotados() {
        logger.info("Obteniendo artículos agotados");
        return articuloRepositorio.buscarArticulosAgotados()
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Busca artículos por término de búsqueda.
     * 
     * @param termino término de búsqueda
     * @return lista de artículos encontrados
     */
    @Transactional(readOnly = true)
    public List<ArticuloRespuesta> buscar(String termino) {
        logger.info("Buscando artículos con término: {}", termino);
        return articuloRepositorio.buscarPorNombreODescripcion(termino)
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo artículo.
     * 
     * @param solicitud datos del artículo
     * @return artículo creado
     */
    public ArticuloRespuesta crear(ArticuloSolicitud solicitud) {
        logger.info("Creando nuevo artículo: {}", solicitud.getNombre());

        // Verificar nombre duplicado
        if (articuloRepositorio.existsByNombre(solicitud.getNombre())) {
            throw new RecursoDuplicadoExcepcion("Artículo", "nombre", solicitud.getNombre());
        }

        // Obtener categoría
        Categoria categoria = categoriaServicio.buscarCategoriaPorId(solicitud.getCategoriaId());

        Articulo articulo = Articulo.builder()
                .nombre(solicitud.getNombre())
                .descripcion(solicitud.getDescripcion())
                .categoria(categoria)
                .cantidadStock(solicitud.getCantidadStock())
                .stockMinimo(solicitud.getStockMinimo())
                .precioUnitario(solicitud.getPrecioUnitario())
                .build();

        Articulo guardado = articuloRepositorio.save(articulo);
        logger.info("Artículo creado con ID: {}", guardado.getId());

        return convertirARespuesta(guardado);
    }

    /**
     * Actualiza un artículo existente.
     * 
     * @param id        ID del artículo
     * @param solicitud nuevos datos
     * @return artículo actualizado
     */
    public ArticuloRespuesta actualizar(Long id, ArticuloSolicitud solicitud) {
        logger.info("Actualizando artículo con ID: {}", id);

        Articulo articulo = buscarArticuloPorId(id);

        // Verificar nombre duplicado (excluyendo el artículo actual)
        if (articuloRepositorio.existsByNombreAndIdNot(solicitud.getNombre(), id)) {
            throw new RecursoDuplicadoExcepcion("Artículo", "nombre", solicitud.getNombre());
        }

        // Obtener categoría si cambió
        if (!articulo.getCategoria().getId().equals(solicitud.getCategoriaId())) {
            Categoria nuevaCategoria = categoriaServicio.buscarCategoriaPorId(solicitud.getCategoriaId());
            articulo.setCategoria(nuevaCategoria);
        }

        articulo.setNombre(solicitud.getNombre());
        articulo.setDescripcion(solicitud.getDescripcion());
        articulo.setCantidadStock(solicitud.getCantidadStock());
        articulo.setStockMinimo(solicitud.getStockMinimo());
        articulo.setPrecioUnitario(solicitud.getPrecioUnitario());

        Articulo actualizado = articuloRepositorio.save(articulo);
        logger.info("Artículo actualizado: {}", actualizado.getNombre());

        return convertirARespuesta(actualizado);
    }

    /**
     * Elimina un artículo.
     * 
     * @param id ID del artículo
     */
    public void eliminar(Long id) {
        logger.info("Eliminando artículo con ID: {}", id);

        Articulo articulo = buscarArticuloPorId(id);
        articuloRepositorio.delete(articulo);

        logger.info("Artículo eliminado: {}", articulo.getNombre());
    }

    /**
     * Busca un artículo por ID o lanza excepción.
     */
    public Articulo buscarArticuloPorId(Long id) {
        return articuloRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Artículo", "id", id));
    }

    /**
     * Convierte entidad a DTO de respuesta.
     */
    public ArticuloRespuesta convertirARespuesta(Articulo articulo) {
        return ArticuloRespuesta.builder()
                .id(articulo.getId())
                .nombre(articulo.getNombre())
                .descripcion(articulo.getDescripcion())
                .categoria(CategoriaRespuesta.builder()
                        .id(articulo.getCategoria().getId())
                        .nombre(articulo.getCategoria().getNombre())
                        .build())
                .cantidadStock(articulo.getCantidadStock())
                .stockMinimo(articulo.getStockMinimo())
                .precioUnitario(articulo.getPrecioUnitario())
                .valorTotal(articulo.getValorTotal())
                .stockBajo(articulo.tieneStockBajo())
                .agotado(articulo.estaAgotado())
                .fechaCreacion(articulo.getFechaCreacion())
                .fechaActualizacion(articulo.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte Page a PaginaRespuesta.
     */
    private PaginaRespuesta<ArticuloRespuesta> convertirAPaginaRespuesta(Page<Articulo> pagina) {
        List<ArticuloRespuesta> contenido = pagina.getContent()
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());

        return PaginaRespuesta.<ArticuloRespuesta>builder()
                .contenido(contenido)
                .numeroPagina(pagina.getNumber())
                .tamanioPagina(pagina.getSize())
                .totalElementos(pagina.getTotalElements())
                .totalPaginas(pagina.getTotalPages())
                .esPrimera(pagina.isFirst())
                .esUltima(pagina.isLast())
                .estaVacia(pagina.isEmpty())
                .build();
    }
}
