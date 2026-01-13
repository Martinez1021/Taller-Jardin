# Taller de Reparación - Gestión de Herramientas de Jardín

Aplicación JavaFX para gestionar el inventario y reparaciones de herramientas de jardín con integración a MongoDB y Odoo.

## 🚀 Ejecutar la Aplicación

```cmd
EJECUTAR.bat
```

## 📋 Requisitos

- Java 17 o superior
- Docker Desktop (para MongoDB y Odoo)

## 🔧 Estructura del Proyecto

```
taller-jardin/
├── src/                    # Código fuente Java
│   ├── main/
│   │   ├── java/          # Clases Java
│   │   └── resources/     # Archivos FXML
│   └── test/              # Tests
├── .mvn/                   # Maven Wrapper
├── docker-compose.yml      # Servicios MongoDB y Odoo
├── pom.xml                # Dependencias Maven
├── EJECUTAR.bat           # Script para ejecutar la app
└── README.md              # Este archivo
```

## 🐳 Servicios Docker

La aplicación requiere MongoDB y Odoo. Los servicios se inician automáticamente con:

```cmd
docker-compose up -d
```

### Puertos

- **MongoDB**: localhost:27017
- **Odoo**: localhost:8069
- **PostgreSQL**: localhost:5432

## 🛠️ Tecnologías

- **JavaFX 21** - Interfaz gráfica
- **MongoDB** - Base de datos
- **Odoo** - ERP
- **Maven** - Gestión de dependencias

---

**Desarrollado para la gestión de taller de herramientas de jardín**
