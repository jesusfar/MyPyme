package com.mypyme.gestionstock.entidad;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un item dentro de un planificador de inventario.
 * Cada item corresponde a un artículo que necesita ser contado.
 */
@Entity
@Table(name = "items_planificador", indexes = {
        @Index(name = "idx_itemplan_planificador", columnList = "planificador_id"),
        @Index(name = "idx_itemplan_articulo", columnList = "articulo_id"),
        @Index(name = "idx_itemplan_completado", columnList = "completado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPlanificador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planificador_id", nullable = false)
    private PlanificadorInventario planificadorInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private UbicacionAlmacen ubicacion;

    /**
     * Cantidad esperada (cantidad del sistema capturada al ejecutar el plan)
     */
    @Column(name = "cantidad_esperada")
    private Integer cantidadEsperada;

    /**
     * Cantidad contada real (ingresada por el usuario)
     */
    @Column(name = "cantidad_contada")
    private Integer cantidadContada;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    @Column(name = "contado_por", length = 100)
    private String contadoPor;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    /**
     * Marca este item como contado.
     * 
     * @param cantidadContada la cantidad contada
     * @param contadoPor      usuario que realizó el conteo
     */
    public void marcarComoContado(Integer cantidadContada, String contadoPor) {
        this.cantidadContada = cantidadContada;
        this.contadoPor = contadoPor;
        this.completado = true;
        this.fechaCompletado = LocalDateTime.now();
    }

    /**
     * Obtiene la diferencia entre cantidad contada y esperada.
     * 
     * @return diferencia (contada - esperada), o null si ambos valores no están
     *         establecidos
     */
    public Integer getDiferencia() {
        if (cantidadContada == null || cantidadEsperada == null) {
            return null;
        }
        return cantidadContada - cantidadEsperada;
    }

    /**
     * Verifica si hay discrepancia.
     * 
     * @return true si la cantidad contada difiere de la esperada
     */
    public boolean tieneDiferencia() {
        Integer dif = getDiferencia();
        return dif != null && dif != 0;
    }
}
