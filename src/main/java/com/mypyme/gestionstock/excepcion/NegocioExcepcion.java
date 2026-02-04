package com.mypyme.gestionstock.excepcion;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 * Retorna HTTP 400 Bad Request.
 */
public class NegocioExcepcion extends RuntimeException {

    private final String codigoError;

    public NegocioExcepcion(String mensaje) {
        super(mensaje);
        this.codigoError = "ERROR_NEGOCIO";
    }

    public NegocioExcepcion(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public String getCodigoError() {
        return codigoError;
    }
}
