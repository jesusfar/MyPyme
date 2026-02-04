package com.mypyme.gestionstock.excepcion;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra.
 * Retorna HTTP 404 Not Found.
 */
public class RecursoNoEncontradoExcepcion extends RuntimeException {

    private final String nombreRecurso;
    private final String nombreCampo;
    private final Object valorCampo;

    public RecursoNoEncontradoExcepcion(String nombreRecurso, String nombreCampo, Object valorCampo) {
        super(String.format("%s no encontrado con %s: '%s'", nombreRecurso, nombreCampo, valorCampo));
        this.nombreRecurso = nombreRecurso;
        this.nombreCampo = nombreCampo;
        this.valorCampo = valorCampo;
    }

    public RecursoNoEncontradoExcepcion(String mensaje) {
        super(mensaje);
        this.nombreRecurso = null;
        this.nombreCampo = null;
        this.valorCampo = null;
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
