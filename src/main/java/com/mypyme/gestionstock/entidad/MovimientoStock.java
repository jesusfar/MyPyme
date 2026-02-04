package com.mypyme.gestionstock.entidad;

import com.mypyme.gestionstock.entidad.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un movimiento de stock (entrada, salida o ajuste).
 * Registra todos los cambios en las cantidades de artículos con trazabilidad
 * completa.
 */
@Entity
@Table(name = "movimientos_stock", indexes = {
        @Index(name = "idx_movimiento_articulo", columnList = "articulo_id"),
        @Index(name = "idx_movimiento_tipo", columnList = "tipo_movimiento"),
        @Index(name = "idx_movimiento_fecha", columnList = "fecha_movimiento")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(length = 500)
    private String motivo;

    @Column(name = "numero_referencia", length = 100)
    private String referencia;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Establece la fecha del movimiento a la actual si no está definida.
     */
    @PrePersist
    public void prePersist() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
        }
    }
}
