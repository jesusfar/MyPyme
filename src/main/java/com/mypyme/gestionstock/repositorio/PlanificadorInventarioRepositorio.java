package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.PlanificadorInventario;
import com.mypyme.gestionstock.entidad.enums.EstadoPlanificador;
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
 * Repositorio para operaciones de la entidad PlanificadorInventario.
 * RF01/RF05 - Planificador de inventario
 */
@Repository
public interface PlanificadorInventarioRepositorio extends JpaRepository<PlanificadorInventario, Long> {

    /**
     * Busca planificador por código de plan.
     */
    Optional<PlanificadorInventario> findByCodigoPlan(String codigoPlan);

    /**
     * Busca planificadores por estado.
     */
    List<PlanificadorInventario> findByEstadoOrderByFechaCreacionDesc(EstadoPlanificador estado);

    /**
     * Busca planificadores con paginación.
     */
    Page<PlanificadorInventario> findAllByOrderByFechaCreacionDesc(Pageable pageable);

    /**
     * Busca planificadores programados en un rango de fechas.
     */
    List<PlanificadorInventario> findByEstadoAndFechaProgramadaBetweenOrderByFechaProgramadaAsc(
            EstadoPlanificador estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca planificadores creados por un usuario.
     */
    List<PlanificadorInventario> findByCreadoPorOrderByFechaCreacionDesc(String creadoPor);

    /**
     * Busca planificadores por categoría.
     */
    List<PlanificadorInventario> findByCategoriaIdOrderByFechaCreacionDesc(Long categoriaId);

    /**
     * Busca planificadores por ubicación.
     */
    List<PlanificadorInventario> findByUbicacionIdOrderByFechaCreacionDesc(Long ubicacionId);

    /**
     * Cuenta planificadores por estado.
     */
    long countByEstado(EstadoPlanificador estado);

    /**
     * Genera el siguiente código de plan.
     */
    @Query("SELECT MAX(p.codigoPlan) FROM PlanificadorInventario p WHERE p.codigoPlan LIKE :prefijo%")
    String buscarMaxCodigoPlanPorPrefijo(@Param("prefijo") String prefijo);

    /**
     * Busca planificadores en progreso.
     */
    List<PlanificadorInventario> findByEstadoInOrderByFechaCreacionDesc(List<EstadoPlanificador> estados);
}
