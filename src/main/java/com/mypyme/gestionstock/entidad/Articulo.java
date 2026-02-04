package com.mypyme.gestionstock.entidad;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un artículo (producto) en el inventario.
 * Los artículos son los elementos principales controlados en el sistema de
 * gestión de stock.
 */
@Entity
@Table(name = "articulos", indexes = {
        @Index(name = "idx_articulo_nombre", columnList = "nombre"),
        @Index(name = "idx_articulo_categoria", columnList = "categoria_id"),
        @Index(name = "idx_articulo_stock", columnList = "cantidad_stock")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "cantidad_stock", nullable = false)
    @Builder.Default
    private Integer cantidadStock = 0;

    @Column(name = "stock_minimo", nullable = false)
    @Builder.Default
    private Integer stockMinimo = 0;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MovimientoStock> movimientos = new ArrayList<>();

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ArticuloUbicacion> articuloUbicaciones = new ArrayList<>();

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConteoStock> conteosStock = new ArrayList<>();

    /**
     * Verifica si el stock está por debajo del nivel mínimo.
     * 
     * @return true si el stock está bajo el mínimo
     */
    public boolean tieneStockBajo() {
        return cantidadStock < stockMinimo;
    }

    /**
     * Verifica si el artículo está agotado.
     * 
     * @return true si el stock es cero o negativo
     */
    public boolean estaAgotado() {
        return cantidadStock <= 0;
    }

    /**
     * Calcula el valor total del artículo en stock.
     * 
     * @return valor total (precioUnitario * cantidadStock)
     */
    public BigDecimal getValorTotal() {
        if (precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidadStock));
    }

    /**
     * Incrementa la cantidad de stock.
     * 
     * @param cantidad cantidad a agregar
     */
    public void incrementarStock(int cantidad) {
        this.cantidadStock += cantidad;
    }

    /**
     * Decrementa la cantidad de stock.
     * 
     * @param cantidad cantidad a restar
     * @throws IllegalArgumentException si el stock resultante sería negativo
     */
    public void decrementarStock(int cantidad) {
        if (this.cantidadStock - cantidad < 0) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Actual: " + cantidadStock + ", Solicitado: " + cantidad);
        }
        this.cantidadStock -= cantidad;
    }
}
