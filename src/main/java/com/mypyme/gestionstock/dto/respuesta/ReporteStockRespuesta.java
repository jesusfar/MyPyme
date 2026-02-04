package com.mypyme.gestionstock.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para reportes de stock.
 * RF07 - Reportes de existencias consolidados
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteStockRespuesta {

    private LocalDateTime fechaReporte;
    private String tituloReporte;
    private FiltrosAplicados filtrosAplicados;
    private ResumenReporte resumen;
    private List<ResumenCategoria> porCategoria;
    private List<ArticuloStockBajo> articulosStockBajo;
    private List<DetalleArticuloStock> articulos;
    private String generadoPor;
    private LocalDateTime fechaGeneracion;

    /**
     * Filtros aplicados al reporte
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FiltrosAplicados {
        private Long categoriaId;
        private String categoriaNombre;
        private Long ubicacionId;
        private String ubicacionNombre;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
    }

    /**
     * Resumen general del reporte
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResumenReporte {
        private Long totalArticulos;
        private Long cantidadTotal;
        private BigDecimal valorTotal;
        private Long articulosStockBajo;
        private Long articulosAgotados;
    }

    /**
     * Resumen por categoría
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResumenCategoria {
        private Long categoriaId;
        private String categoriaNombre;
        private Long cantidadArticulos;
        private Long cantidadTotal;
        private BigDecimal valorTotal;
        private Double porcentajeDelTotal;
    }

    /**
     * Artículos con stock bajo
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArticuloStockBajo {
        private Long articuloId;
        private String articuloNombre;
        private String categoriaNombre;
        private Integer stockActual;
        private Integer stockMinimo;
        private Integer deficit;
        private String estado; // CRITICO, ADVERTENCIA, OK
    }

    /**
     * Detalle de artículo en el reporte
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleArticuloStock {
        private Long id;
        private String nombre;
        private String categoriaNombre;
        private Integer cantidadStock;
        private Integer stockMinimo;
        private BigDecimal precioUnitario;
        private BigDecimal valorTotal;
        private String estado;
    }
}
