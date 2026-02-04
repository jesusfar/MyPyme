package com.mypyme.gestionstock.entidad;

import com.mypyme.gestionstock.entidad.enums.TipoAjuste;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un ajuste de stock derivado de un conteo.
 * Vincula al conteo original y registra la corrección aplicada al inventario.
 */
@Entity
@Table(name = "ajustes_stock", indexes = {
        @Index(name = "idx_ajuste_articulo", columnList = "articulo_id"),
        @Index(name = "idx_ajuste_fecha", columnList = "fecha_ajuste"),
        @Index(name = "idx_ajuste_aprobado", columnList = "aprobado_por")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conteo_stock_id", nullable = false, unique = true)
    private ConteoStock conteoStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ajuste", nullable = false, length = 20)
    private TipoAjuste tipoAjuste;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(length = 500)
    private String motivo;

    @Column(name = "aprobado_por", length = 100)
    private String aprobadoPor;

    @Column(name = "fecha_ajuste", nullable = false)
    private LocalDateTime fechaAjuste;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (fechaAjuste == null) {
            fechaAjuste = LocalDateTime.now();
        }
    }

    /**
     * Determina el tipo de ajuste basado en el cambio de cantidad.
     * 
     * @param diferencia la diferencia del conteo (contada - sistema)
     */
    public void determinarTipoAjuste(int diferencia) {
        if (diferencia > 0) {
            this.tipoAjuste = TipoAjuste.INCREMENTO;
            this.cantidad = diferencia;
        } else {
            this.tipoAjuste = TipoAjuste.DECREMENTO;
            this.cantidad = Math.abs(diferencia);
        }
    }
}
