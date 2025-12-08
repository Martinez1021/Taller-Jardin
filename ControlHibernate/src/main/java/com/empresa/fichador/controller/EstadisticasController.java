package com.empresa.fichador.controller;

import com.empresa.fichador.dao.FichajeDAO;
import com.empresa.fichador.dao.TrabajadorDAO;
import com.empresa.fichador.model.Fichaje;
import com.empresa.fichador.model.Trabajador;
import com.empresa.fichador.service.EstadisticasService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EstadisticasController {

    @FXML private BarChart<String, Number> barChartPuntualidad;
    @FXML private CategoryAxis xAxisBar;
    @FXML private NumberAxis yAxisBar;
    @FXML private PieChart pieChartPorcentajes;
    @FXML private LineChart<String, Number> lineChartHoras;
    @FXML private CategoryAxis xAxisLine;
    @FXML private NumberAxis yAxisLine;

    @FXML private Label lblTotalEmpleados;
    @FXML private Label lblPuntuales;
    @FXML private Label lblTarde;
    @FXML private Label lblMediaHoras;

    @FXML private TableView<Map<String, String>> tableDetalle;
    @FXML private TableColumn<Map<String, String>, String> colEmpleado;
    @FXML private TableColumn<Map<String, String>, String> colDepartamento;
    @FXML private TableColumn<Map<String, String>, String> colEntrada;
    @FXML private TableColumn<Map<String, String>, String> colSalida;
    @FXML private TableColumn<Map<String, String>, String> colHoras;
    @FXML private TableColumn<Map<String, String>, String> colEstado;

    private EstadisticasService estadisticasService;
    private FichajeDAO fichajeDAO;
    private TrabajadorDAO trabajadorDAO;

    @FXML
    public void initialize() {
        try {
            estadisticasService = new EstadisticasService();
            fichajeDAO = new FichajeDAO();
            trabajadorDAO = new TrabajadorDAO();

            configurarTabla();
            cargarEstadisticas();
            cargarGraficos();
            cargarTablaDetalle();
        } catch (Exception e) {
            System.err.println("Error al inicializar estadísticas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        if (colEmpleado != null) {
            colEmpleado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("empleado")));
            colDepartamento.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("departamento")));
            colEntrada.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("entrada")));
            colSalida.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("salida")));
            colHoras.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("horas")));
            colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("estado")));
        }
    }

    private void cargarEstadisticas() {
        try {
            // Total empleados
            List<Trabajador> trabajadores = trabajadorDAO.findAll();
            long activos = trabajadores.stream().filter(Trabajador::isActivo).count();
            if (lblTotalEmpleados != null) lblTotalEmpleados.setText(String.valueOf(activos));

            // Fichajes de hoy
            List<Fichaje> fichajesHoy = fichajeDAO.findByFecha(LocalDate.now());

            long puntuales = fichajesHoy.stream()
                    .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null)
                    .filter(f -> f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15)))
                    .count();

            long tarde = fichajesHoy.stream()
                    .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null)
                    .filter(f -> !f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15)))
                    .count();

            if (lblPuntuales != null) lblPuntuales.setText(String.valueOf(puntuales));
            if (lblTarde != null) lblTarde.setText(String.valueOf(tarde));

            // Media de horas
            Double mediaHoras = fichajeDAO.calcularMediaHoras(LocalDate.now().minusDays(7), LocalDate.now());
            if (lblMediaHoras != null) lblMediaHoras.setText(String.format("%.1fh", mediaHoras != null ? mediaHoras : 0));

        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
        }
    }

    private void cargarGraficos() {
        cargarGraficoPuntualidad();
        cargarGraficoPorcentajes();
        cargarGraficoMediaHoras();
    }

    private void cargarGraficoPuntualidad() {
        try {
            if (barChartPuntualidad == null) return;

            barChartPuntualidad.getData().clear();
            LocalDate hoy = LocalDate.now();
            Map<String, Long> estadisticas = estadisticasService.obtenerPuntualidad(hoy);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Puntualidad");

            for (Map.Entry<String, Long> entry : estadisticas.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChartPuntualidad.getData().add(series);
        } catch (Exception e) {
            System.err.println("Error al cargar gráfico de puntualidad: " + e.getMessage());
        }
    }

    private void cargarGraficoPorcentajes() {
        try {
            if (pieChartPorcentajes == null) return;

            pieChartPorcentajes.getData().clear();
            LocalDate hoy = LocalDate.now();
            Map<String, Long> estadisticas = estadisticasService.obtenerPuntualidad(hoy);

            for (Map.Entry<String, Long> entry : estadisticas.entrySet()) {
                if (entry.getValue() > 0) {
                    PieChart.Data slice = new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
                    pieChartPorcentajes.getData().add(slice);
                }
            }

            if (pieChartPorcentajes.getData().isEmpty()) {
                pieChartPorcentajes.getData().add(new PieChart.Data("Sin datos", 1));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar gráfico de porcentajes: " + e.getMessage());
        }
    }

    private void cargarGraficoMediaHoras() {
        try {
            if (lineChartHoras == null) return;

            lineChartHoras.getData().clear();
            Map<String, Double> mediaHoras = estadisticasService.obtenerMediaHorasUltimos7Dias();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Horas");

            // Ordenar por fecha
            List<Map.Entry<String, Double>> entradas = new ArrayList<>(mediaHoras.entrySet());
            // Las fechas vienen en formato dd/MM, ordenar cronológicamente

            for (Map.Entry<String, Double> entry : entradas) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            lineChartHoras.getData().add(series);
        } catch (Exception e) {
            System.err.println("Error al cargar gráfico de media de horas: " + e.getMessage());
        }
    }

    private void cargarTablaDetalle() {
        try {
            if (tableDetalle == null) return;

            ObservableList<Map<String, String>> datos = FXCollections.observableArrayList();
            List<Trabajador> trabajadores = trabajadorDAO.findAll();
            LocalDate hoy = LocalDate.now();

            for (Trabajador t : trabajadores) {
                if (!t.isActivo()) continue;

                List<Fichaje> fichajes = fichajeDAO.findByTrabajadorAndFecha(t, hoy);

                String entrada = "--:--";
                String salida = "--:--";
                String horas = "0h";
                String estado = "Sin fichar";

                for (Fichaje f : fichajes) {
                    if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null) {
                        entrada = f.getHoraEntrada().format(DateTimeFormatter.ofPattern("HH:mm"));
                        if (f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15))) {
                            estado = "✅ Puntual";
                        } else {
                            estado = "⚠️ Tarde";
                        }
                    }
                    if (f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA && f.getHoraSalida() != null) {
                        salida = f.getHoraSalida().format(DateTimeFormatter.ofPattern("HH:mm"));
                    }
                }

                // Calcular horas
                if (!entrada.equals("--:--") && !salida.equals("--:--")) {
                    estado = "✅ Completo";
                }

                Map<String, String> fila = new HashMap<>();
                fila.put("empleado", t.getNombreCompleto());
                fila.put("departamento", t.getDepartamento() != null ? t.getDepartamento().getNombre() : "-");
                fila.put("entrada", entrada);
                fila.put("salida", salida);
                fila.put("horas", horas);
                fila.put("estado", estado);

                datos.add(fila);
            }

            tableDetalle.setItems(datos);

        } catch (Exception e) {
            System.err.println("Error al cargar tabla de detalle: " + e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        cargarEstadisticas();
        cargarGraficos();
        cargarTablaDetalle();
    }
}

