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

## 🐳 Ejecución con Docker (Recomendado)

Docker es la forma más sencilla de ejecutar la aplicación, ya que configura automáticamente tanto el servidor (Spring Boot) como la base de datos (MySQL).

### Prerrequisitos
- Tener instalado **Docker Desktop** (Windows/Mac) o Docker Engine (Linux).

### Pasos para ejecutar

1. Abre una terminal en la carpeta del proyecto.
2. Ejecuta el siguiente comando para construir y levantar los contenedores:

```bash
docker-compose up -d --build
```
> **Nota**: La primera vez puede tardar unos minutos en descargar las imágenes y compilar el proyecto.

3. Una vez finalizado, accede a la aplicación:
   - **URL**: http://localhost:8080
   - **Base de Datos**: Puerto 3307 (Usuario: `root`, Password: `root`)

### Comandos Útiles

- **Ver logs en tiempo real**:
  ```bash
  docker-compose logs -f
  ```

- **Detener la aplicación**:
  ```bash
  docker-compose down
  ```

- **Reconstruir si haces cambios en el código**:
  ```bash
  docker-compose up -d --build
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

Jesús Fariña.
