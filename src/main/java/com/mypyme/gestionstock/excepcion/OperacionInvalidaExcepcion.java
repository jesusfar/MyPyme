package com.mypyme.gestionstock.excepcion;

/**
 * Excepción lanzada cuando se intenta realizar una operación inválida.
 * Por ejemplo, transiciones de estado no permitidas.
 * Retorna HTTP 400 Bad Request.
 */
public class OperacionInvalidaExcepcion extends NegocioExcepcion {

    public OperacionInvalidaExcepcion(String mensaje) {
        super(mensaje, "OPERACION_INVALIDA");
    }

    public OperacionInvalidaExcepcion(String mensaje, String codigoError) {
        super(mensaje, codigoError);
    }
}
