package com.mypyme.gestionstock.entidad.enums;

/**
 * Enumeración que representa los estados de un conteo de stock.
 */
public enum EstadoConteo {

    /**
     * Conteo pendiente - aún no completado
     */
    PENDIENTE("Pendiente"),

    /**
     * Conteo en progreso
     */
    EN_PROGRESO("En Progreso"),

    /**
     * Conteo completado pero sin ajustar
     */
    COMPLETADO("Completado"),

    /**
     * Conteo ajustado - el stock ha sido corregido
     */
    AJUSTADO("Ajustado");

    private final String descripcion;

    EstadoConteo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
