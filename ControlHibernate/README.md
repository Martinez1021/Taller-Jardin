# Control de Presencia - Sistema de Fichaje

Sistema de gestión de fichajes y control de presencia empresarial desarrollado con JavaFX y Hibernate.

## 📋 Descripción

Aplicación de escritorio para el control de asistencia de trabajadores que permite registrar entradas/salidas, gestionar trabajadores, visualizar estadísticas y generar reportes.

## 🚀 Características

- **Login dual**: Acceso para administradores y trabajadores
- **Panel de administración**: Dashboard con estadísticas en tiempo real
- **Gestión de trabajadores**: CRUD completo de empleados
- **Gestión de fichajes**: Visualización y filtrado de registros
- **Panel de trabajador**: Interfaz para fichar entrada/salida
- **Estadísticas**: Gráficos y reportes de asistencia
- **Base de datos MySQL**: Persistencia con Hibernate

## 🛠️ Tecnologías

- **Java 17**
- **JavaFX 21.0.1**
- **Hibernate 6.4.1**
- **MySQL 8.0**
- **Maven**
- **Docker** (opcional para BD)

## 📦 Requisitos

- JDK 17 o superior
- Maven 3.6+
- MySQL 8.0 o Docker Desktop
- IntelliJ IDEA (recomendado)

## ⚙️ Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/martinez1021/ControlPresencia.git
cd ControlPresencia
```

### 2. Configurar la base de datos

#### Opción A: Usar Docker (Recomendado)

```bash
# Iniciar contenedor MySQL
start-db.bat

# O manualmente:
docker-compose up -d
```

#### Opción B: MySQL local

1. Crear la base de datos:
```sql
CREATE DATABASE control_presencia;
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'AppPass123!';
GRANT ALL PRIVILEGES ON control_presencia.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
```

2. Ejecutar el script de inicialización:
```bash
mysql -u appuser -p control_presencia < init.sql
```

### 3. Configurar Hibernate (opcional)

Si usas MySQL local, edita `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/control_presencia</property>
<property name="hibernate.connection.username">appuser</property>
<property name="hibernate.connection.password">AppPass123!</property>
```

### 4. Compilar y ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar desde IntelliJ
# Run -> Run 'Launcher'

# O desde línea de comandos
mvn javafx:run
```

## 👤 Usuarios de prueba

### Administrador
- **Usuario**: `admin`
- **Contraseña**: `admin123`

### Trabajadores
- **Tarjeta**: `1001` - `1010`
- **PIN**: `1234`

Ejemplo:
- Tarjeta: `1001` (Juan García López)
- PIN: `1234`

## 📂 Estructura del proyecto

```
ControlPresencia/
├── src/
│   ├── main/
│   │   ├── java/com/empresa/fichador/
│   │   │   ├── controller/      # Controladores JavaFX
│   │   │   ├── dao/              # Data Access Objects
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── service/          # Lógica de negocio
│   │   │   ├── util/             # Utilidades
│   │   │   ├── Launcher.java    # Punto de entrada
│   │   │   └── MainApp.java     # Aplicación JavaFX
│   │   └── resources/
│   │       ├── view/             # Archivos FXML
│   │       ├── styles.css        # Estilos CSS
│   │       └── hibernate.cfg.xml # Configuración Hibernate
│   └── test/                     # Tests
├── docker-compose.yml            # Configuración Docker
├── init.sql                      # Script inicial BD
├── start-db.bat                  # Iniciar BD
├── stop-db.bat                   # Detener BD
├── pom.xml                       # Configuración Maven
└── README.md
```

## 🎯 Funcionalidades principales

### Panel de Administración
- Dashboard con métricas en tiempo real
- Gestión completa de trabajadores
- Visualización de fichajes con filtros
- Estadísticas y gráficos
- Exportación de datos

### Panel de Trabajador
- Reloj digital en tiempo real
- Fichaje de entrada/salida con un clic
- Historial de fichajes del día
- Resumen semanal de horas trabajadas
- Indicador de estado actual

## 🗄️ Base de datos

### Tablas principales
- `trabajadores`: Información de empleados
- `fichajes`: Registros de entrada/salida
- `departamentos`: Departamentos de la empresa
- `horarios`: Horarios laborales
- `usuarios`: Usuarios del sistema
- `incidencias`: Registro de incidencias

### Datos iniciales
Al iniciar la aplicación por primera vez con la BD vacía, se crean automáticamente:
- 10 trabajadores de ejemplo
- 3 departamentos (IT, RRHH, Admin)
- 2 horarios predefinidos
- Usuario administrador

## 🔧 Configuración

### Cambiar credenciales de BD

Edita `docker-compose.yml` y `hibernate.cfg.xml`:

```yaml
# docker-compose.yml
environment:
  MYSQL_ROOT_PASSWORD: TuNuevaPassword
  MYSQL_DATABASE: control_presencia
  MYSQL_USER: tuusuario
  MYSQL_PASSWORD: tupassword
```

```xml
<!-- hibernate.cfg.xml -->
<property name="hibernate.connection.username">tuusuario</property>
<property name="hibernate.connection.password">tupassword</property>
```

## 🐛 Solución de problemas

### Error de conexión a BD
```
Error: No se puede conectar a la base de datos
```
**Solución**: Verifica que MySQL/Docker esté ejecutándose y las credenciales sean correctas.

### Ventana no se muestra correctamente
**Solución**: Asegúrate de tener Java 17+ y JavaFX correctamente configurado.

### Puerto 3306 ya en uso
```
Error: bind: address already in use
```
**Solución**: Cambia el puerto en `docker-compose.yml` o detén el MySQL local.

## 📝 Notas de desarrollo

- La aplicación funciona en modo demo sin conexión a BD
- Los fichajes se registran con fecha y hora actual del sistema
- Las estadísticas se actualizan automáticamente cada 30 segundos
- Resolución recomendada: 1920x1080

## 👨‍💻 Autor

**Tu Nombre**
- Proyecto de 2º DAM
- Año 2025

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la Licencia MIT.

## 🙏 Agradecimientos

- Profesor/a y compañeros de DAM
- Documentación de JavaFX y Hibernate
- Comunidad de Stack Overflow

---

**¿Necesitas ayuda?** Abre un issue en GitHub o contacta conmigo.

