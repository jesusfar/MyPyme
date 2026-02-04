package com.mypyme.gestionstock.servicio;

import com.mypyme.gestionstock.dto.solicitud.PlanificadorInventarioSolicitud;
import com.mypyme.gestionstock.dto.solicitud.EjecutarPlanificadorSolicitud;
import com.mypyme.gestionstock.dto.solicitud.ConteoItemPlanificadorSolicitud;
import com.mypyme.gestionstock.dto.respuesta.PlanificadorInventarioRespuesta;
import com.mypyme.gestionstock.dto.respuesta.ItemPlanificadorRespuesta;
import com.mypyme.gestionstock.entidad.*;
import com.mypyme.gestionstock.entidad.enums.EstadoPlanificador;
import com.mypyme.gestionstock.entidad.enums.TipoPlanificador;
import com.mypyme.gestionstock.excepcion.NegocioExcepcion;
import com.mypyme.gestionstock.excepcion.OperacionInvalidaExcepcion;
import com.mypyme.gestionstock.excepcion.RecursoNoEncontradoExcepcion;
import com.mypyme.gestionstock.repositorio.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de planificadores de inventario.
 * RF01 - Crear planificador de inventario
 * RF05 - Ejecutar planificador
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PlanificadorInventarioServicio {

    private static final Logger logger = LoggerFactory.getLogger(PlanificadorInventarioServicio.class);

    private final PlanificadorInventarioRepositorio planificadorRepositorio;
    private final ItemPlanificadorRepositorio itemRepositorio;
    private final ArticuloRepositorio articuloRepositorio;
    private final CategoriaServicio categoriaServicio;
    private final UbicacionAlmacenServicio ubicacionServicio;

    /**
     * Crea un nuevo planificador de inventario.
     * 
     * @param solicitud datos del planificador
     * @return planificador creado
     */
    public PlanificadorInventarioRespuesta crear(PlanificadorInventarioSolicitud solicitud) {
        logger.info("Creando planificador de inventario: {}", solicitud.getNombre());

        // Generar código de plan
        String codigoPlan = generarCodigoPlan();

        PlanificadorInventario planificador = PlanificadorInventario.builder()
                .codigoPlan(codigoPlan)
                .nombre(solicitud.getNombre())
                .descripcion(solicitud.getDescripcion())
                .tipoPlanificador(solicitud.getTipoPlanificador())
                .estado(EstadoPlanificador.BORRADOR)
                .creadoPor(solicitud.getCreadoPor())
                .build();

        // Asignar ubicación o categoría según el tipo
        if (solicitud.getTipoPlanificador() == TipoPlanificador.POR_UBICACION && solicitud.getUbicacionId() != null) {
            UbicacionAlmacen ubicacion = ubicacionServicio.buscarUbicacionPorId(solicitud.getUbicacionId());
            planificador.setUbicacion(ubicacion);
        }

        if (solicitud.getTipoPlanificador() == TipoPlanificador.POR_CATEGORIA && solicitud.getCategoriaId() != null) {
            Categoria categoria = categoriaServicio.buscarCategoriaPorId(solicitud.getCategoriaId());
            planificador.setCategoria(categoria);
        }

        // Programar si se especificó fecha
        if (solicitud.getFechaProgramada() != null) {
            planificador.programar(solicitud.getFechaProgramada());
        }

        PlanificadorInventario guardado = planificadorRepositorio.save(planificador);

        // Agregar items según el tipo
        List<Articulo> articulos = obtenerArticulosParaPlanificador(solicitud);
        for (Articulo articulo : articulos) {
            ItemPlanificador item = ItemPlanificador.builder()
                    .planificadorInventario(guardado)
                    .articulo(articulo)
                    .completado(false)
                    .build();
            guardado.agregarItem(item);
        }

        guardado = planificadorRepositorio.save(guardado);

        logger.info("Planificador creado - Código: {}, Items: {}", codigoPlan, guardado.getTotalItems());

        return convertirARespuesta(guardado, true);
    }

    /**
     * Ejecuta un planificador de inventario.
     * 
     * @param id        ID del planificador
     * @param solicitud datos de ejecución
     * @return planificador ejecutado
     */
    public PlanificadorInventarioRespuesta ejecutar(Long id, EjecutarPlanificadorSolicitud solicitud) {
        logger.info("Ejecutando planificador ID: {}", id);

        PlanificadorInventario planificador = buscarPlanificadorPorId(id);

        // Verificar estado válido para ejecución
        if (planificador.getEstado() != EstadoPlanificador.BORRADOR &&
                planificador.getEstado() != EstadoPlanificador.PROGRAMADO) {
            throw new OperacionInvalidaExcepcion(
                    "Solo se pueden ejecutar planificadores en estado BORRADOR o PROGRAMADO. Estado actual: " +
                            planificador.getEstado().getDescripcion());
        }

        // Capturar cantidades esperadas (cantidad actual del sistema)
        for (ItemPlanificador item : planificador.getItems()) {
            item.setCantidadEsperada(item.getArticulo().getCantidadStock());
        }

        planificador.ejecutar(solicitud.getEjecutadoPor());

        PlanificadorInventario guardado = planificadorRepositorio.save(planificador);

        logger.info("Planificador ejecutado - Código: {}", guardado.getCodigoPlan());

        return convertirARespuesta(guardado, true);
    }

    /**
     * Registra el conteo de un item del planificador.
     * 
     * @param planificadorId ID del planificador
     * @param itemId         ID del item
     * @param solicitud      datos del conteo
     * @return item actualizado
     */
    public ItemPlanificadorRespuesta contarItem(
            Long planificadorId, Long itemId, ConteoItemPlanificadorSolicitud solicitud) {

        logger.info("Contando item ID: {} del planificador ID: {}", itemId, planificadorId);

        PlanificadorInventario planificador = buscarPlanificadorPorId(planificadorId);

        // Verificar que el planificador esté en progreso
        if (planificador.getEstado() != EstadoPlanificador.EN_PROGRESO) {
            throw new OperacionInvalidaExcepcion(
                    "Solo se pueden contar items de planificadores en progreso. Estado actual: " +
                            planificador.getEstado().getDescripcion());
        }

        ItemPlanificador item = itemRepositorio.findById(itemId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Item del planificador", "id", itemId));

        // Verificar que el item pertenece al planificador
        if (!item.getPlanificadorInventario().getId().equals(planificadorId)) {
            throw new NegocioExcepcion("El item no pertenece al planificador especificado");
        }

        // Registrar conteo
        item.marcarComoContado(solicitud.getCantidadContada(), solicitud.getContadoPor());
        itemRepositorio.save(item);

        // Actualizar progreso del planificador
        planificador.incrementarItemsCompletados();

        // Verificar si se completó
        if (planificador.estaCompletamenteFinalizado()) {
            planificador.completar();
            logger.info("Planificador completado: {}", planificador.getCodigoPlan());
        }

        planificadorRepositorio.save(planificador);

        return convertirAItemRespuesta(item);
    }

    /**
     * Cancela un planificador.
     * 
     * @param id ID del planificador
     * @return planificador cancelado
     */
    public PlanificadorInventarioRespuesta cancelar(Long id) {
        logger.info("Cancelando planificador ID: {}", id);

        PlanificadorInventario planificador = buscarPlanificadorPorId(id);

        if (planificador.getEstado() == EstadoPlanificador.COMPLETADO) {
            throw new OperacionInvalidaExcepcion("No se puede cancelar un planificador completado");
        }

        planificador.cancelar();

        return convertirARespuesta(planificadorRepositorio.save(planificador), false);
    }

    /**
     * Obtiene todos los planificadores.
     * 
     * @return lista de planificadores
     */
    @Transactional(readOnly = true)
    public List<PlanificadorInventarioRespuesta> obtenerTodos() {
        logger.info("Obteniendo todos los planificadores");
        return planificadorRepositorio.findAll()
                .stream()
                .map(p -> convertirARespuesta(p, false))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un planificador por ID con items.
     * 
     * @param id ID del planificador
     * @return planificador con items
     */
    @Transactional(readOnly = true)
    public PlanificadorInventarioRespuesta obtenerPorId(Long id) {
        logger.info("Obteniendo planificador ID: {}", id);
        PlanificadorInventario planificador = buscarPlanificadorPorId(id);
        return convertirARespuesta(planificador, true);
    }

    /**
     * Obtiene planificadores por estado.
     * 
     * @param estado estado a filtrar
     * @return lista de planificadores
     */
    @Transactional(readOnly = true)
    public List<PlanificadorInventarioRespuesta> obtenerPorEstado(EstadoPlanificador estado) {
        logger.info("Obteniendo planificadores con estado: {}", estado);
        return planificadorRepositorio.findByEstadoOrderByFechaCreacionDesc(estado)
                .stream()
                .map(p -> convertirARespuesta(p, false))
                .collect(Collectors.toList());
    }

    /**
     * Busca un planificador por ID o lanza excepción.
     */
    public PlanificadorInventario buscarPlanificadorPorId(Long id) {
        return planificadorRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Planificador", "id", id));
    }

    /**
     * Obtiene artículos según el tipo de planificador.
     */
    private List<Articulo> obtenerArticulosParaPlanificador(PlanificadorInventarioSolicitud solicitud) {
        switch (solicitud.getTipoPlanificador()) {
            case INVENTARIO_COMPLETO:
                return articuloRepositorio.findAll();
            case POR_CATEGORIA:
                if (solicitud.getCategoriaId() == null) {
                    throw new NegocioExcepcion("Debe especificar una categoría para planificador por categoría");
                }
                return articuloRepositorio.findByCategoriaId(solicitud.getCategoriaId());
            case POR_ARTICULOS:
                if (solicitud.getArticuloIds() == null || solicitud.getArticuloIds().isEmpty()) {
                    throw new NegocioExcepcion("Debe especificar al menos un artículo");
                }
                return articuloRepositorio.findAllById(solicitud.getArticuloIds());
            case POR_UBICACION:
                // Para ubicación, por ahora retornamos todos los artículos
                // En una implementación completa se filtrarían por ubicación
                return articuloRepositorio.findAll();
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Genera código de plan único.
     */
    private String generarCodigoPlan() {
        String prefijo = "PLAN-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String maxCodigo = planificadorRepositorio.buscarMaxCodigoPlanPorPrefijo(prefijo);

        int siguiente = 1;
        if (maxCodigo != null) {
            String[] partes = maxCodigo.split("-");
            siguiente = Integer.parseInt(partes[partes.length - 1]) + 1;
        }

        return prefijo + String.format("%04d", siguiente);
    }

    /**
     * Convierte entidad a DTO de respuesta.
     */
    private PlanificadorInventarioRespuesta convertirARespuesta(
            PlanificadorInventario planificador, boolean incluirItems) {

        PlanificadorInventarioRespuesta.PlanificadorInventarioRespuestaBuilder builder = PlanificadorInventarioRespuesta
                .builder()
                .id(planificador.getId())
                .codigoPlan(planificador.getCodigoPlan())
                .nombre(planificador.getNombre())
                .descripcion(planificador.getDescripcion())
                .tipoPlanificador(planificador.getTipoPlanificador())
                .tipoPlanificadorDescripcion(planificador.getTipoPlanificador().getDescripcion())
                .estado(planificador.getEstado())
                .estadoDescripcion(planificador.getEstado().getDescripcion())
                .fechaProgramada(planificador.getFechaProgramada())
                .fechaInicio(planificador.getFechaInicio())
                .fechaCompletado(planificador.getFechaCompletado())
                .creadoPor(planificador.getCreadoPor())
                .ejecutadoPor(planificador.getEjecutadoPor())
                .totalItems(planificador.getTotalItems())
                .itemsCompletados(planificador.getItemsCompletados())
                .porcentajeProgreso(planificador.getPorcentajeProgreso())
                .fechaCreacion(planificador.getFechaCreacion())
                .fechaActualizacion(planificador.getFechaActualizacion());

        if (planificador.getUbicacion() != null) {
            builder.ubicacionId(planificador.getUbicacion().getId())
                    .ubicacionNombre(planificador.getUbicacion().getNombre());
        }

        if (planificador.getCategoria() != null) {
            builder.categoriaId(planificador.getCategoria().getId())
                    .categoriaNombre(planificador.getCategoria().getNombre());
        }

        if (incluirItems) {
            List<ItemPlanificadorRespuesta> items = planificador.getItems()
                    .stream()
                    .map(this::convertirAItemRespuesta)
                    .collect(Collectors.toList());
            builder.items(items);
        }

        return builder.build();
    }

    /**
     * Convierte item a DTO de respuesta.
     */
    private ItemPlanificadorRespuesta convertirAItemRespuesta(ItemPlanificador item) {
        return ItemPlanificadorRespuesta.builder()
                .id(item.getId())
                .articuloId(item.getArticulo().getId())
                .articuloNombre(item.getArticulo().getNombre())
                .ubicacionId(item.getUbicacion() != null ? item.getUbicacion().getId() : null)
                .ubicacionCodigo(item.getUbicacion() != null ? item.getUbicacion().getCodigo() : null)
                .ubicacionNombre(item.getUbicacion() != null ? item.getUbicacion().getNombre() : null)
                .cantidadEsperada(item.getCantidadEsperada())
                .cantidadContada(item.getCantidadContada())
                .diferencia(item.getDiferencia())
                .completado(item.getCompletado())
                .contadoPor(item.getContadoPor())
                .fechaCompletado(item.getFechaCompletado())
                .build();
    }
}
