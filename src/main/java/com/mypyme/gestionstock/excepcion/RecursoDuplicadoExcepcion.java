package com.mypyme.gestionstock.excepcion;

/**
 * Excepción lanzada cuando se intenta crear un recurso duplicado.
 * Retorna HTTP 409 Conflict.
 */
public class RecursoDuplicadoExcepcion extends RuntimeException {

    private final String nombreRecurso;
    private final String nombreCampo;
    private final Object valorCampo;

    public RecursoDuplicadoExcepcion(String nombreRecurso, String nombreCampo, Object valorCampo) {
        super(String.format("Ya existe un %s con %s: '%s'", nombreRecurso, nombreCampo, valorCampo));
        this.nombreRecurso = nombreRecurso;
        this.nombreCampo = nombreCampo;
        this.valorCampo = valorCampo;
    }

    public String getNombreRecurso() {
        return nombreRecurso;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public Object getValorCampo() {
        return valorCampo;
    }
}
