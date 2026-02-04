package com.mypyme.gestionstock.entidad;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa la asignación de un artículo a una ubicación.
 * Esta es una tabla de unión que también registra la cantidad en cada
 * ubicación.
 */
@Entity
@Table(name = "articulo_ubicaciones", uniqueConstraints = @UniqueConstraint(name = "uk_articulo_ubicacion", columnNames = {
        "articulo_id", "ubicacion_id" }), indexes = {
                @Index(name = "idx_artubic_articulo", columnList = "articulo_id"),
                @Index(name = "idx_artubic_ubicacion", columnList = "ubicacion_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private UbicacionAlmacen ubicacionAlmacen;

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 0;

    @CreationTimestamp
    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Incrementa la cantidad en esta ubicación.
     * 
     * @param monto cantidad a agregar
     */
    public void incrementarCantidad(int monto) {
        this.cantidad += monto;
    }

    /**
     * Decrementa la cantidad en esta ubicación.
     * 
     * @param monto cantidad a restar
     * @throws IllegalArgumentException si el monto excede la cantidad actual
     */
    public void decrementarCantidad(int monto) {
        if (this.cantidad - monto < 0) {
            throw new IllegalArgumentException(
                    "Cantidad insuficiente en ubicación. Actual: " + cantidad + ", Solicitado: " + monto);
        }
        this.cantidad -= monto;
    }
}
