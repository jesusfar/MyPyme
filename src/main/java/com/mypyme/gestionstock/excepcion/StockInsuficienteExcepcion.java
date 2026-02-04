package com.mypyme.gestionstock.excepcion;

/**
 * Excepción lanzada cuando no hay stock suficiente para una operación.
 * Retorna HTTP 400 Bad Request.
 */
public class StockInsuficienteExcepcion extends NegocioExcepcion {

    private final Long articuloId;
    private final Integer stockActual;
    private final Integer cantidadSolicitada;

    public StockInsuficienteExcepcion(Long articuloId, Integer stockActual, Integer cantidadSolicitada) {
        super(String.format("Stock insuficiente para el artículo ID %d. Stock actual: %d, Cantidad solicitada: %d",
                articuloId, stockActual, cantidadSolicitada), "STOCK_INSUFICIENTE");
        this.articuloId = articuloId;
        this.stockActual = stockActual;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public Long getArticuloId() {
        return articuloId;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
