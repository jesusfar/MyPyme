package com.mypyme.gestionstock.entidad.enums;

/**
 * Enumeración que representa los tipos de movimiento de stock.
 */
public enum TipoMovimiento {

    /**
     * Entrada de stock - agrega cantidad al inventario
     */
    ENTRADA("Entrada de Stock"),

    /**
     * Salida de stock - remueve cantidad del inventario
     */
    SALIDA("Salida de Stock"),

    /**
     * Ajuste de stock - corrige discrepancias de inventario
     */
    AJUSTE("Ajuste de Stock");

    private final String descripcion;

    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
