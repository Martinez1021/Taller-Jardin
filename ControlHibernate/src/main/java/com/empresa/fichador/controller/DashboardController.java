package com.empresa.fichador.controller;

import com.empresa.fichador.dao.*;
import com.empresa.fichador.model.*;
import com.empresa.fichador.service.SessionService;
import com.empresa.fichador.util.HibernateUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class DashboardController {

    @FXML private Label lblBienvenida;
    @FXML private Label lblFechaHoy;
    @FXML private Label lblTotalTrabajadores;
    @FXML private Label lblFichajesHoy;
    @FXML private Label lblPresentes;
    @FXML private Label lblIncidencias;
    @FXML private Label lblHoraPromedioEntrada;
    @FXML private Label lblHoraPromedioSalida;
    @FXML private Label lblHorasTotalesHoy;
    @FXML private Label lblTasaPuntualidad;
    @FXML private Label lblContadorPresentes;
    @FXML private BarChart<String, Number> chartActividad;
    @FXML private CategoryAxis xAxisActividad;
    @FXML private NumberAxis yAxisActividad;
    @FXML private PieChart chartPuntualidad;
    @FXML private VBox vboxUltimosFichajes;
    @FXML private FlowPane flowPresentes;

    private TrabajadorDAO trabajadorDAO;
    private FichajeDAO fichajeDAO;
    private IncidenciaDAO incidenciaDAO;
    private Timeline autoRefresh;
    private boolean dbDisponible = false;

    @FXML
    public void initialize() {
        try {
            // Verificar conexión BD
            dbDisponible = !HibernateUtil.hasConnectionError() && HibernateUtil.getSessionFactory() != null;

            if (dbDisponible) {
                trabajadorDAO = new TrabajadorDAO();
                fichajeDAO = new FichajeDAO();
                incidenciaDAO = new IncidenciaDAO();
            }

            actualizarSaludo();
            actualizarFecha();

            if (dbDisponible) {
                cargarEstadisticas();
                cargarGraficos();
                cargarUltimosFichajes();
                cargarTrabajadoresPresentes();

                // Actualizar cada 30 segundos
                autoRefresh = new Timeline(new KeyFrame(Duration.seconds(30), e -> handleActualizar()));
                autoRefresh.setCycleCount(Timeline.INDEFINITE);
                autoRefresh.play();
            } else {
                mostrarModoDemo();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            mostrarModoDemo();
        }
    }

    private void mostrarModoDemo() {
        // Datos de ejemplo
        if (lblTotalTrabajadores != null) lblTotalTrabajadores.setText("10");
        if (lblFichajesHoy != null) lblFichajesHoy.setText("8");
        if (lblPresentes != null) lblPresentes.setText("5");
        if (lblIncidencias != null) lblIncidencias.setText("0");
        if (lblHoraPromedioEntrada != null) lblHoraPromedioEntrada.setText("08:15");
        if (lblHoraPromedioSalida != null) lblHoraPromedioSalida.setText("17:30");
        if (lblHorasTotalesHoy != null) lblHorasTotalesHoy.setText("40h");
        if (lblTasaPuntualidad != null) lblTasaPuntualidad.setText("85%");
        if (lblContadorPresentes != null) lblContadorPresentes.setText("5 de 10");

        cargarGraficoActividadDemo();
        cargarGraficoPuntualidadDemo();

        if (vboxUltimosFichajes != null) {
            vboxUltimosFichajes.getChildren().clear();
            Label lblDemo = new Label("Sin conexión a base de datos");
            lblDemo.setStyle("-fx-text-fill: #999999; -fx-font-size: 13px;");
            vboxUltimosFichajes.getChildren().add(lblDemo);
        }

        if (flowPresentes != null) {
            flowPresentes.getChildren().clear();
            Label lblDemo = new Label("Sin conexión a base de datos");
            lblDemo.setStyle("-fx-text-fill: #999999; -fx-font-size: 13px;");
            flowPresentes.getChildren().add(lblDemo);
        }
    }

    private void cargarGraficoActividadDemo() {
        if (chartActividad == null) return;
        chartActividad.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Fichajes");
        String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        int[] valores = {18, 20, 19, 21, 17, 2, 0};
        for (int i = 0; i < dias.length; i++) {
            series.getData().add(new XYChart.Data<>(dias[i], valores[i]));
        }
        chartActividad.getData().add(series);
    }

    private void cargarGraficoPuntualidadDemo() {
        if (chartPuntualidad == null) return;
        chartPuntualidad.getData().clear();
        chartPuntualidad.getData().add(new PieChart.Data("Puntuales (8)", 8));
        chartPuntualidad.getData().add(new PieChart.Data("Tarde (2)", 2));
    }

    private void actualizarSaludo() {
        String nombre = SessionService.getInstance().getNombreUsuario();
        int hora = LocalDateTime.now().getHour();
        String saludo;

        if (hora < 12) {
            saludo = "¡Buenos días";
        } else if (hora < 20) {
            saludo = "¡Buenas tardes";
        } else {
            saludo = "¡Buenas noches";
        }

        if (lblBienvenida != null) {
            lblBienvenida.setText(saludo + ", " + nombre + "! 👋");
        }
    }

    private void actualizarFecha() {
        if (lblFechaHoy != null) {
            LocalDate hoy = LocalDate.now();
            String diaSemana = hoy.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            diaSemana = diaSemana.substring(0, 1).toUpperCase() + diaSemana.substring(1);
            String mes = hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

            lblFechaHoy.setText(String.format("%s, %d de %s de %d",
                    diaSemana, hoy.getDayOfMonth(), mes, hoy.getYear()));
        }
    }

    private void cargarEstadisticas() {
        if (!dbDisponible) return;

        try {
            // Total trabajadores
            List<Trabajador> trabajadores = trabajadorDAO.findAll();
            long activos = trabajadores.stream().filter(Trabajador::isActivo).count();
            if (lblTotalTrabajadores != null) lblTotalTrabajadores.setText(String.valueOf(activos));

            // Fichajes de hoy
            List<Fichaje> fichajesHoy = fichajeDAO.findByFecha(LocalDate.now());
            if (lblFichajesHoy != null) lblFichajesHoy.setText(String.valueOf(fichajesHoy.size()));

            // Presentes
            long presentes = contarPresentes(fichajesHoy);
            if (lblPresentes != null) lblPresentes.setText(String.valueOf(presentes));
            if (lblContadorPresentes != null) lblContadorPresentes.setText(presentes + " de " + activos);

            // Incidencias
            if (lblIncidencias != null) lblIncidencias.setText("0");

            calcularPromedios(fichajesHoy);
            calcularPuntualidad(fichajesHoy);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private long contarPresentes(List<Fichaje> fichajesHoy) {
        Set<Long> conEntrada = new HashSet<>();
        Set<Long> conSalida = new HashSet<>();

        for (Fichaje f : fichajesHoy) {
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA) {
                conEntrada.add(f.getTrabajador().getId());
            } else {
                conSalida.add(f.getTrabajador().getId());
            }
        }

        conEntrada.removeAll(conSalida);
        return conEntrada.size();
    }

    private void calcularPromedios(List<Fichaje> fichajesHoy) {
        List<LocalDateTime> entradas = new ArrayList<>();
        List<LocalDateTime> salidas = new ArrayList<>();

        for (Fichaje f : fichajesHoy) {
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null) {
                entradas.add(f.getHoraEntrada());
            } else if (f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA && f.getHoraSalida() != null) {
                salidas.add(f.getHoraSalida());
            }
        }

        if (!entradas.isEmpty() && lblHoraPromedioEntrada != null) {
            long avgMinutos = (long) entradas.stream()
                    .mapToLong(dt -> dt.getHour() * 60 + dt.getMinute())
                    .average().orElse(0);
            lblHoraPromedioEntrada.setText(String.format("%02d:%02d", avgMinutos / 60, avgMinutos % 60));
        } else if (lblHoraPromedioEntrada != null) {
            lblHoraPromedioEntrada.setText("--:--");
        }

        if (!salidas.isEmpty() && lblHoraPromedioSalida != null) {
            long avgMinutos = (long) salidas.stream()
                    .mapToLong(dt -> dt.getHour() * 60 + dt.getMinute())
                    .average().orElse(0);
            lblHoraPromedioSalida.setText(String.format("%02d:%02d", avgMinutos / 60, avgMinutos % 60));
        } else if (lblHoraPromedioSalida != null) {
            lblHoraPromedioSalida.setText("--:--");
        }

        // Horas totales
        double horasTotales = 0;
        Map<Long, LocalDateTime> entradasMap = new HashMap<>();

        for (Fichaje f : fichajesHoy) {
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null) {
                entradasMap.put(f.getTrabajador().getId(), f.getHoraEntrada());
            }
        }

        for (Fichaje f : fichajesHoy) {
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA && f.getHoraSalida() != null) {
                LocalDateTime entrada = entradasMap.get(f.getTrabajador().getId());
                if (entrada != null) {
                    horasTotales += java.time.Duration.between(entrada, f.getHoraSalida()).toMinutes() / 60.0;
                }
            }
        }

        if (lblHorasTotalesHoy != null) lblHorasTotalesHoy.setText(String.format("%.1fh", horasTotales));
    }

    private void calcularPuntualidad(List<Fichaje> fichajesHoy) {
        long totalEntradas = fichajesHoy.stream()
                .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA)
                .count();

        long puntuales = fichajesHoy.stream()
                .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null)
                .filter(f -> f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15)))
                .count();

        if (totalEntradas > 0 && lblTasaPuntualidad != null) {
            double tasa = (puntuales * 100.0) / totalEntradas;
            lblTasaPuntualidad.setText(String.format("%.0f%%", tasa));
        } else if (lblTasaPuntualidad != null) {
            lblTasaPuntualidad.setText("--");
        }
    }

    private void cargarGraficos() {
        if (!dbDisponible) return;
        cargarGraficoActividad();
        cargarGraficoPuntualidad();
    }

    private void cargarGraficoActividad() {
        if (chartActividad == null) return;
        chartActividad.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Fichajes");

        String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        LocalDate hoy = LocalDate.now();
        int diaActual = hoy.getDayOfWeek().getValue();

        for (int i = 1; i <= 7; i++) {
            LocalDate fecha = hoy.minusDays(diaActual - i);
            int fichajes = fichajeDAO.findByFecha(fecha).size();
            series.getData().add(new XYChart.Data<>(dias[i - 1], fichajes));
        }

        chartActividad.getData().add(series);
    }

    private void cargarGraficoPuntualidad() {
        if (chartPuntualidad == null) return;
        chartPuntualidad.getData().clear();

        List<Fichaje> fichajesHoy = fichajeDAO.findByFecha(LocalDate.now());

        long puntuales = fichajesHoy.stream()
                .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null)
                .filter(f -> f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15)))
                .count();

        long tarde = fichajesHoy.stream()
                .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null)
                .filter(f -> !f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15)))
                .count();

        if (puntuales > 0 || tarde > 0) {
            chartPuntualidad.getData().add(new PieChart.Data("Puntuales (" + puntuales + ")", puntuales));
            chartPuntualidad.getData().add(new PieChart.Data("Tarde (" + tarde + ")", tarde));
        } else {
            chartPuntualidad.getData().add(new PieChart.Data("Sin datos", 1));
        }
    }

    private void cargarUltimosFichajes() {
        if (!dbDisponible || vboxUltimosFichajes == null) return;

        vboxUltimosFichajes.getChildren().clear();

        List<Fichaje> fichajes = fichajeDAO.findByFecha(LocalDate.now());
        fichajes.sort((a, b) -> {
            LocalDateTime horaA = a.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? a.getHoraEntrada() : a.getHoraSalida();
            LocalDateTime horaB = b.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? b.getHoraEntrada() : b.getHoraSalida();
            if (horaA == null) return 1;
            if (horaB == null) return -1;
            return horaB.compareTo(horaA);
        });

        int count = 0;
        for (Fichaje fichaje : fichajes) {
            if (count >= 6) break;
            vboxUltimosFichajes.getChildren().add(crearFilaFichaje(fichaje));
            count++;
        }

        if (fichajes.isEmpty()) {
            Label lblVacio = new Label("No hay fichajes registrados hoy");
            lblVacio.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
            vboxUltimosFichajes.getChildren().add(lblVacio);
        }
    }

    private HBox crearFilaFichaje(Fichaje fichaje) {
        HBox fila = new HBox(15);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12 16; -fx-background-radius: 10;");

        StackPane avatar = new StackPane();
        avatar.setStyle("-fx-background-color: " +
                (fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "#d1fae5" : "#fee2e2") +
                "; -fx-background-radius: 100; -fx-min-width: 40; -fx-min-height: 40;");
        Label lblIniciales = new Label(fichaje.getTrabajador().getIniciales());
        lblIniciales.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " +
                (fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "#059669" : "#dc2626") + ";");
        avatar.getChildren().add(lblIniciales);

        VBox infoBox = new VBox(2);
        Label lblNombre = new Label(fichaje.getTrabajador().getNombreCompleto());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        String tipoTexto = fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "⬇ Entrada" : "⬆ Salida";
        Label lblTipo = new Label(tipoTexto);
        lblTipo.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                (fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "#10b981" : "#ef4444") + ";");
        infoBox.getChildren().addAll(lblNombre, lblTipo);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        LocalDateTime hora = fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ?
                fichaje.getHoraEntrada() : fichaje.getHoraSalida();
        String horaStr = hora != null ? hora.format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--";
        Label lblHora = new Label(horaStr);
        lblHora.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #334155;");

        fila.getChildren().addAll(avatar, infoBox, lblHora);
        return fila;
    }

    private void cargarTrabajadoresPresentes() {
        if (!dbDisponible || flowPresentes == null) return;

        flowPresentes.getChildren().clear();

        List<Fichaje> fichajesHoy = fichajeDAO.findByFecha(LocalDate.now());
        Set<Long> conEntrada = new HashSet<>();
        Set<Long> conSalida = new HashSet<>();
        Map<Long, Trabajador> trabajadoresMap = new HashMap<>();

        for (Fichaje f : fichajesHoy) {
            trabajadoresMap.put(f.getTrabajador().getId(), f.getTrabajador());
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA) {
                conEntrada.add(f.getTrabajador().getId());
            } else {
                conSalida.add(f.getTrabajador().getId());
            }
        }

        conEntrada.removeAll(conSalida);

        for (Long id : conEntrada) {
            Trabajador t = trabajadoresMap.get(id);
            if (t != null) {
                flowPresentes.getChildren().add(crearTarjetaPresente(t));
            }
        }

        if (conEntrada.isEmpty()) {
            Label lblVacio = new Label("No hay trabajadores presentes en este momento");
            lblVacio.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
            flowPresentes.getChildren().add(lblVacio);
        }
    }

    private HBox crearTarjetaPresente(Trabajador trabajador) {
        HBox tarjeta = new HBox(10);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setStyle("-fx-background-color: #f0fdf4; -fx-padding: 10 15; -fx-background-radius: 10; -fx-border-color: #bbf7d0; -fx-border-radius: 10;");

        StackPane avatar = new StackPane();
        avatar.setStyle("-fx-background-color: #10b981; -fx-background-radius: 100; -fx-min-width: 35; -fx-min-height: 35;");
        Label lblIniciales = new Label(trabajador.getIniciales());
        lblIniciales.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: white;");
        avatar.getChildren().add(lblIniciales);

        VBox infoBox = new VBox(1);
        Label lblNombre = new Label(trabajador.getNombreCompleto());
        lblNombre.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #166534;");
        Label lblCargo = new Label(trabajador.getCargo() != null ? trabajador.getCargo() : "");
        lblCargo.setStyle("-fx-font-size: 11px; -fx-text-fill: #16a34a;");
        infoBox.getChildren().addAll(lblNombre);
        if (trabajador.getCargo() != null && !trabajador.getCargo().isEmpty()) {
            infoBox.getChildren().add(lblCargo);
        }

        tarjeta.getChildren().addAll(avatar, infoBox);
        return tarjeta;
    }

    @FXML
    private void handleActualizar() {
        if (dbDisponible) {
            cargarEstadisticas();
            cargarGraficos();
            cargarUltimosFichajes();
            cargarTrabajadoresPresentes();
        }
    }
}

