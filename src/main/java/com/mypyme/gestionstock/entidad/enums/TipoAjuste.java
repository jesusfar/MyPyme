package com.mypyme.gestionstock.entidad.enums;

/**
 * Enumeración que representa los tipos de ajuste de stock.
 */
public enum TipoAjuste {

    /**
     * Ajuste de incremento - agrega stock
     */
    INCREMENTO("Incremento de Stock"),

    /**
     * Ajuste de decremento - reduce stock
     */
    DECREMENTO("Decremento de Stock");

    private final String descripcion;

    TipoAjuste(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
