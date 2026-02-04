package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.ItemPlanificador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones de la entidad ItemPlanificador.
 */
@Repository
public interface ItemPlanificadorRepositorio extends JpaRepository<ItemPlanificador, Long> {

    /**
     * Busca items por planificador.
     */
    List<ItemPlanificador> findByPlanificadorInventarioId(Long planificadorId);

    /**
     * Busca items por artículo.
     */
    List<ItemPlanificador> findByArticuloId(Long articuloId);

    /**
     * Busca items no completados de un planificador.
     */
    List<ItemPlanificador> findByPlanificadorInventarioIdAndCompletadoFalse(Long planificadorId);

    /**
     * Busca items completados de un planificador.
     */
    List<ItemPlanificador> findByPlanificadorInventarioIdAndCompletadoTrue(Long planificadorId);

    /**
     * Cuenta items completados de un planificador.
     */
    long countByPlanificadorInventarioIdAndCompletadoTrue(Long planificadorId);

    /**
     * Cuenta total de items de un planificador.
     */
    long countByPlanificadorInventarioId(Long planificadorId);

    /**
     * Busca items con diferencias.
     */
    @Query("SELECT i FROM ItemPlanificador i WHERE i.planificadorInventario.id = :planificadorId AND i.completado = true AND i.cantidadContada != i.cantidadEsperada")
    List<ItemPlanificador> buscarItemsConDiferencias(@Param("planificadorId") Long planificadorId);

    /**
     * Elimina items por planificador.
     */
    void deleteByPlanificadorInventarioId(Long planificadorId);
}
