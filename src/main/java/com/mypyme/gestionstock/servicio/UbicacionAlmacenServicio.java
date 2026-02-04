package com.mypyme.gestionstock.servicio;

import com.mypyme.gestionstock.dto.solicitud.UbicacionAlmacenSolicitud;
import com.mypyme.gestionstock.dto.respuesta.UbicacionAlmacenRespuesta;
import com.mypyme.gestionstock.entidad.UbicacionAlmacen;
import com.mypyme.gestionstock.excepcion.RecursoDuplicadoExcepcion;
import com.mypyme.gestionstock.excepcion.RecursoNoEncontradoExcepcion;
import com.mypyme.gestionstock.repositorio.ArticuloUbicacionRepositorio;
import com.mypyme.gestionstock.repositorio.UbicacionAlmacenRepositorio;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de ubicaciones de almacén.
 * RF04 - Crear ubicaciones
 * RF08 - Registrar ubicación
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UbicacionAlmacenServicio {

    private static final Logger logger = LoggerFactory.getLogger(UbicacionAlmacenServicio.class);

    private final UbicacionAlmacenRepositorio ubicacionRepositorio;
    private final ArticuloUbicacionRepositorio articuloUbicacionRepositorio;

    /**
     * Obtiene todas las ubicaciones.
     * 
     * @return lista de ubicaciones
     */
    @Transactional(readOnly = true)
    public List<UbicacionAlmacenRespuesta> obtenerTodas() {
        logger.info("Obteniendo todas las ubicaciones");
        return ubicacionRepositorio.findAll()
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene ubicaciones activas.
     * 
     * @return lista de ubicaciones activas
     */
    @Transactional(readOnly = true)
    public List<UbicacionAlmacenRespuesta> obtenerActivas() {
        logger.info("Obteniendo ubicaciones activas");
        return ubicacionRepositorio.findByActivaTrue()
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una ubicación por su ID.
     * 
     * @param id ID de la ubicación
     * @return ubicación encontrada
     */
    @Transactional(readOnly = true)
    public UbicacionAlmacenRespuesta obtenerPorId(Long id) {
        logger.info("Buscando ubicación con ID: {}", id);
        UbicacionAlmacen ubicacion = buscarUbicacionPorId(id);
        return convertirARespuesta(ubicacion);
    }

    /**
     * Obtiene ubicaciones por zona.
     * 
     * @param zona nombre de la zona
     * @return lista de ubicaciones
     */
    @Transactional(readOnly = true)
    public List<UbicacionAlmacenRespuesta> obtenerPorZona(String zona) {
        logger.info("Obteniendo ubicaciones de la zona: {}", zona);
        return ubicacionRepositorio.findByZonaAndActivaTrue(zona)
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las zonas distintas.
     * 
     * @return lista de zonas
     */
    @Transactional(readOnly = true)
    public List<String> obtenerZonas() {
        logger.info("Obteniendo zonas distintas");
        return ubicacionRepositorio.buscarZonasDistintas();
    }

    /**
     * Crea una nueva ubicación.
     * 
     * @param solicitud datos de la ubicación
     * @return ubicación creada
     */
    public UbicacionAlmacenRespuesta crear(UbicacionAlmacenSolicitud solicitud) {
        logger.info("Creando nueva ubicación: {}", solicitud.getCodigo());

        // Verificar código duplicado
        if (ubicacionRepositorio.existsByCodigo(solicitud.getCodigo())) {
            throw new RecursoDuplicadoExcepcion("Ubicación", "código", solicitud.getCodigo());
        }

        UbicacionAlmacen ubicacion = UbicacionAlmacen.builder()
                .codigo(solicitud.getCodigo())
                .nombre(solicitud.getNombre())
                .descripcion(solicitud.getDescripcion())
                .zona(solicitud.getZona())
                .capacidad(solicitud.getCapacidad())
                .activa(true)
                .build();

        UbicacionAlmacen guardada = ubicacionRepositorio.save(ubicacion);
        logger.info("Ubicación creada con ID: {}", guardada.getId());

        return convertirARespuesta(guardada);
    }

    /**
     * Actualiza una ubicación existente.
     * 
     * @param id        ID de la ubicación
     * @param solicitud nuevos datos
     * @return ubicación actualizada
     */
    public UbicacionAlmacenRespuesta actualizar(Long id, UbicacionAlmacenSolicitud solicitud) {
        logger.info("Actualizando ubicación con ID: {}", id);

        UbicacionAlmacen ubicacion = buscarUbicacionPorId(id);

        // Verificar código duplicado (excluyendo la ubicación actual)
        if (ubicacionRepositorio.existsByCodigoAndIdNot(solicitud.getCodigo(), id)) {
            throw new RecursoDuplicadoExcepcion("Ubicación", "código", solicitud.getCodigo());
        }

        ubicacion.setCodigo(solicitud.getCodigo());
        ubicacion.setNombre(solicitud.getNombre());
        ubicacion.setDescripcion(solicitud.getDescripcion());
        ubicacion.setZona(solicitud.getZona());
        ubicacion.setCapacidad(solicitud.getCapacidad());

        UbicacionAlmacen actualizada = ubicacionRepositorio.save(ubicacion);
        logger.info("Ubicación actualizada: {}", actualizada.getCodigo());

        return convertirARespuesta(actualizada);
    }

    /**
     * Activa una ubicación.
     * 
     * @param id ID de la ubicación
     * @return ubicación activada
     */
    public UbicacionAlmacenRespuesta activar(Long id) {
        logger.info("Activando ubicación con ID: {}", id);

        UbicacionAlmacen ubicacion = buscarUbicacionPorId(id);
        ubicacion.activar();

        return convertirARespuesta(ubicacionRepositorio.save(ubicacion));
    }

    /**
     * Desactiva una ubicación.
     * 
     * @param id ID de la ubicación
     * @return ubicación desactivada
     */
    public UbicacionAlmacenRespuesta desactivar(Long id) {
        logger.info("Desactivando ubicación con ID: {}", id);

        UbicacionAlmacen ubicacion = buscarUbicacionPorId(id);
        ubicacion.desactivar();

        return convertirARespuesta(ubicacionRepositorio.save(ubicacion));
    }

    /**
     * Elimina una ubicación.
     * 
     * @param id ID de la ubicación
     */
    public void eliminar(Long id) {
        logger.info("Eliminando ubicación con ID: {}", id);

        UbicacionAlmacen ubicacion = buscarUbicacionPorId(id);

        // Verificar si hay artículos asignados
        long cantidadArticulos = articuloUbicacionRepositorio.countByUbicacionAlmacenId(id);
        if (cantidadArticulos > 0) {
            throw new com.mypyme.gestionstock.excepcion.NegocioExcepcion(
                    "No se puede eliminar la ubicación porque tiene " + cantidadArticulos + " artículos asignados");
        }

        ubicacionRepositorio.delete(ubicacion);
        logger.info("Ubicación eliminada: {}", ubicacion.getCodigo());
    }

    /**
     * Busca una ubicación por ID o lanza excepción.
     */
    public UbicacionAlmacen buscarUbicacionPorId(Long id) {
        return ubicacionRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Ubicación", "id", id));
    }

    /**
     * Convierte entidad a DTO de respuesta.
     */
    private UbicacionAlmacenRespuesta convertirARespuesta(UbicacionAlmacen ubicacion) {
        long cantidadArticulos = articuloUbicacionRepositorio.countByUbicacionAlmacenId(ubicacion.getId());
        int capacidadUsada = articuloUbicacionRepositorio.sumarCantidadPorUbicacion(ubicacion.getId());

        return UbicacionAlmacenRespuesta.builder()
                .id(ubicacion.getId())
                .codigo(ubicacion.getCodigo())
                .nombre(ubicacion.getNombre())
                .descripcion(ubicacion.getDescripcion())
                .zona(ubicacion.getZona())
                .capacidad(ubicacion.getCapacidad())
                .capacidadUsada(capacidadUsada)
                .activa(ubicacion.getActiva())
                .cantidadArticulos(cantidadArticulos)
                .fechaCreacion(ubicacion.getFechaCreacion())
                .fechaActualizacion(ubicacion.getFechaActualizacion())
                .build();
    }
}
