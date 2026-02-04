package com.mypyme.gestionstock.servicio;

import com.mypyme.gestionstock.dto.solicitud.FiltroReporteSolicitud;
import com.mypyme.gestionstock.dto.respuesta.ReporteStockRespuesta;
import com.mypyme.gestionstock.entidad.Articulo;
import com.mypyme.gestionstock.entidad.Categoria;
import com.mypyme.gestionstock.repositorio.ArticuloRepositorio;
import com.mypyme.gestionstock.repositorio.CategoriaRepositorio;
import com.mypyme.gestionstock.repositorio.MovimientoStockRepositorio;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la generación de reportes de stock.
 * RF07 - Reportes de existencias consolidados
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteStockServicio {

    private static final Logger logger = LoggerFactory.getLogger(ReporteStockServicio.class);

    private final ArticuloRepositorio articuloRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final MovimientoStockRepositorio movimientoRepositorio;

    /**
     * Genera un reporte de stock consolidado.
     * 
     * @param filtro filtros opcionales
     * @return reporte generado
     */
    public ReporteStockRespuesta generarReporteStock(FiltroReporteSolicitud filtro) {
        logger.info("Generando reporte de stock");

        // Obtener artículos según filtros
        List<Articulo> articulos;
        if (filtro != null && filtro.getCategoriaId() != null) {
            articulos = articuloRepositorio.findByCategoriaId(filtro.getCategoriaId());
        } else if (filtro != null && filtro.getArticuloId() != null) {
            articulos = articuloRepositorio.findById(filtro.getArticuloId())
                    .map(List::of)
                    .orElse(new ArrayList<>());
        } else {
            articulos = articuloRepositorio.findAll();
        }

        // Calcular resumen
        ReporteStockRespuesta.ResumenReporte resumen = calcularResumen(articulos);

        // Resumen por categoría
        List<ReporteStockRespuesta.ResumenCategoria> porCategoria = calcularResumenPorCategoria(articulos);

        // Artículos con stock bajo
        List<ReporteStockRespuesta.ArticuloStockBajo> articulosStockBajo = obtenerArticulosStockBajo(articulos);

        // Detalle de artículos
        List<ReporteStockRespuesta.DetalleArticuloStock> detalleArticulos = articulos.stream()
                .map(this::convertirADetalleArticulo)
                .collect(Collectors.toList());

        // Construir filtros aplicados
        ReporteStockRespuesta.FiltrosAplicados filtrosAplicados = null;
        if (filtro != null) {
            filtrosAplicados = ReporteStockRespuesta.FiltrosAplicados.builder()
                    .categoriaId(filtro.getCategoriaId())
                    .ubicacionId(filtro.getUbicacionId())
                    .fechaInicio(filtro.getFechaInicio())
                    .fechaFin(filtro.getFechaFin())
                    .build();
        }

        return ReporteStockRespuesta.builder()
                .fechaReporte(LocalDateTime.now())
                .tituloReporte("Reporte de Stock Consolidado")
                .filtrosAplicados(filtrosAplicados)
                .resumen(resumen)
                .porCategoria(porCategoria)
                .articulosStockBajo(articulosStockBajo)
                .articulos(detalleArticulos)
                .fechaGeneracion(LocalDateTime.now())
                .build();
    }

    /**
     * Obtiene artículos con alerta de stock bajo.
     * 
     * @return lista de alertas
     */
    public List<ReporteStockRespuesta.ArticuloStockBajo> obtenerAlertasStockBajo() {
        logger.info("Obteniendo alertas de stock bajo");
        return articuloRepositorio.buscarArticulosStockBajo()
                .stream()
                .map(this::convertirAArticuloStockBajo)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas rápidas.
     * 
     * @return resumen de estadísticas
     */
    public ReporteStockRespuesta.ResumenReporte obtenerEstadisticasRapidas() {
        logger.info("Obteniendo estadísticas rápidas");
        List<Articulo> articulos = articuloRepositorio.findAll();
        return calcularResumen(articulos);
    }

    /**
     * Calcula el resumen general.
     */
    private ReporteStockRespuesta.ResumenReporte calcularResumen(List<Articulo> articulos) {
        long totalArticulos = articulos.size();
        long cantidadTotal = articulos.stream()
                .mapToLong(Articulo::getCantidadStock)
                .sum();
        BigDecimal valorTotal = articulos.stream()
                .map(Articulo::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long articulosStockBajo = articulos.stream()
                .filter(Articulo::tieneStockBajo)
                .count();
        long articulosAgotados = articulos.stream()
                .filter(Articulo::estaAgotado)
                .count();

        return ReporteStockRespuesta.ResumenReporte.builder()
                .totalArticulos(totalArticulos)
                .cantidadTotal(cantidadTotal)
                .valorTotal(valorTotal)
                .articulosStockBajo(articulosStockBajo)
                .articulosAgotados(articulosAgotados)
                .build();
    }

    /**
     * Calcula resumen por categoría.
     */
    private List<ReporteStockRespuesta.ResumenCategoria> calcularResumenPorCategoria(List<Articulo> articulos) {
        long cantidadTotalGeneral = articulos.stream()
                .mapToLong(Articulo::getCantidadStock)
                .sum();

        return categoriaRepositorio.findAll().stream()
                .map(categoria -> {
                    List<Articulo> articulosCategoria = articulos.stream()
                            .filter(a -> a.getCategoria().getId().equals(categoria.getId()))
                            .collect(Collectors.toList());

                    long cantidadArticulos = articulosCategoria.size();
                    long cantidadTotal = articulosCategoria.stream()
                            .mapToLong(Articulo::getCantidadStock)
                            .sum();
                    BigDecimal valorTotal = articulosCategoria.stream()
                            .map(Articulo::getValorTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    double porcentaje = cantidadTotalGeneral > 0
                            ? (cantidadTotal * 100.0) / cantidadTotalGeneral
                            : 0.0;

                    return ReporteStockRespuesta.ResumenCategoria.builder()
                            .categoriaId(categoria.getId())
                            .categoriaNombre(categoria.getNombre())
                            .cantidadArticulos(cantidadArticulos)
                            .cantidadTotal(cantidadTotal)
                            .valorTotal(valorTotal)
                            .porcentajeDelTotal(porcentaje)
                            .build();
                })
                .filter(r -> r.getCantidadArticulos() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene artículos con stock bajo.
     */
    private List<ReporteStockRespuesta.ArticuloStockBajo> obtenerArticulosStockBajo(List<Articulo> articulos) {
        return articulos.stream()
                .filter(a -> a.tieneStockBajo() || a.estaAgotado())
                .map(this::convertirAArticuloStockBajo)
                .collect(Collectors.toList());
    }

    /**
     * Convierte artículo a detalle de stock.
     */
    private ReporteStockRespuesta.DetalleArticuloStock convertirADetalleArticulo(Articulo articulo) {
        String estado;
        if (articulo.estaAgotado()) {
            estado = "AGOTADO";
        } else if (articulo.tieneStockBajo()) {
            estado = "BAJO";
        } else {
            estado = "NORMAL";
        }

        return ReporteStockRespuesta.DetalleArticuloStock.builder()
                .id(articulo.getId())
                .nombre(articulo.getNombre())
                .categoriaNombre(articulo.getCategoria().getNombre())
                .cantidadStock(articulo.getCantidadStock())
                .stockMinimo(articulo.getStockMinimo())
                .precioUnitario(articulo.getPrecioUnitario())
                .valorTotal(articulo.getValorTotal())
                .estado(estado)
                .build();
    }

    /**
     * Convierte artículo a alerta de stock bajo.
     */
    private ReporteStockRespuesta.ArticuloStockBajo convertirAArticuloStockBajo(Articulo articulo) {
        String estado;
        int deficit = articulo.getStockMinimo() - articulo.getCantidadStock();

        if (articulo.estaAgotado()) {
            estado = "CRITICO";
        } else if (deficit > articulo.getStockMinimo() / 2) {
            estado = "ADVERTENCIA";
        } else {
            estado = "BAJO";
        }

        return ReporteStockRespuesta.ArticuloStockBajo.builder()
                .articuloId(articulo.getId())
                .articuloNombre(articulo.getNombre())
                .categoriaNombre(articulo.getCategoria().getNombre())
                .stockActual(articulo.getCantidadStock())
                .stockMinimo(articulo.getStockMinimo())
                .deficit(deficit)
                .estado(estado)
                .build();
    }
}
