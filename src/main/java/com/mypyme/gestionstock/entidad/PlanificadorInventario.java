package com.mypyme.gestionstock.entidad;

import com.mypyme.gestionstock.entidad.enums.EstadoPlanificador;
import com.mypyme.gestionstock.entidad.enums.TipoPlanificador;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un planificador de inventario.
 * RF01 - Crear planificador de inventario
 * RF05 - Ejecutar planificador
 * 
 * Permite programar y ejecutar conteos de inventario para artículos.
 */
@Entity
@Table(name = "planificadores_inventario", indexes = {
        @Index(name = "idx_planificador_codigo", columnList = "codigo_plan"),
        @Index(name = "idx_planificador_estado", columnList = "estado"),
        @Index(name = "idx_planificador_programado", columnList = "fecha_programada"),
        @Index(name = "idx_planificador_creado_por", columnList = "creado_por")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanificadorInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_plan", nullable = false, unique = true, length = 50)
    private String codigoPlan;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_planificador", nullable = false, length = 30)
    private TipoPlanificador tipoPlanificador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPlanificador estado = EstadoPlanificador.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private UbicacionAlmacen ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @Column(name = "creado_por", nullable = false, length = 100)
    private String creadoPor;

    @Column(name = "ejecutado_por", length = 100)
    private String ejecutadoPor;

    @Column(name = "total_items", nullable = false)
    @Builder.Default
    private Integer totalItems = 0;

    @Column(name = "items_completados", nullable = false)
    @Builder.Default
    private Integer itemsCompletados = 0;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "planificadorInventario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPlanificador> items = new ArrayList<>();

    /**
     * Agrega un item a este planificador.
     */
    public void agregarItem(ItemPlanificador item) {
        items.add(item);
        item.setPlanificadorInventario(this);
        this.totalItems = items.size();
    }

    /**
     * Calcula el porcentaje de progreso.
     * 
     * @return porcentaje de items completados
     */
    public double getPorcentajeProgreso() {
        if (totalItems == null || totalItems == 0) {
            return 0.0;
        }
        return (itemsCompletados * 100.0) / totalItems;
    }

    /**
     * Verifica si todos los items están completados.
     * 
     * @return true si todos los items están completos
     */
    public boolean estaCompletamenteFinalizado() {
        return totalItems != null && itemsCompletados != null && itemsCompletados.equals(totalItems);
    }

    /**
     * Ejecuta el plan - cambia estado a EN_PROGRESO.
     * 
     * @param ejecutadoPor usuario que ejecuta el plan
     */
    public void ejecutar(String ejecutadoPor) {
        this.estado = EstadoPlanificador.EN_PROGRESO;
        this.ejecutadoPor = ejecutadoPor;
        this.fechaInicio = LocalDateTime.now();
    }

    /**
     * Completa el plan - cambia estado a COMPLETADO.
     */
    public void completar() {
        this.estado = EstadoPlanificador.COMPLETADO;
        this.fechaCompletado = LocalDateTime.now();
    }

    /**
     * Cancela el plan.
     */
    public void cancelar() {
        this.estado = EstadoPlanificador.CANCELADO;
    }

    /**
     * Programa el plan.
     * 
     * @param fechaProgramada fecha para programar
     */
    public void programar(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
        this.estado = EstadoPlanificador.PROGRAMADO;
    }

    /**
     * Incrementa el contador de items completados.
     */
    public void incrementarItemsCompletados() {
        if (this.itemsCompletados == null) {
            this.itemsCompletados = 0;
        }
        this.itemsCompletados++;
    }
}
