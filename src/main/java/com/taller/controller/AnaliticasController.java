package com.taller.controller;

import com.taller.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AnaliticasController {
    
    @FXML private Label lblTotalFacturado;
    @FXML private Label lblFacturadoMes;
    @FXML private Label lblReservasActivas;
    @FXML private Label lblReservasPendientes;
    @FXML private Label lblTotalClientes;
    @FXML private Label lblClientesNuevos;
    @FXML private Label lblStockCritico;
    @FXML private Label lblSaldoPendiente;
    @FXML private Label lblTotalProductos;
    @FXML private Label lblReparacionesActivas;
    @FXML private Label lblValorInventario;
    @FXML private Label lblUltimaActualizacion;
    
    @FXML private LineChart<String, Number> chartFacturacion;
    @FXML private PieChart chartReservas;
    @FXML private BarChart<String, Number> chartTopClientes;
    @FXML private BarChart<String, Number> chartInventario;
    
    private final FacturaService facturaService = new FacturaService();
    private final ReservaService reservaService = new ReservaService();
    private final ClienteService clienteService = new ClienteService();
    private final InventarioService inventarioService = new InventarioService();

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            cargarKPIs();
            cargarGraficoFacturacion();
            cargarGraficoReservas();
            cargarGraficoTopClientes();
            cargarGraficoInventario();
            
            actualizarFechaActualizacion();
        } catch (Exception e) {
            System.err.println("Error cargando analíticas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarKPIs() {
        // Total Facturado este mes
        double totalMes = facturaService.obtenerTotalFacturadoMesActual();
        lblTotalFacturado.setText(String.format("%.2f €", totalMes));
        lblFacturadoMes.setText("Este mes");
        
        // Reservas
        long activas = reservaService.contarPorEstado("confirmada") + 
                      reservaService.contarPorEstado("en_curso");
        long pendientes = reservaService.contarPorEstado("pendiente");
        lblReservasActivas.setText(String.valueOf(activas));
        lblReservasPendientes.setText(pendientes + " pendientes");
        
        // Clientes
        long totalClientes = clienteService.contarTotal();
        long nuevosEsteMes = clienteService.contarNuevosEsteMes();
        lblTotalClientes.setText(String.valueOf(totalClientes));
        lblClientesNuevos.setText(nuevosEsteMes + " este mes");
        
        // Stock Crítico
        long stockCritico = inventarioService.contarStockBajoMinimo();
        lblStockCritico.setText(String.valueOf(stockCritico));
        
        // Saldo Pendiente
        double saldoPendiente = clienteService.obtenerSaldoTotalPendiente();
        lblSaldoPendiente.setText(String.format("%.2f €", saldoPendiente));
        
        // Total Productos en Inventario
        long totalProductos = inventarioService.contarTotal();
        lblTotalProductos.setText(String.valueOf(totalProductos));
        
        // Reparaciones Activas (simulado - ajustar cuando tengas el servicio)
        lblReparacionesActivas.setText("0");
        
        // Valor Total del Inventario
        double valorInventario = inventarioService.obtenerValorTotalInventario();
        lblValorInventario.setText(String.format("%.2f €", valorInventario));
    }

    private void cargarGraficoFacturacion() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Facturación");
        
        // Obtener datos de los últimos 6 meses
        Map<String, Double> facturacionMensual = facturaService.obtenerFacturacionUltimos6Meses();
        
        // Agregar datos al gráfico
        facturacionMensual.forEach((mes, total) -> {
            series.getData().add(new XYChart.Data<>(mes, total));
        });
        
        chartFacturacion.getData().clear();
        chartFacturacion.getData().add(series);
        chartFacturacion.setCreateSymbols(true);
    }

    private void cargarGraficoReservas() {
        chartReservas.getData().clear();
        
        Map<String, Long> reservasPorEstado = new HashMap<>();
        reservasPorEstado.put("Confirmadas", reservaService.contarPorEstado("confirmada"));
        reservasPorEstado.put("En Curso", reservaService.contarPorEstado("en_curso"));
        reservasPorEstado.put("Pendientes", reservaService.contarPorEstado("pendiente"));
        reservasPorEstado.put("Completadas", reservaService.contarPorEstado("completada"));
        reservasPorEstado.put("Canceladas", reservaService.contarPorEstado("cancelada"));
        
        reservasPorEstado.forEach((estado, cantidad) -> {
            if (cantidad > 0) {
                PieChart.Data slice = new PieChart.Data(estado + " (" + cantidad + ")", cantidad);
                chartReservas.getData().add(slice);
            }
        });
    }

    private void cargarGraficoTopClientes() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Obtener top 10 clientes
        List<Map.Entry<String, Double>> topClientes = facturaService.obtenerTopClientes(10);
        
        topClientes.forEach(entry -> {
            String nombre = entry.getKey();
            if (nombre.length() > 20) {
                nombre = nombre.substring(0, 17) + "...";
            }
            series.getData().add(new XYChart.Data<>(nombre, entry.getValue()));
        });
        
        chartTopClientes.getData().clear();
        chartTopClientes.getData().add(series);
    }

    private void cargarGraficoInventario() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Obtener stock por categoría
        Map<String, Integer> stockPorCategoria = inventarioService.obtenerStockPorCategoria();
        
        stockPorCategoria.forEach((categoria, stock) -> {
            series.getData().add(new XYChart.Data<>(categoria, stock));
        });
        
        chartInventario.getData().clear();
        chartInventario.getData().add(series);
    }

    @FXML
    private void handleActualizar() {
        cargarDatos();
    }

    @FXML
    private void handleExportarPDF() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar a PDF");
        alert.setHeaderText("Exportación de Reportes");
        alert.setContentText("Funcionalidad de exportación a PDF próximamente.\n\n" +
            "Por ahora puedes:\n" +
            "1. Hacer captura de pantalla (Windows + Shift + S)\n" +
            "2. Usar 'Exportar Datos' en la sección de Reportes PowerBI");
        alert.showAndWait();
    }

    private void actualizarFechaActualizacion() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblUltimaActualizacion.setText("Última actualización: " + 
            LocalDateTime.now().format(formatter));
    }
}
