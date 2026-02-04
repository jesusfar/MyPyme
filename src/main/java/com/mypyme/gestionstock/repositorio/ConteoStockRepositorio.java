package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.ConteoStock;
import com.mypyme.gestionstock.entidad.enums.EstadoConteo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de la entidad ConteoStock.
 * RF03 - Conteo de existencias
 */
@Repository
public interface ConteoStockRepositorio extends JpaRepository<ConteoStock, Long> {

    /**
     * Busca conteo por número de conteo.
     */
    Optional<ConteoStock> findByNumeroConteo(String numeroConteo);

    /**
     * Busca conteos por artículo.
     */
    List<ConteoStock> findByArticuloIdOrderByFechaConteoDesc(Long articuloId);

    /**
     * Busca conteos por usuario responsable.
     */
    List<ConteoStock> findByUsuarioResponsableOrderByFechaConteoDesc(String usuarioResponsable);

    /**
     * Busca conteos por estado.
     */
    List<ConteoStock> findByEstado(EstadoConteo estado);

    /**
     * Busca conteos con paginación.
     */
    Page<ConteoStock> findAllByOrderByFechaConteoDesc(Pageable pageable);

    /**
     * Busca conteos por rango de fechas.
     */
    List<ConteoStock> findByFechaConteoBetweenOrderByFechaConteoDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca conteos con diferencias (diferencia no cero).
     */
    @Query("SELECT c FROM ConteoStock c WHERE c.diferencia != 0 ORDER BY c.fechaConteo DESC")
    List<ConteoStock> buscarConteosConDiferencias();

    /**
     * Busca conteos pendientes.
     */
    List<ConteoStock> findByEstadoOrderByFechaConteoDesc(EstadoConteo estado);

    /**
     * Cuenta por estado.
     */
    long countByEstado(EstadoConteo estado);

    /**
     * Genera el siguiente número de conteo.
     */
    @Query("SELECT MAX(c.numeroConteo) FROM ConteoStock c WHERE c.numeroConteo LIKE :prefijo%")
    String buscarMaxNumeroConteoPorPrefijo(@Param("prefijo") String prefijo);

    /**
     * Busca conteos que no han sido ajustados.
     */
    @Query("SELECT c FROM ConteoStock c WHERE c.diferencia != 0 AND c.estado != 'AJUSTADO' ORDER BY c.fechaConteo DESC")
    List<ConteoStock> buscarConteosNoAjustadosConDiferencias();
}
