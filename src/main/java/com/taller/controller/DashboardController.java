package com.taller.controller;

import com.taller.service.*;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    
    @FXML private Label lblTotalMaquinas;
    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalInventario;
    @FXML private Label lblReservasActivas;
    @FXML private Label lblIngresosMes;
    @FXML private Label lblFacturasPendientes;
    @FXML private Label lblStockBajo;
    @FXML private Label lblSaldoPendiente;
    
    @FXML private PieChart chartEstadoMaquinas;
    @FXML private BarChart<String, Number> chartReparacionesMes;
    @FXML private LineChart<String, Number> chartIngresosMensuales;
    @FXML private PieChart chartEstadoReservas;
    
    private final MaquinaService maquinaService = new MaquinaService();
    private final ClienteService clienteService = new ClienteService();
    private final InventarioService inventarioService = new InventarioService();
    private final ReservaService reservaService = new ReservaService();
    private final FacturaService facturaService = new FacturaService();

    @FXML
    public void initialize() {
        cargarEstadisticas();
        cargarGraficos();
    }

    private void cargarEstadisticas() {
        // Total de máquinas
        long totalMaquinas = maquinaService.contarTotal();
        lblTotalMaquinas.setText(String.valueOf(totalMaquinas));
        
        // Total de clientes
        long totalClientes = clienteService.contarTotal();
        lblTotalClientes.setText(String.valueOf(totalClientes));
        
        // Total items inventario
        long totalInventario = inventarioService.contarTotal();
        lblTotalInventario.setText(String.valueOf(totalInventario));
        
        // Reservas activas
        long reservasActivas = reservaService.contarPorEstado("confirmada") + 
                               reservaService.contarPorEstado("en_curso");
        lblReservasActivas.setText(String.valueOf(reservasActivas));
        
        // Ingresos del mes
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        double ingresosMes = facturaService.obtenerPorRangoFechas(inicioMes, finMes)
            .stream()
            .filter(f -> f.getEstado().equals("pagada"))
            .mapToDouble(f -> f.getTotal())
            .sum();
        lblIngresosMes.setText(String.format("%.2f€", ingresosMes));
        
        // Facturas pendientes
        long facturasPendientes = facturaService.contarPorEstado("emitida");
        lblFacturasPendientes.setText(String.valueOf(facturasPendientes));
        
        // Stock bajo
        long stockBajo = inventarioService.contarBajoStock();
        lblStockBajo.setText(String.valueOf(stockBajo));
        
        // Saldo pendiente
        double saldoPendiente = clienteService.obtenerSaldoTotalPendiente();
        lblSaldoPendiente.setText(String.format("%.2f€", saldoPendiente));
    }

    private void cargarGraficos() {
        cargarChartEstadoMaquinas();
        cargarChartReparacionesMes();
        cargarChartIngresosMensuales();
        cargarChartEstadoReservas();
    }

    private void cargarChartEstadoMaquinas() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Disponibles", maquinaService.obtenerPorEstado("disponible").size()),
            new PieChart.Data("En Reparación", maquinaService.obtenerPorEstado("en_reparacion").size()),
            new PieChart.Data("Alquiladas", maquinaService.obtenerPorEstado("alquilada").size()),
            new PieChart.Data("Fuera de Servicio", maquinaService.obtenerPorEstado("fuera_servicio").size())
        );
        chartEstadoMaquinas.setData(pieChartData);
    }

    private void cargarChartReparacionesMes() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reparaciones");
        
        // Últimos 6 meses
        for (int i = 5; i >= 0; i--) {
            LocalDate mes = LocalDate.now().minusMonths(i);
            String nombreMes = mes.format(DateTimeFormatter.ofPattern("MMM"));
            
            LocalDate inicio = mes.withDayOfMonth(1);
            LocalDate fin = mes.withDayOfMonth(mes.lengthOfMonth());
            
            long reparaciones = maquinaService.contarReparacionesEnRango(inicio, fin);
            series.getData().add(new XYChart.Data<>(nombreMes, reparaciones));
        }
        
        chartReparacionesMes.getData().clear();
        chartReparacionesMes.getData().add(series);
    }

    private void cargarChartIngresosMensuales() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos");
        
        // Últimos 6 meses
        for (int i = 5; i >= 0; i--) {
            LocalDate mes = LocalDate.now().minusMonths(i);
            String nombreMes = mes.format(DateTimeFormatter.ofPattern("MMM"));
            
            LocalDate inicio = mes.withDayOfMonth(1);
            LocalDate fin = mes.withDayOfMonth(mes.lengthOfMonth());
            
            double ingresos = facturaService.obtenerPorRangoFechas(inicio, fin)
                .stream()
                .filter(f -> f.getEstado().equals("pagada"))
                .mapToDouble(f -> f.getTotal())
                .sum();
            
            series.getData().add(new XYChart.Data<>(nombreMes, ingresos));
        }
        
        chartIngresosMensuales.getData().clear();
        chartIngresosMensuales.getData().add(series);
    }

    private void cargarChartEstadoReservas() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Pendientes", reservaService.contarPorEstado("pendiente")),
            new PieChart.Data("Confirmadas", reservaService.contarPorEstado("confirmada")),
            new PieChart.Data("En Curso", reservaService.contarPorEstado("en_curso")),
            new PieChart.Data("Completadas", reservaService.contarPorEstado("completada"))
        );
        chartEstadoReservas.setData(pieChartData);
    }

    @FXML
    private void handleRefresh() {
        cargarEstadisticas();
        cargarGraficos();
    }
}
