# 🛠️ Taller-Jardin | Sistema de Gestión Integral

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-4285F4?style=for-the-badge&logo=openjdk&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Odoo](https://img.shields.io/badge/Odoo-17-714B67?style=for-the-badge&logo=odoo&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**Taller-Jardin** es una solución de software empresarial diseñada para optimizar la gestión de talleres de reparación de maquinaria de jardinería. Combina una interfaz de escritorio robusta y moderna (JavaFX) con la flexibilidad de bases de datos NoSQL (MongoDB) y la potencia de un ERP líder en la industria (Odoo).

---

## 🚀 Características Principales

El sistema está dividido en módulos funcionales para cubrir todas las áreas del negocio:

| Módulo | Descripción |
| :--- | :--- |
| **📊 Dashboard** | Vista general del estado del taller, métricas clave y accesos rápidos. |
| **👥 Clientes** | Gestión completa de la cartera de clientes (CRM básico). |
| **🔧 Órdenes de Reparación** | Ciclo de vida completo de las reparaciones: recepción, diagnóstico, presupuesto y entrega. |
| **📦 Inventario** | Control de stock de repuestos y herramientas sincronizado en tiempo real. |
| **💰 Facturación** | Generación de facturas y gestión de cobros integrada con Odoo. |
| **📅 Reservas** | Sistema de citas previas para mantenimientos y reparaciones. |
| **🛡️ Garantías** | Gestión de garantías de productos y servicios realizados. |
| **📈 Analíticas** | Reportes avanzados e integración con **PowerBI** para toma de decisiones. |
| **🔒 Seguridad** | Control de acceso basado en roles y autenticación de usuarios. |

---

## 🏗️ Arquitectura Tecnológica

El proyecto sigue una arquitectura modular en tres capas principales:

1.  **Frontend (Cliente)**: Desarrollado en **Java 17** con **JavaFX 21**. Utiliza FXML para la definición de interfaces y CSS para estilos modernos.
2.  **Backend (Lógica & Datos)**:
    *   **MongoDB**: Almacenamiento principal de datos operativos del taller (documentos flexibles).
    *   **Odoo ERP**: Backend para gestión contable y empresarial, conectado vía XML-RPC.
    *   **PostgreSQL**: Base de datos relacional subyacente de Odoo.
3.  **Infraestructura**: Despliegue de servicios contenerizados mediante **Docker Compose**.

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

*   [Java JDK 17](https://www.oracle.com/java/technologies/downloads/) o superior.
*   [Docker Desktop](https://www.docker.com/products/docker-desktop) (para levantar las bases de datos).
*   [Maven](https://maven.apache.org/) (opcional, el proyecto incluye `mvnw`).

---

## 🛠️ Instalación y Puesta en Marcha

### 1. Clonar el Repositorio
```bash
git clone https://github.com/Martinez1021/Taller-Jardin.git
cd Taller-Jardin
```

### 2. Iniciar Servicios (Base de Datos)
El sistema requiere que MongoDB y Odoo estén activos. Usa Docker para iniciarlos automáticamente:

```bash
docker-compose up -d
```
> *Esto levantará MongoDB en el puerto **27017**, Odoo en el **8069** y Postgres en el **5432**.*

### 3. Compilar el Proyecto
Para descargar las dependencias y compilar el código fuente:

```bash
./mvnw clean install
# O en Windows:
RECOMPILAR.bat
```

### 4. Ejecutar la Aplicación
Una vez compilado, puedes lanzar la aplicación con el script incluido:

```bash
# Windows
EJECUTAR.bat
```

---

## 📂 Estructura del Proyecto

```plaintext
taller-jardin/
├── src/
│   └── main/
│       ├── java/com/taller/
│       │   ├── controller/   # Controladores de las vistas (MVC)
│       │   ├── model/        # Modelos de datos y mapeo
│       │   ├── service/      # Lógica de negocio y servicios
│       │   └── Main.java     # Punto de entrada
│       └── resources/
│           ├── fxml/         # Archivos de interfaz gráfica
│           └── style.css     # Estilos de la aplicación
├── docker-compose.yml        # Definición de servicios Docker
├── pom.xml                   # Configuración de Maven
├── GUIA_POWERBI.md           # Guía para integración con BI
└── README.md                 # Documentación del proyecto
```

---

## 📊 Integración con PowerBI

Este proyecto incluye capacidades analíticas avanzadas. Para conectar PowerBI a los datos generados por la aplicación, consulta la guía dedicada:

👉 [Ver Guía de integración PowerBI + MongoDB](GUIA_POWERBI.md)

---

## 👤 Autor

**Desarrollado por Martinez1021**

Proyecto realizado como parte de las Prácticas de Acceso a Datos y Desarrollo de Interfaces.
