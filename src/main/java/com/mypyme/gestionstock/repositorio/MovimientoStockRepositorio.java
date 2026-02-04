package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.MovimientoStock;
import com.mypyme.gestionstock.entidad.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones de la entidad MovimientoStock.
 */
@Repository
public interface MovimientoStockRepositorio extends JpaRepository<MovimientoStock, Long> {

    /**
     * Busca movimientos por ID de artículo ordenados por fecha descendente.
     */
    List<MovimientoStock> findByArticuloIdOrderByFechaMovimientoDesc(Long articuloId);

    /**
     * Busca movimientos por ID de artículo con paginación.
     */
    Page<MovimientoStock> findByArticuloId(Long articuloId, Pageable pageable);

    /**
     * Busca movimientos por tipo.
     */
    List<MovimientoStock> findByTipoMovimiento(TipoMovimiento tipoMovimiento);

    /**
     * Busca movimientos en un rango de fechas.
     */
    List<MovimientoStock> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca movimientos por artículo y rango de fechas.
     */
    List<MovimientoStock> findByArticuloIdAndFechaMovimientoBetweenOrderByFechaMovimientoDesc(
            Long articuloId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Cuenta movimientos por tipo en un rango de fechas.
     */
    @Query("SELECT COUNT(m) FROM MovimientoStock m WHERE m.tipoMovimiento = :tipo AND m.fechaMovimiento BETWEEN :inicio AND :fin")
    long contarPorTipoYRangoFecha(
            @Param("tipo") TipoMovimiento tipo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Suma cantidad por tipo en un rango de fechas.
     */
    @Query("SELECT COALESCE(SUM(m.cantidad), 0) FROM MovimientoStock m WHERE m.tipoMovimiento = :tipo AND m.fechaMovimiento BETWEEN :inicio AND :fin")
    long sumarCantidadPorTipoYRangoFecha(
            @Param("tipo") TipoMovimiento tipo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Busca los movimientos más recientes.
     */
    List<MovimientoStock> findTop10ByOrderByFechaMovimientoDesc();

    /**
     * Busca el último movimiento de un artículo.
     */
    @Query("SELECT m FROM MovimientoStock m WHERE m.articulo.id = :articuloId ORDER BY m.fechaMovimiento DESC LIMIT 1")
    MovimientoStock buscarUltimoMovimientoPorArticulo(@Param("articuloId") Long articuloId);
}
