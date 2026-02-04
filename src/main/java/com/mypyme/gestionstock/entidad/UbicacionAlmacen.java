package com.mypyme.gestionstock.entidad;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una ubicación de almacenamiento en el depósito.
 * Las ubicaciones pueden contener múltiples artículos con sus cantidades.
 */
@Entity
@Table(name = "ubicaciones_almacen", indexes = {
        @Index(name = "idx_ubicacion_codigo", columnList = "codigo"),
        @Index(name = "idx_ubicacion_zona", columnList = "zona"),
        @Index(name = "idx_ubicacion_activa", columnList = "activa")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 50)
    private String zona;

    @Column
    private Integer capacidad;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "ubicacionAlmacen", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ArticuloUbicacion> articuloUbicaciones = new ArrayList<>();

    /**
     * Activa esta ubicación de almacenamiento.
     */
    public void activar() {
        this.activa = true;
    }

    /**
     * Desactiva esta ubicación de almacenamiento.
     */
    public void desactivar() {
        this.activa = false;
    }

    /**
     * Obtiene la cantidad total de todos los artículos en esta ubicación.
     * 
     * @return suma de todas las cantidades de artículos
     */
    public int getCantidadTotal() {
        return articuloUbicaciones.stream()
                .mapToInt(ArticuloUbicacion::getCantidad)
                .sum();
    }

    /**
     * Verifica si la ubicación tiene capacidad disponible.
     * 
     * @param cantidadRequerida cantidad a agregar
     * @return true si hay capacidad
     */
    public boolean tieneCapacidad(int cantidadRequerida) {
        if (capacidad == null) {
            return true; // Sin límite de capacidad
        }
        return getCantidadTotal() + cantidadRequerida <= capacidad;
    }
}
