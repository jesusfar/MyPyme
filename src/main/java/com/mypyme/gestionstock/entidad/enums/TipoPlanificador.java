package com.mypyme.gestionstock.entidad.enums;

/**
 * Enumeración que representa los tipos de planificador de inventario.
 */
public enum TipoPlanificador {

    /**
     * Inventario completo - incluye todos los artículos
     */
    INVENTARIO_COMPLETO("Inventario Completo"),

    /**
     * Por categoría - incluye artículos de una categoría específica
     */
    POR_CATEGORIA("Por Categoría"),

    /**
     * Por ubicación - incluye artículos de una ubicación específica
     */
    POR_UBICACION("Por Ubicación"),

    /**
     * Por artículos - incluye solo artículos seleccionados
     */
    POR_ARTICULOS("Por Artículos Seleccionados");

    private final String descripcion;

    TipoPlanificador(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
