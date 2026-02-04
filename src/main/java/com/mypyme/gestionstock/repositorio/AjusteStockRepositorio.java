package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.AjusteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de la entidad AjusteStock.
 */
@Repository
public interface AjusteStockRepositorio extends JpaRepository<AjusteStock, Long> {

    /**
     * Busca ajustes por artículo.
     */
    List<AjusteStock> findByArticuloIdOrderByFechaAjusteDesc(Long articuloId);

    /**
     * Busca ajuste por conteo de stock.
     */
    Optional<AjusteStock> findByConteoStockId(Long conteoStockId);

    /**
     * Verifica si existe ajuste para un conteo.
     */
    boolean existsByConteoStockId(Long conteoStockId);

    /**
     * Busca ajustes por rango de fechas.
     */
    List<AjusteStock> findByFechaAjusteBetweenOrderByFechaAjusteDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca ajustes por aprobador.
     */
    List<AjusteStock> findByAprobadoPorOrderByFechaAjusteDesc(String aprobadoPor);
}
