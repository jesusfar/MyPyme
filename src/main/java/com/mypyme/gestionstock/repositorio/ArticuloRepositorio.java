package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.Articulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones de la entidad Articulo.
 */
@Repository
public interface ArticuloRepositorio extends JpaRepository<Articulo, Long> {

    /**
     * Busca artículos por ID de categoría.
     */
    List<Articulo> findByCategoriaId(Long categoriaId);

    /**
     * Busca artículos por ID de categoría con paginación.
     */
    Page<Articulo> findByCategoriaId(Long categoriaId, Pageable pageable);

    /**
     * Busca artículos con stock por debajo del nivel mínimo.
     */
    @Query("SELECT a FROM Articulo a WHERE a.cantidadStock < a.stockMinimo")
    List<Articulo> buscarArticulosStockBajo();

    /**
     * Busca artículos agotados.
     */
    @Query("SELECT a FROM Articulo a WHERE a.cantidadStock <= 0")
    List<Articulo> buscarArticulosAgotados();

    /**
     * Busca artículos por nombre (insensible a mayúsculas).
     */
    List<Articulo> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca artículos por nombre o descripción.
     */
    @Query("SELECT a FROM Articulo a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Articulo> buscarPorNombreODescripcion(@Param("busqueda") String busqueda);

    /**
     * Cuenta artículos por categoría.
     */
    long countByCategoriaId(Long categoriaId);

    /**
     * Verifica si existe un artículo con el nombre dado.
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si existe un artículo con el nombre dado excluyendo un id
     * específico.
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Cuenta artículos con stock bajo.
     */
    @Query("SELECT COUNT(a) FROM Articulo a WHERE a.cantidadStock < a.stockMinimo")
    long contarArticulosStockBajo();

    /**
     * Cuenta artículos agotados.
     */
    @Query("SELECT COUNT(a) FROM Articulo a WHERE a.cantidadStock <= 0")
    long contarArticulosAgotados();
}
