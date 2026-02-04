package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.UbicacionAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de la entidad UbicacionAlmacen.
 */
@Repository
public interface UbicacionAlmacenRepositorio extends JpaRepository<UbicacionAlmacen, Long> {

    /**
     * Busca ubicación por código.
     */
    Optional<UbicacionAlmacen> findByCodigo(String codigo);

    /**
     * Busca todas las ubicaciones activas.
     */
    List<UbicacionAlmacen> findByActivaTrue();

    /**
     * Busca todas las ubicaciones inactivas.
     */
    List<UbicacionAlmacen> findByActivaFalse();

    /**
     * Busca ubicaciones por zona.
     */
    List<UbicacionAlmacen> findByZona(String zona);

    /**
     * Busca ubicaciones activas por zona.
     */
    List<UbicacionAlmacen> findByZonaAndActivaTrue(String zona);

    /**
     * Verifica si existe una ubicación con el código dado.
     */
    boolean existsByCodigo(String codigo);

    /**
     * Verifica si existe una ubicación con el código dado excluyendo un id
     * específico.
     */
    boolean existsByCodigoAndIdNot(String codigo, Long id);

    /**
     * Obtiene las zonas distintas.
     */
    @Query("SELECT DISTINCT u.zona FROM UbicacionAlmacen u WHERE u.zona IS NOT NULL")
    List<String> buscarZonasDistintas();
}
