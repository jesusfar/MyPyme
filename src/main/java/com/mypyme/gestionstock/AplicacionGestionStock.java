package com.mypyme.gestionstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Sistema de Gestión de Stock.
 * 
 * Esta aplicación proporciona una API REST para gestionar:
 * - Artículos y Categorías
 * - Movimientos de Stock (entradas/salidas)
 * - Ubicaciones de Almacenamiento
 * - Conteo de Existencias y Ajustes
 * - Planificación de Inventario
 * - Reportes de Stock
 * 
 * @author Equipo de Desarrollo MyPyme
 * @version 1.0.0
 */
@SpringBootApplication
public class AplicacionGestionStock {

    public static void main(String[] args) {
        SpringApplication.run(AplicacionGestionStock.class, args);
    }

}
