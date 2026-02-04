package com.mypyme.gestionstock.excepcion;

import com.mypyme.gestionstock.dto.respuesta.ErrorApiRespuesta;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la API REST.
 * Captura todas las excepciones y las convierte en respuestas JSON
 * estandarizadas.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    private static final Logger logger = LoggerFactory.getLogger(ManejadorGlobalExcepciones.class);

    /**
     * Maneja excepciones de recurso no encontrado.
     * Retorna HTTP 404.
     */
    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    public ResponseEntity<ErrorApiRespuesta> manejarRecursoNoEncontrado(
            RecursoNoEncontradoExcepcion ex, HttpServletRequest solicitud) {

        logger.warn("Recurso no encontrado: {}", ex.getMessage());

        ErrorApiRespuesta error = ErrorApiRespuesta.builder()
                .estado(HttpStatus.NOT_FOUND.value())
                .error("No Encontrado")
                .mensaje(ex.getMessage())
                .ruta(solicitud.getRequestURI())
                .marca(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja excepciones de reglas de negocio.
     * Retorna HTTP 400.
     */
    @ExceptionHandler(NegocioExcepcion.class)
    public ResponseEntity<ErrorApiRespuesta> manejarNegocioExcepcion(
            NegocioExcepcion ex, HttpServletRequest solicitud) {

        logger.warn("Error de negocio: {}", ex.getMessage());

        ErrorApiRespuesta error = ErrorApiRespuesta.builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .error(ex.getCodigoError())
                .mensaje(ex.getMessage())
                .ruta(solicitud.getRequestURI())
                .marca(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones de recursos duplicados.
     * Retorna HTTP 409.
     */
    @ExceptionHandler(RecursoDuplicadoExcepcion.class)
    public ResponseEntity<ErrorApiRespuesta> manejarRecursoDuplicado(
            RecursoDuplicadoExcepcion ex, HttpServletRequest solicitud) {

        logger.warn("Recurso duplicado: {}", ex.getMessage());

        ErrorApiRespuesta error = ErrorApiRespuesta.builder()
                .estado(HttpStatus.CONFLICT.value())
                .error("Conflicto")
                .mensaje(ex.getMessage())
                .ruta(solicitud.getRequestURI())
                .marca(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja excepciones de validación de argumentos.
     * Retorna HTTP 400 con detalles de los campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApiRespuesta> manejarValidacionExcepcion(
            MethodArgumentNotValidException ex, HttpServletRequest solicitud) {

        logger.warn("Error de validación en la solicitud");

        List<ErrorApiRespuesta.ErrorCampo> erroresCampo = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapearErrorCampo)
                .collect(Collectors.toList());

        ErrorApiRespuesta error = ErrorApiRespuesta.builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .error("Error de Validación")
                .mensaje("Error de validación en los datos enviados")
                .ruta(solicitud.getRequestURI())
                .marca(LocalDateTime.now())
                .erroresCampo(erroresCampo)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones de argumento ilegal.
     * Retorna HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorApiRespuesta> manejarArgumentoIlegal(
            IllegalArgumentException ex, HttpServletRequest solicitud) {

        logger.warn("Argumento ilegal: {}", ex.getMessage());

        ErrorApiRespuesta error = ErrorApiRespuesta.builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .error("Solicitud Incorrecta")
                .mensaje(ex.getMessage())
                .ruta(solicitud.getRequestURI())
                .marca(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja todas las demás excepciones no capturadas.
     * Retorna HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApiRespuesta> manejarExcepcionGenerica(
            Exception ex, HttpServletRequest solicitud) {

        logger.error("Error interno del servidor: ", ex);

        ErrorApiRespuesta error = ErrorApiRespuesta.builder()
                .estado(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error Interno del Servidor")
                .mensaje("Ha ocurrido un error interno. Por favor, contacte al administrador.")
                .ruta(solicitud.getRequestURI())
                .marca(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Convierte un FieldError de Spring a nuestro DTO de error.
     */
    private ErrorApiRespuesta.ErrorCampo mapearErrorCampo(FieldError errorCampo) {
        return ErrorApiRespuesta.ErrorCampo.builder()
                .campo(errorCampo.getField())
                .mensaje(errorCampo.getDefaultMessage())
                .valorRechazado(errorCampo.getRejectedValue())
                .build();
    }
}
