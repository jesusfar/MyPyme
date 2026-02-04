package com.mypyme.gestionstock.repositorio;

import com.mypyme.gestionstock.entidad.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para operaciones de la entidad Categoria.
 */
@Repository
public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {

    /**
     * Busca categoría por nombre.
     */
    Optional<Categoria> findByNombre(String nombre);

    /**
     * Verifica si existe una categoría con el nombre dado.
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si existe una categoría con el nombre dado excluyendo un id
     * específico.
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);
}
