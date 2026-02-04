package com.mypyme.gestionstock.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas.
 * 
 * @param <T> Tipo de contenido en la página
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginaRespuesta<T> {

    private List<T> contenido;
    private Integer numeroPagina;
    private Integer tamanioPagina;
    private Long totalElementos;
    private Integer totalPaginas;
    private Boolean esPrimera;
    private Boolean esUltima;
    private Boolean estaVacia;
}
