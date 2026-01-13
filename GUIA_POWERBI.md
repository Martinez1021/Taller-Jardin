# 📊 Guía Completa: Integración PowerBI con MongoDB

## Taller de Jardin - Sistema de Reportes

Esta guía te ayudará a conectar PowerBI con tu base de datos MongoDB para crear reportes y dashboards profesionales.

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Método 1: PowerBI Desktop + MongoDB (Recomendado)](#método-1-powerbi-desktop--mongodb)
3. [Método 2: Exportar a CSV](#método-2-exportar-a-csv)
4. [Método 3: Embeber Reportes en la Aplicación](#método-3-embeber-reportes-en-la-aplicación)
5. [Ejemplos de Reportes](#ejemplos-de-reportes)
6. [Solución de Problemas](#solución-de-problemas)

---

## 🎯 Requisitos Previos

- ✅ PowerBI Desktop instalado ([Descargar aquí](https://powerbi.microsoft.com/desktop/))
- ✅ MongoDB corriendo en Docker (`docker-compose up -d`)
- ✅ Aplicación del taller con datos de prueba
- ⭐ (Opcional) Cuenta PowerBI Pro/Premium para publicar reportes

---

## 🔧 Método 1: PowerBI Desktop + MongoDB (Recomendado)

### Paso 1: Instalar Conector MongoDB para PowerBI

PowerBI Desktop incluye un conector nativo para MongoDB.

### Paso 2: Conectar a MongoDB

1. Abre **PowerBI Desktop**

2. Click en **Obtener datos** (botón superior izquierdo)

3. En la ventana de conectores:
   - Busca "**MongoDB**"
   - Selecciónalo y click **Conectar**

4. Configurar la conexión:
   ```
   Servidor: localhost:27017
   Base de datos: taller_db
   ```

5. Autenticación:
   ```
   Modo: Básico
   Usuario: admin
   Contraseña: admin123
   ```

### Paso 3: Seleccionar Colecciones

Selecciona las colecciones (tablas) que necesitas:

- ☑️ **clientes** - Información de clientes
- ☑️ **facturas** - Facturación
- ☑️ **reservas** - Reservas de maquinaria
- ☑️ **inventario** - Stock de repuestos
- ☑️ **maquinas** - Máquinas registradas

### Paso 4: Transformar Datos (Power Query)

Al cargar, PowerBI abrirá Power Query Editor:

1. **Expandir documentos anidados**:
   - Las colecciones de MongoDB vienen como documentos
   - Click en el icono de expandir (dos flechas) en las columnas con `[Record]`
   - Selecciona los campos que necesitas

2. **Cambiar tipos de datos**:
   - Fechas → Tipo Fecha
   - Números → Tipo Decimal/Entero
   - Textos → Tipo Texto

3. **Eliminar columnas innecesarias**:
   - `_id` interno de MongoDB (opcional)
   - Campos técnicos que no necesites

4. Click **Cerrar y aplicar**

### Paso 5: Crear Visualizaciones

Ahora puedes crear tus reportes con:
- 📊 Gráficos de barras
- 📈 Líneas de tendencia
- 🥧 Gráficos circulares
- 🗺️ Mapas
- 📋 Tablas y matrices
- 🎯 KPIs y tarjetas

---

## 📤 Método 2: Exportar a CSV

### Desde la Aplicación

1. Ve a la sección **"Reportes PowerBI"** en la aplicación

2. Click en **"Exportar Datos"**

3. Selecciona qué colecciones exportar:
   - Clientes
   - Facturas
   - Reservas
   - Inventario
   - Todo

4. Los archivos CSV se guardarán en la ubicación que elijas

### Importar CSV en PowerBI

1. En PowerBI Desktop:
   - **Obtener datos** > **Texto/CSV**
   - Selecciona los archivos exportados

2. PowerBI detectará automáticamente:
   - Encabezados de columna
   - Tipos de datos

3. Click **Cargar**

---

## 🌐 Método 3: Embeber Reportes en la Aplicación

Este método permite ver tus reportes de PowerBI **dentro de la aplicación JavaFX**.

### Requisitos

- ⭐ Cuenta **PowerBI Pro** o **PowerBI Premium**
- 📊 Reporte ya creado y publicado en PowerBI Service

### Paso 1: Publicar Reporte

1. En PowerBI Desktop, click **Publicar**

2. Selecciona tu workspace en PowerBI Service

3. Espera a que se publique

### Paso 2: Obtener URL de Embed

1. Ve a [PowerBI Service](https://app.powerbi.com)

2. Abre tu reporte

3. Click en **Archivo** > **Insertar** > **Sitio web o portal**

4. **Copia la URL** que aparece (algo como):
   ```
   https://app.powerbi.com/reportEmbed?reportId=abc123...
   ```

### Paso 3: Configurar en la Aplicación

1. Abre el archivo:
   ```
   src/main/java/com/taller/controller/ReportesController.java
   ```

2. Busca el método `configurarReportes()`

3. Agrega tu reporte:
   ```java
   reportesUrls.put("Mi Dashboard", "TU_URL_DE_EMBED_AQUI");
   ```

   Ejemplo:
   ```java
   reportesUrls.put("Dashboard General", 
       "https://app.powerbi.com/reportEmbed?reportId=abc123...");
   
   reportesUrls.put("Análisis Financiero", 
       "https://app.powerbi.com/reportEmbed?reportId=def456...");
   ```

4. Recompila la aplicación:
   ```bash
   ./mvnw.cmd clean package -DskipTests
   ```

5. Ejecuta con `EJECUTAR.bat`

6. Ve a **Reportes PowerBI** en el menú

---

## 📊 Ejemplos de Reportes Útiles

### 1. Dashboard General

**Métricas principales:**
- Total de facturas emitidas (mes actual)
- Ingresos totales
- Reservas activas
- Clientes nuevos

**Visualizaciones:**
- KPI Cards para métricas principales
- Gráfico de líneas: Ingresos por mes
- Gráfico de barras: Top 10 clientes
- Tabla: Próximas reservas

### 2. Análisis Financiero

**Métricas:**
- Facturación mensual
- Saldo pendiente
- Facturas vencidas
- Métodos de pago más usados

**Visualizaciones:**
- Gráfico de líneas: Tendencia de facturación
- Gráfico circular: Distribución por método de pago
- Tabla: Facturas pendientes
- Embudo: Estados de facturación

### 3. Gestión de Inventario

**Métricas:**
- Stock actual vs mínimo
- Valor del inventario
- Productos más vendidos
- Alertas de stock bajo

**Visualizaciones:**
- Gráfico de barras: Stock por categoría
- Gauge: Nivel de stock
- Tabla: Productos bajo mínimo
- Treemap: Valor por categoría

### 4. Análisis de Reservas

**Métricas:**
- Tasa de ocupación de maquinaria
- Ingresos por alquiler
- Reservas por cliente
- Máquinas más solicitadas

**Visualizaciones:**
- Calendario de reservas
- Gráfico de barras: Máquinas más rentables
- Mapa de calor: Días con más reservas
- Tabla: Próximas entregas

---

## 🔧 Configuración de Conexión MongoDB

### Datos de Conexión

```
Host: localhost
Puerto: 27017
Base de datos: taller_db
Autenticación: SI

Usuario: admin
Contraseña: admin123
Base de autenticación: admin
```

### Verificar que MongoDB esté corriendo

```bash
docker ps | findstr mongo
```

Deberías ver algo como:
```
taller-jardin-mongodb-1   mongo:7   Up X minutes   0.0.0.0:27017->27017/tcp
```

### Probar conexión manualmente

```bash
docker exec -it taller-jardin-mongodb-1 mongosh taller_db -u admin -p admin123 --authenticationDatabase admin
```

---

## ❗ Solución de Problemas

### No puedo conectar PowerBI a MongoDB

**Problema**: "No se puede establecer conexión"

**Soluciones**:
1. Verifica que MongoDB esté corriendo
2. Verifica el puerto 27017 esté accesible
3. Comprueba usuario y contraseña
4. Intenta desde MongoDB Compass primero

### Los datos se ven raros en PowerBI

**Problema**: Fechas o números no se muestran correctamente

**Solución**:
1. En Power Query Editor
2. Selecciona la columna
3. Click derecho > Cambiar tipo
4. Elige el tipo correcto (Fecha, Número, Texto)

### El reporte embebido no se ve en la aplicación

**Problema**: Pantalla en blanco o error

**Soluciones**:
1. Verifica que la URL de embed sea correcta
2. Comprueba que tengas cuenta PowerBI Pro
3. Asegúrate que el reporte esté publicado y sea público
4. Revisa la consola Java para errores

### Error de autenticación al embeber

**Problema**: PowerBI pide login constantemente

**Solución**:
- Usa la URL con `?autoAuth=true` al final
- O configura el reporte como "público" en PowerBI Service

---

## 📚 Recursos Adicionales

- [Documentación oficial PowerBI](https://docs.microsoft.com/power-bi/)
- [Conector MongoDB para PowerBI](https://learn.microsoft.com/power-bi/connect-data/desktop-connect-mongodb)
- [MongoDB Atlas + PowerBI](https://www.mongodb.com/docs/atlas/bi-connector/)

---

## 💡 Consejos Pro

1. **Actualización automática**: Configura "Actualización programada" en PowerBI Service

2. **Rendimiento**: Usa DirectQuery solo si necesitas datos en tiempo real, sino usa Import

3. **Seguridad**: No compartas URLs de embed públicamente, contienen tokens de acceso

4. **Filtros**: Usa slicers (segmentadores) para permitir filtrado interactivo

5. **Temas**: Crea un tema personalizado con los colores de tu taller

---

## 🎓 Próximos Pasos

1. ✅ Conectar PowerBI Desktop a MongoDB
2. ✅ Crear tu primer reporte (Dashboard General)
3. ✅ Publicar en PowerBI Service (si tienes cuenta Pro)
4. ✅ Embeber en la aplicación (opcional)
5. ✅ Configurar actualización automática

---

**¿Necesitas ayuda?** Revisa la sección "Ver Guía" dentro de la aplicación en Reportes PowerBI.
