package com.mypyme.gestionstock.entidad;

import com.mypyme.gestionstock.entidad.enums.EstadoConteo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un conteo de stock formal (inventario).
 * Registra el conteo físico de artículos con comparación contra el sistema.
 * RF03 - Conteo de existencias y registro
 */
@Entity
@Table(name = "conteos_stock", indexes = {
        @Index(name = "idx_conteo_numero", columnList = "numero_conteo"),
        @Index(name = "idx_conteo_articulo", columnList = "articulo_id"),
        @Index(name = "idx_conteo_fecha", columnList = "fecha_conteo"),
        @Index(name = "idx_conteo_estado", columnList = "estado"),
        @Index(name = "idx_conteo_responsable", columnList = "usuario_responsable")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conteo", nullable = false, unique = true, length = 50)
    private String numeroConteo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private UbicacionAlmacen ubicacion;

    /**
     * Cantidad del sistema al momento del conteo (capturada automáticamente)
     */
    @Column(name = "cantidad_sistema", nullable = false)
    private Integer cantidadSistema;

    /**
     * Cantidad física contada por el usuario
     */
    @Column(name = "cantidad_contada", nullable = false)
    private Integer cantidadContada;

    /**
     * Diferencia entre contada y sistema (calculada: cantidadContada -
     * cantidadSistema)
     */
    @Column(nullable = false)
    private Integer diferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoConteo estado = EstadoConteo.PENDIENTE;

    /**
     * Usuario responsable de realizar el conteo
     */
    @Column(name = "usuario_responsable", nullable = false, length = 100)
    private String usuarioResponsable;

    @Column(length = 500)
    private String notas;

    @Column(name = "fecha_conteo", nullable = false)
    private LocalDateTime fechaConteo;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToOne(mappedBy = "conteoStock", cascade = CascadeType.ALL)
    private AjusteStock ajuste;

    /**
     * Calcula y establece la diferencia entre cantidades contada y del sistema.
     */
    public void calcularDiferencia() {
        this.diferencia = this.cantidadContada - this.cantidadSistema;
    }

    /**
     * Verifica si hay discrepancia entre cantidad contada y del sistema.
     * 
     * @return true si la diferencia no es cero
     */
    public boolean tieneDiferencia() {
        return diferencia != null && diferencia != 0;
    }

    /**
     * Verifica si se ha aplicado un ajuste.
     * 
     * @return true si existe ajuste
     */
    public boolean tieneAjuste() {
        return ajuste != null;
    }

    @PrePersist
    public void prePersist() {
        if (fechaConteo == null) {
            fechaConteo = LocalDateTime.now();
        }
        if (diferencia == null) {
            calcularDiferencia();
        }
    }
}
