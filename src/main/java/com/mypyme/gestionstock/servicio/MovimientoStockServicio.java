package com.mypyme.gestionstock.servicio;

import com.mypyme.gestionstock.dto.solicitud.MovimientoStockSolicitud;
import com.mypyme.gestionstock.dto.respuesta.MovimientoStockRespuesta;
import com.mypyme.gestionstock.entidad.Articulo;
import com.mypyme.gestionstock.entidad.MovimientoStock;
import com.mypyme.gestionstock.entidad.enums.TipoMovimiento;
import com.mypyme.gestionstock.excepcion.StockInsuficienteExcepcion;
import com.mypyme.gestionstock.repositorio.ArticuloRepositorio;
import com.mypyme.gestionstock.repositorio.MovimientoStockRepositorio;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de movimientos de stock.
 * RF06 - Registrar existencias (entradas/salidas)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoStockServicio {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoStockServicio.class);

    private final MovimientoStockRepositorio movimientoRepositorio;
    private final ArticuloServicio articuloServicio;
    private final ArticuloRepositorio articuloRepositorio;

    /**
     * Registra una entrada de stock (incremento).
     * 
     * @param solicitud datos del movimiento
     * @return movimiento registrado
     */
    public MovimientoStockRespuesta registrarEntrada(MovimientoStockSolicitud solicitud) {
        logger.info("Registrando entrada de stock para artículo ID: {}, cantidad: {}",
                solicitud.getArticuloId(), solicitud.getCantidad());

        return registrarMovimiento(solicitud, TipoMovimiento.ENTRADA);
    }

    /**
     * Registra una salida de stock (decremento).
     * 
     * @param solicitud datos del movimiento
     * @return movimiento registrado
     * @throws StockInsuficienteExcepcion si no hay stock suficiente
     */
    public MovimientoStockRespuesta registrarSalida(MovimientoStockSolicitud solicitud) {
        logger.info("Registrando salida de stock para artículo ID: {}, cantidad: {}",
                solicitud.getArticuloId(), solicitud.getCantidad());

        // Verificar stock disponible
        Articulo articulo = articuloServicio.buscarArticuloPorId(solicitud.getArticuloId());
        if (articulo.getCantidadStock() < solicitud.getCantidad()) {
            throw new StockInsuficienteExcepcion(
                    articulo.getId(),
                    articulo.getCantidadStock(),
                    solicitud.getCantidad());
        }

        return registrarMovimiento(solicitud, TipoMovimiento.SALIDA);
    }

    /**
     * Registra un ajuste de stock.
     * 
     * @param solicitud    datos del movimiento
     * @param esIncremento true si es incremento, false si es decremento
     * @return movimiento registrado
     */
    public MovimientoStockRespuesta registrarAjuste(MovimientoStockSolicitud solicitud, boolean esIncremento) {
        logger.info("Registrando ajuste de stock para artículo ID: {}, cantidad: {}, incremento: {}",
                solicitud.getArticuloId(), solicitud.getCantidad(), esIncremento);

        if (!esIncremento) {
            // Verificar stock disponible para decrementos
            Articulo articulo = articuloServicio.buscarArticuloPorId(solicitud.getArticuloId());
            if (articulo.getCantidadStock() < solicitud.getCantidad()) {
                throw new StockInsuficienteExcepcion(
                        articulo.getId(),
                        articulo.getCantidadStock(),
                        solicitud.getCantidad());
            }
        }

        return registrarMovimiento(solicitud, TipoMovimiento.AJUSTE);
    }

    /**
     * Obtiene movimientos por artículo.
     * 
     * @param articuloId ID del artículo
     * @return lista de movimientos
     */
    @Transactional(readOnly = true)
    public List<MovimientoStockRespuesta> obtenerPorArticulo(Long articuloId) {
        logger.info("Obteniendo movimientos del artículo ID: {}", articuloId);
        return movimientoRepositorio.findByArticuloIdOrderByFechaMovimientoDesc(articuloId)
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene movimientos por rango de fechas.
     * 
     * @param fechaInicio fecha inicio
     * @param fechaFin    fecha fin
     * @return lista de movimientos
     */
    @Transactional(readOnly = true)
    public List<MovimientoStockRespuesta> obtenerPorRangoFechas(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        logger.info("Obteniendo movimientos entre {} y {}", fechaInicio, fechaFin);
        return movimientoRepositorio.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(fechaInicio, fechaFin)
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los movimientos más recientes.
     * 
     * @return lista de movimientos recientes
     */
    @Transactional(readOnly = true)
    public List<MovimientoStockRespuesta> obtenerRecientes() {
        logger.info("Obteniendo movimientos recientes");
        return movimientoRepositorio.findTop10ByOrderByFechaMovimientoDesc()
                .stream()
                .map(this::convertirARespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Registra un movimiento de stock.
     */
    private MovimientoStockRespuesta registrarMovimiento(
            MovimientoStockSolicitud solicitud, TipoMovimiento tipo) {

        Articulo articulo = articuloServicio.buscarArticuloPorId(solicitud.getArticuloId());
        int stockAnterior = articulo.getCantidadStock();

        // Actualizar stock del artículo
        if (tipo == TipoMovimiento.ENTRADA) {
            articulo.incrementarStock(solicitud.getCantidad());
        } else if (tipo == TipoMovimiento.SALIDA) {
            articulo.decrementarStock(solicitud.getCantidad());
        } else if (tipo == TipoMovimiento.AJUSTE) {
            // El ajuste puede ser positivo o negativo según el motivo
            // Por defecto incrementamos, el llamador decide el signo
            articulo.incrementarStock(solicitud.getCantidad());
        }

        int stockNuevo = articulo.getCantidadStock();
        articuloRepositorio.save(articulo);

        // Crear movimiento
        MovimientoStock movimiento = MovimientoStock.builder()
                .articulo(articulo)
                .tipoMovimiento(tipo)
                .cantidad(solicitud.getCantidad())
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .motivo(solicitud.getMotivo())
                .referencia(solicitud.getReferencia())
                .creadoPor(solicitud.getCreadoPor())
                .fechaMovimiento(LocalDateTime.now())
                .build();

        MovimientoStock guardado = movimientoRepositorio.save(movimiento);

        logger.info("Movimiento registrado - Tipo: {}, Artículo: {}, Cantidad: {}, Stock anterior: {}, Stock nuevo: {}",
                tipo, articulo.getNombre(), solicitud.getCantidad(), stockAnterior, stockNuevo);

        return convertirARespuesta(guardado);
    }

    /**
     * Convierte entidad a DTO de respuesta.
     */
    private MovimientoStockRespuesta convertirARespuesta(MovimientoStock movimiento) {
        return MovimientoStockRespuesta.builder()
                .id(movimiento.getId())
                .articuloId(movimiento.getArticulo().getId())
                .articuloNombre(movimiento.getArticulo().getNombre())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .tipoMovimientoDescripcion(movimiento.getTipoMovimiento().getDescripcion())
                .cantidad(movimiento.getCantidad())
                .stockAnterior(movimiento.getStockAnterior())
                .stockNuevo(movimiento.getStockNuevo())
                .motivo(movimiento.getMotivo())
                .referencia(movimiento.getReferencia())
                .creadoPor(movimiento.getCreadoPor())
                .fechaMovimiento(movimiento.getFechaMovimiento())
                .fechaCreacion(movimiento.getFechaCreacion())
                .build();
    }
}
