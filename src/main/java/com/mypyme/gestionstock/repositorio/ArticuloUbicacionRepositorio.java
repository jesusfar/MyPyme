package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.ArticuloUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de la entidad ArticuloUbicacion.
 */
@Repository
public interface ArticuloUbicacionRepositorio extends JpaRepository<ArticuloUbicacion, Long> {

    /**
     * Busca todas las ubicaciones de un artículo.
     */
    List<ArticuloUbicacion> findByArticuloId(Long articuloId);

    /**
     * Busca todos los artículos en una ubicación.
     */
    List<ArticuloUbicacion> findByUbicacionAlmacenId(Long ubicacionId);

    /**
     * Busca asignación específica artículo-ubicación.
     */
    Optional<ArticuloUbicacion> findByArticuloIdAndUbicacionAlmacenId(Long articuloId, Long ubicacionId);

    /**
     * Verifica si existe la asignación.
     */
    boolean existsByArticuloIdAndUbicacionAlmacenId(Long articuloId, Long ubicacionId);

    /**
     * Suma cantidad por artículo en todas las ubicaciones.
     */
    @Query("SELECT COALESCE(SUM(au.cantidad), 0) FROM ArticuloUbicacion au WHERE au.articulo.id = :articuloId")
    int sumarCantidadPorArticulo(@Param("articuloId") Long articuloId);

    /**
     * Suma cantidad por ubicación.
     */
    @Query("SELECT COALESCE(SUM(au.cantidad), 0) FROM ArticuloUbicacion au WHERE au.ubicacionAlmacen.id = :ubicacionId")
    int sumarCantidadPorUbicacion(@Param("ubicacionId") Long ubicacionId);

    /**
     * Cuenta artículos en una ubicación.
     */
    long countByUbicacionAlmacenId(Long ubicacionId);

    /**
     * Busca asignaciones con cantidad mayor a cero.
     */
    List<ArticuloUbicacion> findByArticuloIdAndCantidadGreaterThan(Long articuloId, int cantidad);
}
