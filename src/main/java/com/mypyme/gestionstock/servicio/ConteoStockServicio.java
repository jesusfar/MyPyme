package com.mypyme.gestionstock.servicio;

import com.mypyme.gestionstock.dto.solicitud.ConteoStockSolicitud;
import com.mypyme.gestionstock.dto.solicitud.AjusteStockSolicitud;
import com.mypyme.gestionstock.dto.respuesta.ConteoStockRespuesta;
import com.mypyme.gestionstock.dto.respuesta.AjusteStockRespuesta;
import com.mypyme.gestionstock.entidad.*;
import com.mypyme.gestionstock.entidad.enums.EstadoConteo;
import com.mypyme.gestionstock.entidad.enums.TipoAjuste;
import com.mypyme.gestionstock.entidad.enums.TipoMovimiento;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de conteos de stock y ajustes.
 * RF03 - Conteo de existencias y registro
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ConteoStockServicio {

    private static final Logger logger = LoggerFactory.getLogger(ConteoStockServicio.class);

    private final ConteoStockRepositorio conteoRepositorio;
    private final AjusteStockRepositorio ajusteRepositorio;
    private final ArticuloServicio articuloServicio;
    private final ArticuloRepositorio articuloRepositorio;
    private final UbicacionAlmacenServicio ubicacionServicio;
    private final MovimientoStockRepositorio movimientoRepositorio;

    /**
     * Registra un nuevo conteo de stock.
     * 
     * @param solicitud datos del conteo
     * @return conteo registrado
     */
    public ConteoStockRespuesta registrarConteo(ConteoStockSolicitud solicitud) {
        logger.info("Registrando conteo de stock para artículo ID: {}", solicitud.getArticuloId());

        Articulo articulo = articuloServicio.buscarArticuloPorId(solicitud.getArticuloId());

        UbicacionAlmacen ubicacion = null;
        if (solicitud.getUbicacionId() != null) {
            ubicacion = ubicacionServicio.buscarUbicacionPorId(solicitud.getUbicacionId());
        }

        // Generar número de conteo
        String numeroConteo = generarNumeroConteo();

        // Capturar cantidad del sistema
        Integer cantidadSistema = articulo.getCantidadStock();

        ConteoStock conteo = ConteoStock.builder()
                .numeroConteo(numeroConteo)
                .articulo(articulo)
                .ubicacion(ubicacion)
                .cantidadSistema(cantidadSistema)
                .cantidadContada(solicitud.getCantidadContada())
                .usuarioResponsable(solicitud.getUsuarioResponsable())
                .notas(solicitud.getNotas())
                .fechaConteo(LocalDateTime.now())
                .estado(EstadoConteo.COMPLETADO)
                .build();

        conteo.calcularDiferencia();

        ConteoStock guardado = conteoRepositorio.save(conteo);

        logger.info("Conteo registrado - Número: {}, Sistema: {}, Contado: {}, Diferencia: {}",
                numeroConteo, cantidadSistema, solicitud.getCantidadContada(), guardado.getDiferencia());

        return convertirAConteoRespuesta(guardado);
    }

    /**
     * Aplica un ajuste basado en un conteo de stock.
     * 
     * @param conteoId  ID del conteo
     * @param solicitud datos del ajuste
     * @return ajuste aplicado
     */
    public AjusteStockRespuesta aplicarAjuste(Long conteoId, AjusteStockSolicitud solicitud) {
        logger.info("Aplicando ajuste para conteo ID: {}", conteoId);

        ConteoStock conteo = conteoRepositorio.findById(conteoId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Conteo", "id", conteoId));

        // Verificar que el conteo no esté ya ajustado
        if (conteo.getEstado() == EstadoConteo.AJUSTADO) {
            throw new OperacionInvalidaExcepcion("Este conteo ya ha sido ajustado");
        }

        // Verificar que haya diferencia
        if (!conteo.tieneDiferencia()) {
            throw new NegocioExcepcion("No hay diferencia que ajustar en este conteo");
        }

        Articulo articulo = conteo.getArticulo();
        int stockAnterior = articulo.getCantidadStock();
        int diferencia = conteo.getDiferencia();

        // Determinar tipo de ajuste
        TipoAjuste tipoAjuste;
        if (diferencia > 0) {
            tipoAjuste = TipoAjuste.INCREMENTO;
            articulo.incrementarStock(diferencia);
        } else {
            tipoAjuste = TipoAjuste.DECREMENTO;
            articulo.decrementarStock(Math.abs(diferencia));
        }

        int stockNuevo = articulo.getCantidadStock();
        articuloRepositorio.save(articulo);

        // Crear ajuste
        AjusteStock ajuste = AjusteStock.builder()
                .conteoStock(conteo)
                .articulo(articulo)
                .tipoAjuste(tipoAjuste)
                .cantidad(Math.abs(diferencia))
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .motivo(solicitud.getMotivo())
                .aprobadoPor(solicitud.getAprobadoPor())
                .fechaAjuste(LocalDateTime.now())
                .build();

        AjusteStock ajusteGuardado = ajusteRepositorio.save(ajuste);

        // Actualizar estado del conteo
        conteo.setEstado(EstadoConteo.AJUSTADO);
        conteoRepositorio.save(conteo);

        // Registrar movimiento
        MovimientoStock movimiento = MovimientoStock.builder()
                .articulo(articulo)
                .tipoMovimiento(TipoMovimiento.AJUSTE)
                .cantidad(Math.abs(diferencia))
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .motivo("Ajuste por conteo " + conteo.getNumeroConteo() + ": " + solicitud.getMotivo())
                .creadoPor(solicitud.getAprobadoPor())
                .fechaMovimiento(LocalDateTime.now())
                .build();

        movimientoRepositorio.save(movimiento);

        logger.info("Ajuste aplicado - Tipo: {}, Cantidad: {}, Stock anterior: {}, Stock nuevo: {}",
                tipoAjuste, Math.abs(diferencia), stockAnterior, stockNuevo);

        return convertirAAjusteRespuesta(ajusteGuardado);
    }

    /**
     * Obtiene todos los conteos.
     * 
     * @return lista de conteos
     */
    @Transactional(readOnly = true)
    public List<ConteoStockRespuesta> obtenerTodosConteos() {
        logger.info("Obteniendo todos los conteos");
        return conteoRepositorio.findAll()
                .stream()
                .map(this::convertirAConteoRespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene conteos con diferencias.
     * 
     * @return lista de conteos con diferencias
     */
    @Transactional(readOnly = true)
    public List<ConteoStockRespuesta> obtenerConteosConDiferencias() {
        logger.info("Obteniendo conteos con diferencias");
        return conteoRepositorio.buscarConteosConDiferencias()
                .stream()
                .map(this::convertirAConteoRespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene conteos pendientes de ajuste.
     * 
     * @return lista de conteos pendientes
     */
    @Transactional(readOnly = true)
    public List<ConteoStockRespuesta> obtenerConteosPendientesAjuste() {
        logger.info("Obteniendo conteos pendientes de ajuste");
        return conteoRepositorio.buscarConteosNoAjustadosConDiferencias()
                .stream()
                .map(this::convertirAConteoRespuesta)
                .collect(Collectors.toList());
    }

    /**
     * Genera número de conteo único.
     */
    private String generarNumeroConteo() {
        String prefijo = "CON-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String maxNumero = conteoRepositorio.buscarMaxNumeroConteoPorPrefijo(prefijo);

        int siguiente = 1;
        if (maxNumero != null) {
            String[] partes = maxNumero.split("-");
            siguiente = Integer.parseInt(partes[partes.length - 1]) + 1;
        }

        return prefijo + String.format("%04d", siguiente);
    }

    /**
     * Convierte entidad ConteoStock a DTO de respuesta.
     */
    private ConteoStockRespuesta convertirAConteoRespuesta(ConteoStock conteo) {
        return ConteoStockRespuesta.builder()
                .id(conteo.getId())
                .numeroConteo(conteo.getNumeroConteo())
                .articuloId(conteo.getArticulo().getId())
                .articuloNombre(conteo.getArticulo().getNombre())
                .ubicacionId(conteo.getUbicacion() != null ? conteo.getUbicacion().getId() : null)
                .ubicacionCodigo(conteo.getUbicacion() != null ? conteo.getUbicacion().getCodigo() : null)
                .ubicacionNombre(conteo.getUbicacion() != null ? conteo.getUbicacion().getNombre() : null)
                .cantidadSistema(conteo.getCantidadSistema())
                .cantidadContada(conteo.getCantidadContada())
                .diferencia(conteo.getDiferencia())
                .estado(conteo.getEstado())
                .estadoDescripcion(conteo.getEstado().getDescripcion())
                .usuarioResponsable(conteo.getUsuarioResponsable())
                .notas(conteo.getNotas())
                .fechaConteo(conteo.getFechaConteo())
                .tieneAjuste(conteo.tieneAjuste())
                .fechaCreacion(conteo.getFechaCreacion())
                .build();
    }

    /**
     * Convierte entidad AjusteStock a DTO de respuesta.
     */
    private AjusteStockRespuesta convertirAAjusteRespuesta(AjusteStock ajuste) {
        return AjusteStockRespuesta.builder()
                .id(ajuste.getId())
                .conteoStockId(ajuste.getConteoStock().getId())
                .numeroConteo(ajuste.getConteoStock().getNumeroConteo())
                .articuloId(ajuste.getArticulo().getId())
                .articuloNombre(ajuste.getArticulo().getNombre())
                .tipoAjuste(ajuste.getTipoAjuste())
                .tipoAjusteDescripcion(ajuste.getTipoAjuste().getDescripcion())
                .cantidad(ajuste.getCantidad())
                .stockAnterior(ajuste.getStockAnterior())
                .stockNuevo(ajuste.getStockNuevo())
                .motivo(ajuste.getMotivo())
                .aprobadoPor(ajuste.getAprobadoPor())
                .fechaAjuste(ajuste.getFechaAjuste())
                .fechaCreacion(ajuste.getFechaCreacion())
                .build();
    }
}
