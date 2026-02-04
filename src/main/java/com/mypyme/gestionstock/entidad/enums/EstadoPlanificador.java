package com.mypyme.gestionstock.entidad.enums;

/**
 * Enumeración que representa los estados de un planificador de inventario.
 */
public enum EstadoPlanificador {

    /**
     * Borrador - el plan está siendo creado, no programado
     */
    BORRADOR("Borrador"),

    /**
     * Programado - el plan está programado para ejecución
     */
    PROGRAMADO("Programado"),

    /**
     * En progreso - el plan se está ejecutando actualmente
     */
    EN_PROGRESO("En Progreso"),

    /**
     * Completado - el plan ha sido ejecutado completamente
     */
    COMPLETADO("Completado"),

    /**
     * Cancelado - el plan ha sido cancelado
     */
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoPlanificador(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
