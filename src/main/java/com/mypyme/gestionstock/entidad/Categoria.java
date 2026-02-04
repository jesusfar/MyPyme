package com.mypyme.gestionstock.entidad;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una categoría de productos.
 * Las categorías se utilizan para organizar y agrupar artículos.
 */
@Entity
@Table(name = "categorias", indexes = {
        @Index(name = "idx_categoria_nombre", columnList = "nombre")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Articulo> articulos = new ArrayList<>();

    /**
     * Método auxiliar para agregar un artículo a esta categoría.
     */
    public void agregarArticulo(Articulo articulo) {
        articulos.add(articulo);
        articulo.setCategoria(this);
    }

    /**
     * Método auxiliar para remover un artículo de esta categoría.
     */
    public void removerArticulo(Articulo articulo) {
        articulos.remove(articulo);
        articulo.setCategoria(null);
    }
}
