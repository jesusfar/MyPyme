# MyPyme - Sistema de Gestión de Stock

Sistema completo de gestión de inventario desarrollado con **Spring Boot** y una interfaz premium estilo **Vercel**.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-green)
![License](https://img.shields.io/badge/License-MIT-blue)

## 🚀 Características

### Backend
- ✅ API REST completa con Spring Boot
- ✅ Base de datos H2 (desarrollo) / MySQL (producción)
- ✅ Documentación OpenAPI (Swagger)
- ✅ Manejo de excepciones centralizado
- ✅ Validación de datos con Jakarta Validation

### Frontend "Factory Edition"
- ✅ Interfaz premium industrial
- ✅ Logo isométrico de fábrica
- ✅ Tema oscuro con gradientes
- ✅ Animaciones Glassmorphism
- ✅ Notificaciones toast

## 📋 Funcionalidades

| Módulo | Descripción |
|--------|-------------|
| **Artículos** | Gestión completa de productos con categorías |
| **Categorías** | Organización de inventario |
| **Ubicaciones** | Control de almacenes y zonas |
| **Movimientos** | Registro de entradas y salidas de stock |
| **Conteos** | Sistema de conteo de existencias |
| **Planificadores** | Planificación de inventarios |
| **Reportes** | Reportes de stock y alertas |

## 🛠️ Requisitos

- Java 17+
- Maven 3.8+

## 🚀 Instalación

```bash
# Clonar repositorio
git clone https://github.com/jesusfar/MyPyme.git
cd MyPyme

# Ejecutar con Maven Wrapper
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📡 Endpoints API

| Recurso | URL Base |
|---------|----------|
| Artículos | `/api/articulos` |
| Categorías | `/api/categorias` |
| Ubicaciones | `/api/ubicaciones` |
| Movimientos | `/api/movimientos` |
| Conteos | `/api/conteos` |
| Planificadores | `/api/planificadores` |
| Reportes | `/api/reportes` |
| Swagger UI | `/swagger-ui.html` |

## 📸 Screenshots

### Dashboard
La interfaz principal muestra estadísticas en tiempo real del inventario con un diseño moderno y elegante.

## 📦 Estructura del Proyecto

```
src/main/java/com/mypyme/gestionstock/
├── controlador/     # Controladores REST
├── dto/             # Data Transfer Objects
├── entidad/         # Entidades JPA
├── excepcion/       # Excepciones personalizadas
├── repositorio/     # Repositorios Spring Data
└── servicio/        # Lógica de negocio

src/main/resources/
├── static/          # Frontend (HTML, CSS, JS)
└── application.yml  # Configuración
```

## 🔧 Configuración

El proyecto usa perfiles de Spring Boot:
- **dev**: Base de datos H2 en memoria
- **prod**: MySQL (configurar en application-prod.yml)

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.

## 👨‍💻 Autor

Desarrollado como sistema de gestión de inventario para PyMEs.
