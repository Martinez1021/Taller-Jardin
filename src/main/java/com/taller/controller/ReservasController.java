package com.taller.controller;

import com.taller.model.Reserva;
import com.taller.model.Cliente;
import com.taller.model.Maquina;
import com.taller.service.ReservaService;
import com.taller.service.ClienteService;
import com.taller.service.MaquinaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ReservasController {
    
    @FXML private TableView<Reserva> tableReservas;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, String> colMaquina;
    @FXML private TableColumn<Reserva, LocalDate> colFechaInicio;
    @FXML private TableColumn<Reserva, LocalDate> colFechaFin;
    @FXML private TableColumn<Reserva, String> colEstado;
    @FXML private TableColumn<Reserva, Double> colPrecio;
    
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<Maquina> cmbMaquina;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TextField txtPrecio;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblTotalReservas;
    @FXML private Label lblReservasActivas;
    @FXML private Label lblIngresos;
    @FXML private Button btnGuardar;
    @FXML private Button btnNuevo;
    @FXML private Button btnCancelar;
    
    private final ReservaService reservaService = new ReservaService();
    private final ClienteService clienteService = new ClienteService();
    private final MaquinaService maquinaService = new MaquinaService();
    private final ObservableList<Reserva> reservasData = FXCollections.observableArrayList();
    private Reserva reservaSeleccionada;

    @FXML
    public void initialize() {
        try {
            configurarTabla();
            configurarFormulario();
            cargarDatos();
            actualizarEstadisticas();
        } catch (Exception e) {
            System.err.println("Error inicializando ReservasController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClienteNombre()));
        colMaquina.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaquinaNombre()));
        colFechaInicio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaInicio()));
        colFechaFin.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaFin()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioAlquiler()));
        
        // Colorear filas según estado
        tableReservas.setRowFactory(tv -> new TableRow<Reserva>() {
            @Override
            protected void updateItem(Reserva reserva, boolean empty) {
                super.updateItem(reserva, empty);
                if (empty || reserva == null) {
                    setStyle("");
                } else {
                    switch (reserva.getEstado()) {
                        case "pendiente":
                            setStyle("-fx-background-color: #fff3e0;");
                            break;
                        case "confirmada":
                            setStyle("-fx-background-color: #e8f5e9;");
                            break;
                        case "en_curso":
                            setStyle("-fx-background-color: #e3f2fd;");
                            break;
                        case "completada":
                            setStyle("-fx-background-color: #f5f5f5;");
                            break;
                        case "cancelada":
                            setStyle("-fx-background-color: #ffebee;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        tableReservas.setItems(reservasData);
        
        tableReservas.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetallesReserva(newSelection);
                }
            }
        );
    }

    private void configurarFormulario() {
        cmbEstado.setItems(FXCollections.observableArrayList(
            "pendiente", "confirmada", "en_curso", "completada", "cancelada"
        ));
        cmbEstado.setValue("pendiente");
        
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
            "Todos", "pendiente", "confirmada", "en_curso", "completada", "cancelada"
        ));
        cmbFiltroEstado.setValue("Todos");
        
        // Cargar clientes con converter
        try {
            ObservableList<Cliente> clientes = FXCollections.observableArrayList(
                clienteService.obtenerTodos()
            );
            cmbCliente.setItems(clientes);
            cmbCliente.setConverter(new javafx.util.StringConverter<Cliente>() {
                @Override
                public String toString(Cliente cliente) {
                    return cliente != null ? cliente.toString() : "";
                }
                
                @Override
                public Cliente fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            System.err.println("Error cargando clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Cargar máquinas con converter
        try {
            ObservableList<Maquina> maquinas = FXCollections.observableArrayList(
                maquinaService.obtenerTodas()
            );
            cmbMaquina.setItems(maquinas);
            cmbMaquina.setConverter(new javafx.util.StringConverter<Maquina>() {
                @Override
                public String toString(Maquina maquina) {
                    return maquina != null ? maquina.toString() : "";
                }
                
                @Override
                public Maquina fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            System.err.println("Error cargando máquinas: " + e.getMessage());
            e.printStackTrace();
        }
        
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaFin.setValue(LocalDate.now().plusDays(7));
    }

    private void cargarDatos() {
        try {
            reservasData.clear();
            reservasData.addAll(reservaService.obtenerTodas());
        } catch (Exception e) {
            System.err.println("Error cargando reservas: " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error al cargar las reservas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarEstadisticas() {
        try {
            lblTotalReservas.setText(String.valueOf(reservasData.size()));
            
            long activas = reservaService.contarPorEstado("confirmada") + 
                           reservaService.contarPorEstado("en_curso");
            lblReservasActivas.setText(String.valueOf(activas));
            
            double ingresos = reservaService.obtenerIngresosTotales();
            lblIngresos.setText(String.format("%.2f€", ingresos));
        } catch (Exception e) {
            System.err.println("Error actualizando estadísticas: " + e.getMessage());
            lblTotalReservas.setText("0");
            lblReservasActivas.setText("0");
            lblIngresos.setText("0.00€");
        }
    }

    private void mostrarDetallesReserva(Reserva reserva) {
        reservaSeleccionada = reserva;
        
        // Buscar y seleccionar cliente
        for (Cliente c : cmbCliente.getItems()) {
            if (c.getId() != null && c.getId().equals(reserva.getClienteId())) {
                cmbCliente.setValue(c);
                break;
            }
        }
        
        // Buscar y seleccionar máquina
        for (Maquina m : cmbMaquina.getItems()) {
            if (m.getNumeroSerie().equals(reserva.getMaquinaId())) {
                cmbMaquina.setValue(m);
                break;
            }
        }
        
        dpFechaInicio.setValue(reserva.getFechaInicio());
        dpFechaFin.setValue(reserva.getFechaFin());
        cmbEstado.setValue(reserva.getEstado());
        txtPrecio.setText(String.valueOf(reserva.getPrecioAlquiler()));
        txtObservaciones.setText(reserva.getObservaciones());
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleNuevo() {
        limpiarFormulario();
        reservaSeleccionada = null;
        btnGuardar.setText("Guardar");
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        Reserva reserva = reservaSeleccionada != null ? reservaSeleccionada : new Reserva();
        
        Cliente cliente = cmbCliente.getValue();
        Maquina maquina = cmbMaquina.getValue();
        
        reserva.setClienteId(cliente.getId());
        reserva.setClienteNombre(cliente.getNombre());
        reserva.setMaquinaId(maquina.getNumeroSerie());
        reserva.setMaquinaNombre(maquina.getTipo() + " " + maquina.getModelo());
        reserva.setFechaInicio(dpFechaInicio.getValue());
        reserva.setFechaFin(dpFechaFin.getValue());
        reserva.setEstado(cmbEstado.getValue());
        reserva.setPrecioAlquiler(Double.parseDouble(txtPrecio.getText().trim()));
        reserva.setObservaciones(txtObservaciones.getText() != null ? txtObservaciones.getText() : "");

        boolean exito = reservaSeleccionada != null ? 
            reservaService.actualizar(reserva) : 
            reservaService.crear(reserva);

        if (exito) {
            mostrarMensaje("Reserva guardada correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatos();
            actualizarEstadisticas();
        } else {
            mostrarMensaje("Error al guardar la reserva", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelar() {
        if (reservaSeleccionada == null) {
            mostrarMensaje("Seleccione una reserva", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText("¿Está seguro de cancelar esta reserva?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            if (reservaService.actualizarEstado(reservaSeleccionada.getId(), "cancelada")) {
                mostrarMensaje("Reserva cancelada", Alert.AlertType.INFORMATION);
                cargarDatos();
                actualizarEstadisticas();
            }
        }
    }

    @FXML
    private void handleVerificarDisponibilidad() {
        if (cmbMaquina.getValue() == null || dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            mostrarMensaje("Seleccione máquina y fechas", Alert.AlertType.WARNING);
            return;
        }

        boolean disponible = reservaService.maquinaDisponible(
            cmbMaquina.getValue().getNumeroSerie(),
            dpFechaInicio.getValue(),
            dpFechaFin.getValue()
        );

        Alert alert = new Alert(disponible ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        alert.setContentText(disponible ? 
            "✓ Máquina disponible para las fechas seleccionadas" : 
            "✗ Máquina NO disponible para las fechas seleccionadas");
        alert.showAndWait();
    }

    @FXML
    private void handleFiltrarPorEstado() {
        String filtro = cmbFiltroEstado.getValue();
        reservasData.clear();
        if (filtro == null || filtro.equals("Todos")) {
            reservasData.addAll(reservaService.obtenerTodas());
        } else {
            reservasData.addAll(reservaService.obtenerPorEstado(filtro));
        }
        actualizarEstadisticas();
    }

    private boolean validarFormulario() {
        if (cmbCliente.getValue() == null) {
            mostrarMensaje("Seleccione un cliente", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbMaquina.getValue() == null) {
            mostrarMensaje("Seleccione una máquina", Alert.AlertType.WARNING);
            return false;
        }
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            mostrarMensaje("Seleccione las fechas", Alert.AlertType.WARNING);
            return false;
        }
        if (dpFechaFin.getValue().isBefore(dpFechaInicio.getValue())) {
            mostrarMensaje("La fecha fin debe ser posterior a la fecha inicio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) {
            mostrarMensaje("Ingrese el precio del alquiler", Alert.AlertType.WARNING);
            return false;
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio < 0) {
                mostrarMensaje("El precio no puede ser negativo", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("El precio debe ser un número válido (use punto para decimales)", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        cmbCliente.setValue(null);
        cmbMaquina.setValue(null);
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaFin.setValue(LocalDate.now().plusDays(7));
        cmbEstado.setValue("pendiente");
        txtPrecio.clear();
        txtObservaciones.clear();
        reservaSeleccionada = null;
        tableReservas.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
