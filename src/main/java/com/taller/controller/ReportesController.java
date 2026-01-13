package com.taller.controller;

import com.taller.service.*;
import com.taller.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ReportesController {
    
    @FXML private WebView webView;
    @FXML private VBox panelAyuda;
    @FXML private ComboBox<String> cmbReportes;
    @FXML private Label lblEstado;
    @FXML private Label lblConexion;
    
    private WebEngine webEngine;
    private Map<String, String> reportesUrls;
    
    // Servicios para exportación
    private final ClienteService clienteService = new ClienteService();
    private final FacturaService facturaService = new FacturaService();
    private final ReservaService reservaService = new ReservaService();
    private final InventarioService inventarioService = new InventarioService();

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();
        
        // Habilitar JavaScript
        webEngine.setJavaScriptEnabled(true);
        
        configurarReportes();
        verificarConfiguracion();
    }

    private void configurarReportes() {
        reportesUrls = new HashMap<>();
        
        // ========== TUS REPORTES PERSONALIZADOS ==========
        // Para agregar tus reportes de PowerBI:
        // 
        // OPCIÓN 1: PowerBI Service (Online - Requiere cuenta Pro)
        // 1. Crea reporte en PowerBI Desktop conectado a MongoDB:
        //    - Servidor: localhost:27017
        //    - Base de datos: taller_db
        //    - Usuario: admin / Contraseña: admin123
        // 2. Publica en PowerBI Service
        // 3. Obtén URL de embed (Compartir > Insertar > Publicar en web)
        // 4. Agrega aquí:
        //    reportesUrls.put("Mi Dashboard", "https://app.powerbi.com/view?r=...");
        //
        // OPCIÓN 2: PowerBI Report Server (Local - No requiere cuenta)
        // 1. Instala PowerBI Report Server
        // 2. Publica tus reportes localmente
        // 3. Agrega URL local:
        //    reportesUrls.put("Dashboard Local", "http://localhost:80/Reports/...");
        
        // Ejemplo (descomenta cuando tengas tus URLs):
        reportesUrls.put("📊 Demo Dashboard (Microsoft Sample)", "https://app.powerbi.com/view?r=eyJrIjoiY2I1NGRlMTItNzY3Yy00MTQ2LWE1N2YtMjQ2YTdmMmNhMmYyIiwidCI6IjI3ZmNjZGNmLWM4ZDgtNDRkMi1iNTliLTk5YTM3OWI2NTdiMCJ9");
        // reportesUrls.put("💰 Análisis Financiero", "TU_URL_AQUI");
        // reportesUrls.put("📦 Control Inventario", "TU_URL_AQUI");
        
        // Cargar opciones en el ComboBox
        cmbReportes.setItems(FXCollections.observableArrayList(reportesUrls.keySet()));
        
        if (!reportesUrls.isEmpty()) {
            cmbReportes.getSelectionModel().selectFirst();
        }
    }

    private void verificarConfiguracion() {
        // Si no hay reportes configurados, mostrar panel de ayuda
        if (reportesUrls.isEmpty()) {
            mostrarPanelAyuda(true);
            lblEstado.setText("Estado: PowerBI no configurado");
            lblConexion.setText("💡 Usa la sección 'Analíticas' para ver gráficos sin PowerBI");
        } else {
            // Cargar el primer reporte
            mostrarPanelAyuda(false);
            cargarReporte(cmbReportes.getSelectionModel().getSelectedItem());
            lblEstado.setText("Estado: Reporte cargado");
        }
    }

    @FXML
    private void handleCambiarReporte() {
        String reporteSeleccionado = cmbReportes.getSelectionModel().getSelectedItem();
        if (reporteSeleccionado != null) {
            cargarReporte(reporteSeleccionado);
        }
    }

    private void cargarReporte(String nombreReporte) {
        if (nombreReporte == null || nombreReporte.isEmpty()) {
            return;
        }
        
        String url = reportesUrls.get(nombreReporte);
        if (url != null && !url.isEmpty()) {
            lblEstado.setText("Estado: Cargando " + nombreReporte + "...");
            
            try {
                webEngine.load(url);
                mostrarPanelAyuda(false);
                
                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        lblEstado.setText("Estado: " + nombreReporte + " cargado correctamente");
                    } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                        lblEstado.setText("Estado: Error al cargar el reporte");
                        mostrarError("No se pudo cargar el reporte. Verifica la URL y tu conexión.");
                    }
                });
                
            } catch (Exception e) {
                lblEstado.setText("Estado: Error");
                mostrarError("Error al cargar el reporte: " + e.getMessage());
            }
        } else {
            mostrarError("URL del reporte no configurada");
        }
    }

    @FXML
    private void handleRecargar() {
        String reporteSeleccionado = cmbReportes.getSelectionModel().getSelectedItem();
        if (reporteSeleccionado != null) {
            cargarReporte(reporteSeleccionado);
        } else {
            webEngine.reload();
        }
    }

    @FXML
    private void handleAbrirPowerBI() {
        // Ruta por defecto: busca en la carpeta del proyecto
        File powerBIFile = new File("Dashboard Taller Jardin.pbix");
        
        // Si no encuentra el archivo en la carpeta del proyecto, busca en C:\taller-jardin
        if (!powerBIFile.exists()) {
            powerBIFile = new File("C:\\taller-jardin\\Dashboard.pbix");
        }
        
        if (!powerBIFile.exists()) {
            // Si no existe en la ruta por defecto, abrir selector de archivos
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo Power BI");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Power BI Files", "*.pbix")
            );
            
            powerBIFile = fileChooser.showOpenDialog(webView.getScene().getWindow());
            
            if (powerBIFile == null) {
                mostrarInfo("💡 Guarda tu archivo Power BI como 'Dashboard Taller Jardin.pbix' en la carpeta del proyecto\n" +
                           "o en C:\\taller-jardin\\Dashboard.pbix para que se abra automáticamente.");
                return;
            }
        }
        
        try {
            // Abrir el archivo con la aplicación predeterminada (Power BI Desktop)
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Runtime.getRuntime().exec("cmd /c start \"\" \"" + powerBIFile.getAbsolutePath() + "\"");
            } else {
                java.awt.Desktop.getDesktop().open(powerBIFile);
            }
            lblEstado.setText("Estado: Abriendo Power BI Desktop...");
        } catch (Exception e) {
            mostrarError("Error al abrir Power BI:\n" + e.getMessage() + 
                        "\n\nAsegúrate de tener Power BI Desktop instalado.");
        }
    }

    @FXML
    private void handleExportarDatos() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exportar Datos para PowerBI");
        alert.setHeaderText("¿Qué datos deseas exportar?");
        alert.setContentText("Selecciona el tipo de datos a exportar:");
        
        ButtonType btnClientes = new ButtonType("Clientes");
        ButtonType btnFacturas = new ButtonType("Facturas");
        ButtonType btnReservas = new ButtonType("Reservas");
        ButtonType btnInventario = new ButtonType("Inventario");
        ButtonType btnTodo = new ButtonType("Todo");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnClientes, btnFacturas, btnReservas, btnInventario, btnTodo, btnCancelar);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == btnClientes) {
                exportarColeccion("clientes");
            } else if (response == btnFacturas) {
                exportarColeccion("facturas");
            } else if (response == btnReservas) {
                exportarColeccion("reservas");
            } else if (response == btnInventario) {
                exportarColeccion("inventario");
            } else if (response == btnTodo) {
                exportarTodasLasColecciones();
            }
        });
    }

    private void exportarColeccion(String coleccion) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar datos de " + coleccion);
        fileChooser.setInitialFileName(coleccion + "_export.csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(webView.getScene().getWindow());
        if (file != null) {
            try {
                exportarACSV(coleccion, file);
                mostrarInfo("Datos exportados correctamente a:\n" + file.getAbsolutePath());
                lblEstado.setText("Estado: Datos de " + coleccion + " exportados");
            } catch (Exception e) {
                mostrarError("Error al exportar datos: " + e.getMessage());
            }
        }
    }

    private void exportarACSV(String coleccion, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            switch (coleccion) {
                case "clientes":
                    exportarClientes(writer);
                    break;
                case "facturas":
                    exportarFacturas(writer);
                    break;
                case "reservas":
                    exportarReservas(writer);
                    break;
                case "inventario":
                    exportarInventario(writer);
                    break;
                default:
                    writer.write("Colección no soportada\n");
            }
        }
    }
    
    private void exportarClientes(FileWriter writer) throws IOException {
        writer.write("id,nombre,dni,telefono,email,direccion,tipo_cliente,saldo_pendiente,fecha_registro\n");
        List<Cliente> clientes = clienteService.obtenerTodos();
        for (Cliente c : clientes) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%.2f,%s\n",
                escaparCSV(c.getId()),
                escaparCSV(c.getNombre()),
                escaparCSV(c.getDni()),
                escaparCSV(c.getTelefono()),
                escaparCSV(c.getEmail()),
                escaparCSV(c.getDireccion()),
                escaparCSV(c.getTipoCliente()),
                c.getSaldoPendiente(),
                c.getFechaRegistro() != null ? c.getFechaRegistro().toString() : ""
            ));
        }
    }
    
    private void exportarFacturas(FileWriter writer) throws IOException {
        writer.write("id,numero_factura,cliente_id,cliente_nombre,fecha_emision,fecha_vencimiento,subtotal,iva,total,estado,metodo_pago\n");
        List<Factura> facturas = facturaService.obtenerTodas();
        for (Factura f : facturas) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%s,%s\n",
                escaparCSV(f.getId()),
                escaparCSV(f.getNumeroFactura()),
                escaparCSV(f.getClienteId()),
                escaparCSV(f.getClienteNombre()),
                f.getFechaEmision() != null ? f.getFechaEmision().toString() : "",
                f.getFechaVencimiento() != null ? f.getFechaVencimiento().toString() : "",
                f.getSubtotal(),
                f.getIva(),
                f.getTotal(),
                escaparCSV(f.getEstado()),
                escaparCSV(f.getMetodoPago())
            ));
        }
    }
    
    private void exportarReservas(FileWriter writer) throws IOException {
        writer.write("id,maquina_id,maquina_nombre,cliente_id,cliente_nombre,fecha_inicio,fecha_fin,precio_alquiler,estado\n");
        List<Reserva> reservas = reservaService.obtenerTodas();
        for (Reserva r : reservas) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%.2f,%s\n",
                escaparCSV(r.getId()),
                escaparCSV(r.getMaquinaId()),
                escaparCSV(r.getMaquinaNombre()),
                escaparCSV(r.getClienteId()),
                escaparCSV(r.getClienteNombre()),
                r.getFechaInicio() != null ? r.getFechaInicio().toString() : "",
                r.getFechaFin() != null ? r.getFechaFin().toString() : "",
                r.getPrecioAlquiler(),
                escaparCSV(r.getEstado())
            ));
        }
    }
    
    private void exportarInventario(FileWriter writer) throws IOException {
        writer.write("id,codigo,nombre,categoria,stock_actual,stock_minimo,precio_compra,precio_venta,proveedor\n");
        List<ItemInventario> items = inventarioService.obtenerTodos();
        for (ItemInventario i : items) {
            writer.write(String.format("%s,%s,%s,%s,%d,%d,%.2f,%.2f,%s\n",
                escaparCSV(i.getId()),
                escaparCSV(i.getCodigo()),
                escaparCSV(i.getNombre()),
                escaparCSV(i.getCategoria()),
                i.getStockActual(),
                i.getStockMinimo(),
                i.getPrecioCompra(),
                i.getPrecioVenta(),
                escaparCSV(i.getProveedor())
            ));
        }
    }
    
    private String escaparCSV(String valor) {
        if (valor == null) return "";
        // Escapar comillas dobles y encerrar en comillas si contiene comas
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private void exportarTodasLasColecciones() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar carpeta de exportación");
        
        File file = fileChooser.showSaveDialog(webView.getScene().getWindow());
        if (file != null) {
            File carpeta = file.getParentFile();
            
            try {
                exportarACSV("clientes", new File(carpeta, "clientes_export.csv"));
                exportarACSV("facturas", new File(carpeta, "facturas_export.csv"));
                exportarACSV("reservas", new File(carpeta, "reservas_export.csv"));
                exportarACSV("inventario", new File(carpeta, "inventario_export.csv"));
                
                mostrarInfo("Todos los datos exportados correctamente en:\n" + carpeta.getAbsolutePath());
                lblEstado.setText("Estado: Todas las colecciones exportadas");
            } catch (Exception e) {
                mostrarError("Error al exportar datos: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleVerGuia() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guía de Configuración PowerBI");
        alert.setHeaderText("Cómo conectar PowerBI con MongoDB");
        
        String guia = """
                📊 GUÍA PASO A PASO - PowerBI + MongoDB
                
                ══════════════════════════════════════════════════════════
                
                OPCIÓN 1: Conectar PowerBI Desktop directamente a MongoDB
                ══════════════════════════════════════════════════════════
                
                1. Abre PowerBI Desktop
                
                2. Click en "Obtener datos" > "Más..." 
                
                3. Busca "MongoDB" en la lista de conectores
                   (Requiere PowerBI Desktop versión reciente)
                
                4. Configurar conexión:
                   - Server: localhost:27017
                   - Database: taller_db
                   - Authentication: 
                     * Usuario: admin
                     * Contraseña: admin123
                
                5. Selecciona las colecciones (tablas):
                   ✓ clientes
                   ✓ facturas
                   ✓ reservas
                   ✓ inventario
                   ✓ maquinas
                
                6. Transforma y carga los datos
                
                7. Crea tus visualizaciones
                
                ══════════════════════════════════════════════════════════
                
                OPCIÓN 2: Usar archivos CSV exportados
                ══════════════════════════════════════════════════════════
                
                1. En esta aplicación, click en "Exportar Datos"
                
                2. Exporta las colecciones que necesites
                
                3. En PowerBI Desktop:
                   - "Obtener datos" > "Texto/CSV"
                   - Selecciona los archivos exportados
                
                4. Crea tus visualizaciones
                
                ══════════════════════════════════════════════════════════
                
                OPCIÓN 3: Embeber reportes en la aplicación
                ══════════════════════════════════════════════════════════
                
                1. Crea y publica tu reporte en PowerBI Service
                   (Requiere cuenta PowerBI Pro o Premium)
                
                2. En PowerBI Service, abre tu reporte
                
                3. Click en "Archivo" > "Insertar informe" > "Sitio web o portal"
                
                4. Copia la URL de inserción
                
                5. En el código (ReportesController.java):
                   - Busca el método configurarReportes()
                   - Agrega tu URL:
                     reportesUrls.put("Mi Reporte", "TU_URL_AQUI");
                
                6. Recompila la aplicación
                
                ══════════════════════════════════════════════════════════
                
                💡 RECOMENDACIÓN:
                
                Para empezar, usa la OPCIÓN 1 o 2 (más simple).
                La OPCIÓN 3 es ideal cuando ya tengas reportes listos.
                
                ══════════════════════════════════════════════════════════
                """;
        
        alert.setContentText(guia);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(650);
        alert.showAndWait();
    }

    private void mostrarPanelAyuda(boolean mostrar) {
        panelAyuda.setVisible(mostrar);
        panelAyuda.setManaged(mostrar);
        webView.setVisible(!mostrar);
        webView.setManaged(!mostrar);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
