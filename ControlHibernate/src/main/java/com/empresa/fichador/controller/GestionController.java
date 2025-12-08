package com.empresa.fichador.controller;

import com.empresa.fichador.dao.DepartamentoDAO;
import com.empresa.fichador.dao.FichajeDAO;
import com.empresa.fichador.dao.TrabajadorDAO;
import com.empresa.fichador.model.Departamento;
import com.empresa.fichador.model.Fichaje;
import com.empresa.fichador.model.Trabajador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GestionController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Trabajador> comboTrabajador;
    @FXML private ComboBox<Departamento> comboDepartamento;

    @FXML private Label lblTotalFichajes;
    @FXML private Label lblTotalEntradas;
    @FXML private Label lblTotalSalidas;
    @FXML private Label lblTotalHoras;

    @FXML private TableView<FichajeResumen> tableFichajes;
    @FXML private TableColumn<FichajeResumen, String> colTrabajador;
    @FXML private TableColumn<FichajeResumen, String> colDepartamento;
    @FXML private TableColumn<FichajeResumen, String> colFecha;
    @FXML private TableColumn<FichajeResumen, String> colEntrada;
    @FXML private TableColumn<FichajeResumen, String> colSalida;
    @FXML private TableColumn<FichajeResumen, String> colTotalHoras;
    @FXML private TableColumn<FichajeResumen, String> colClima;
    @FXML private TableColumn<FichajeResumen, String> colTemperatura;
    @FXML private TableColumn<FichajeResumen, Void> colAcciones;

    private FichajeDAO fichajeDAO;
    private TrabajadorDAO trabajadorDAO;
    private DepartamentoDAO departamentoDAO;
    private List<Fichaje> fichajesTotales = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            fichajeDAO = new FichajeDAO();
            trabajadorDAO = new TrabajadorDAO();
            departamentoDAO = new DepartamentoDAO();

            configurarFiltros();
            configurarTabla();

            // Establecer fecha de hoy por defecto
            datePicker.setValue(LocalDate.now());

            // Cargar datos iniciales
            handleBuscar();

        } catch (Exception e) {
            System.err.println("Error al inicializar gestión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarFiltros() {
        // Cargar trabajadores
        try {
            List<Trabajador> trabajadores = trabajadorDAO.findAll();
            comboTrabajador.getItems().clear();
            comboTrabajador.getItems().add(null); // Opción "Todos"
            comboTrabajador.getItems().addAll(trabajadores);
        } catch (Exception e) {
            System.err.println("Error cargando trabajadores: " + e.getMessage());
        }

        // Cargar departamentos
        try {
            List<Departamento> departamentos = departamentoDAO.findAll();
            comboDepartamento.getItems().clear();
            comboDepartamento.getItems().add(null); // Opción "Todos"
            comboDepartamento.getItems().addAll(departamentos);
        } catch (Exception e) {
            System.err.println("Error cargando departamentos: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        colTrabajador.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreTrabajador()));
        colDepartamento.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartamento()));
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFecha()));
        colEntrada.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHoraEntrada()));
        colSalida.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHoraSalida()));
        colTotalHoras.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHorasTrabajadas()));
        colClima.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClima()));
        colTemperatura.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTemperatura()));

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁️");
            private final HBox hbox = new HBox(5, btnVer);

            {
                btnVer.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 10;");
                btnVer.setOnAction(event -> {
                    FichajeResumen resumen = getTableView().getItems().get(getIndex());
                    verDetalle(resumen);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    @FXML
    private void handleBuscar() {
        try {
            LocalDate fechaSeleccionada = datePicker.getValue();
            if (fechaSeleccionada == null) fechaSeleccionada = LocalDate.now();
            final LocalDate fecha = fechaSeleccionada;

            Trabajador trabajadorSeleccionado = comboTrabajador.getValue();
            Departamento departamentoSeleccionado = comboDepartamento.getValue();

            // Obtener fichajes
            List<Fichaje> fichajes;
            if (trabajadorSeleccionado != null) {
                fichajes = fichajeDAO.findByTrabajadorAndFecha(trabajadorSeleccionado, fecha);
            } else {
                fichajes = fichajeDAO.findByFecha(fecha);
            }

            fichajesTotales = fichajes;

            // Filtrar por departamento si está seleccionado
            if (departamentoSeleccionado != null) {
                fichajes = fichajes.stream()
                        .filter(f -> f.getTrabajador().getDepartamento() != null &&
                                    f.getTrabajador().getDepartamento().getId().equals(departamentoSeleccionado.getId()))
                        .toList();
            }

            // Agrupar por trabajador para crear resumen
            Map<Long, FichajeResumen> resumenMap = new LinkedHashMap<>();

            for (Fichaje f : fichajes) {
                Long trabId = f.getTrabajador().getId();
                FichajeResumen resumen = resumenMap.computeIfAbsent(trabId,
                        k -> new FichajeResumen(f.getTrabajador(), fecha));

                if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA) {
                    resumen.setHoraEntrada(f.getHoraEntrada());
                    resumen.setClima(f.getClima());
                    resumen.setTemperaturaVal(f.getTemperatura());
                } else {
                    resumen.setHoraSalida(f.getHoraSalida());
                }
            }

            ObservableList<FichajeResumen> datos = FXCollections.observableArrayList(resumenMap.values());
            tableFichajes.setItems(datos);

            // Actualizar estadísticas
            actualizarEstadisticas(fichajes);

        } catch (Exception e) {
            System.err.println("Error al buscar fichajes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas(List<Fichaje> fichajes) {
        long entradas = fichajes.stream().filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA).count();
        long salidas = fichajes.stream().filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA).count();

        lblTotalFichajes.setText(String.valueOf(fichajes.size()));
        lblTotalEntradas.setText(String.valueOf(entradas));
        lblTotalSalidas.setText(String.valueOf(salidas));

        // Calcular horas totales
        double horasTotales = 0;
        Map<Long, LocalDateTime> entradasMap = new HashMap<>();

        for (Fichaje f : fichajes) {
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null) {
                entradasMap.put(f.getTrabajador().getId(), f.getHoraEntrada());
            }
        }

        for (Fichaje f : fichajes) {
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA && f.getHoraSalida() != null) {
                LocalDateTime entrada = entradasMap.get(f.getTrabajador().getId());
                if (entrada != null) {
                    horasTotales += java.time.Duration.between(entrada, f.getHoraSalida()).toMinutes() / 60.0;
                }
            }
        }

        lblTotalHoras.setText(String.format("%.1fh", horasTotales));
    }

    @FXML
    private void handleLimpiar() {
        datePicker.setValue(LocalDate.now());
        comboTrabajador.setValue(null);
        comboDepartamento.setValue(null);
        handleBuscar();
    }

    @FXML
    private void handleExportar() {
        if (fichajesTotales.isEmpty()) {
            mostrarMensaje("No hay datos para exportar", Alert.AlertType.WARNING);
            return;
        }

        mostrarMensaje("Función de exportar en desarrollo", Alert.AlertType.INFORMATION);
    }


    private void verDetalle(FichajeResumen resumen) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del Fichaje");
        alert.setHeaderText("📋 " + resumen.getNombreTrabajador());

        StringBuilder sb = new StringBuilder();
        sb.append("📅 Fecha: ").append(resumen.getFecha()).append("\n");
        sb.append("⬇️ Entrada: ").append(resumen.getHoraEntrada()).append("\n");
        sb.append("⬆️ Salida: ").append(resumen.getHoraSalida()).append("\n");
        sb.append("⏱️ Horas: ").append(resumen.getHorasTrabajadas()).append("\n");
        sb.append("🏢 Departamento: ").append(resumen.getDepartamento()).append("\n");
        if (resumen.getClima() != null && !resumen.getClima().equals("-")) {
            sb.append("🌤️ Clima: ").append(resumen.getClima()).append("\n");
            sb.append("🌡️ Temperatura: ").append(resumen.getTemperatura()).append("\n");
        }

        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.INFORMATION ? "Información" : "Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase interna para el resumen de fichajes
    public static class FichajeResumen {
        private Trabajador trabajador;
        private LocalDate fecha;
        private LocalDateTime horaEntrada;
        private LocalDateTime horaSalida;
        private String clima;
        private Double temperaturaVal;

        public FichajeResumen(Trabajador trabajador, LocalDate fecha) {
            this.trabajador = trabajador;
            this.fecha = fecha;
        }

        public String getNombreTrabajador() {
            return trabajador.getNombreCompleto();
        }

        public String getDepartamento() {
            return trabajador.getDepartamento() != null ? trabajador.getDepartamento().getNombre() : "-";
        }

        public String getFecha() {
            return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public String getHoraEntrada() {
            return horaEntrada != null ? horaEntrada.format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--";
        }

        public String getHoraSalida() {
            return horaSalida != null ? horaSalida.format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--";
        }

        public String getHorasTrabajadas() {
            if (horaEntrada != null && horaSalida != null) {
                long minutos = java.time.Duration.between(horaEntrada, horaSalida).toMinutes();
                return String.format("%dh %02dm", minutos / 60, minutos % 60);
            }
            return "-";
        }

        public String getClima() {
            return clima != null ? clima : "-";
        }

        public String getTemperatura() {
            return temperaturaVal != null ? String.format("%.1f°C", temperaturaVal) : "-";
        }

        public void setHoraEntrada(LocalDateTime horaEntrada) {
            this.horaEntrada = horaEntrada;
        }

        public void setHoraSalida(LocalDateTime horaSalida) {
            this.horaSalida = horaSalida;
        }

        public void setClima(String clima) {
            this.clima = clima;
        }

        public void setTemperaturaVal(Double temperaturaVal) {
            this.temperaturaVal = temperaturaVal;
        }
    }
}

