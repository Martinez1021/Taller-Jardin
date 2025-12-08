package com.empresa.fichador.controller;

import com.empresa.fichador.dao.DepartamentoDAO;
import com.empresa.fichador.dao.HorarioDAO;
import com.empresa.fichador.dao.TrabajadorDAO;
import com.empresa.fichador.model.Departamento;
import com.empresa.fichador.model.Horario;
import com.empresa.fichador.model.Trabajador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class GestionTrabajadoresController {

    @FXML private TableView<Trabajador> tableTrabajadores;
    @FXML private TableColumn<Trabajador, Long> colId;
    @FXML private TableColumn<Trabajador, Void> colFoto;
    @FXML private TableColumn<Trabajador, String> colNombre;
    @FXML private TableColumn<Trabajador, String> colApellidos;
    @FXML private TableColumn<Trabajador, String> colTarjeta;
    @FXML private TableColumn<Trabajador, String> colDepartamento;
    @FXML private TableColumn<Trabajador, String> colCargo;
    @FXML private TableColumn<Trabajador, String> colEmail;
    @FXML private TableColumn<Trabajador, String> colTelefono;
    @FXML private TableColumn<Trabajador, String> colEstado;
    @FXML private TableColumn<Trabajador, Void> colAcciones;

    @FXML private TextField txtBuscar;
    @FXML private Label lblContador;

    @FXML private VBox formulario;
    @FXML private Label lblFormularioTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private TextField txtTarjeta;
    @FXML private TextField txtPin;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCargo;
    @FXML private ComboBox<Departamento> comboDepartamento;
    @FXML private ComboBox<Horario> comboHorario;

    private TrabajadorDAO trabajadorDAO;
    private DepartamentoDAO departamentoDAO;
    private HorarioDAO horarioDAO;
    private Trabajador trabajadorEditando;
    private boolean modoEdicion = false;
    private List<Trabajador> todosTrabajadores;

    @FXML
    public void initialize() {
        try {
            trabajadorDAO = new TrabajadorDAO();
            departamentoDAO = new DepartamentoDAO();
            horarioDAO = new HorarioDAO();

            configurarTabla();
            cargarCombos();
            cargarTrabajadores();
        } catch (Exception e) {
            System.err.println("Error al inicializar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("apellidos"));
        colTarjeta.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("numeroTarjeta"));

        colDepartamento.setCellValueFactory(cellData -> {
            Departamento dept = cellData.getValue().getDepartamento();
            return new SimpleStringProperty(dept != null ? dept.getNombre() : "-");
        });

        colCargo.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCargo() != null ? cellData.getValue().getCargo() : "-"));

        colEmail.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getEmail() != null ? cellData.getValue().getEmail() : "-"));

        colTelefono.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTelefono() != null ? cellData.getValue().getTelefono() : "-"));

        // Columna de foto/avatar
        colFoto.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Trabajador t = getTableRow().getItem();
                    StackPane avatar = new StackPane();
                    avatar.setStyle("-fx-background-color: #6366f1; -fx-background-radius: 100; -fx-min-width: 35; -fx-min-height: 35; -fx-max-width: 35; -fx-max-height: 35;");
                    Label lblIniciales = new Label(t.getIniciales());
                    lblIniciales.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: white;");
                    avatar.getChildren().add(lblIniciales);
                    setGraphic(avatar);
                }
            }
        });

        // Columna de estado
        colEstado.setCellValueFactory(cellData -> {
            boolean activo = cellData.getValue().isActivo();
            return new SimpleStringProperty(activo ? "✅ Activo" : "❌ Inactivo");
        });

        colEstado.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Activo")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: 700;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 700;");
                    }
                }
            }
        });

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️");
            private final Button btnEliminar = new Button("🗑️");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);

            {
                hbox.setAlignment(Pos.CENTER);
                btnEditar.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 8;");
                btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 8;");

                btnEditar.setOnAction(event -> {
                    Trabajador trabajador = getTableView().getItems().get(getIndex());
                    editarTrabajador(trabajador);
                });

                btnEliminar.setOnAction(event -> {
                    Trabajador trabajador = getTableView().getItems().get(getIndex());
                    eliminarTrabajador(trabajador);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void cargarCombos() {
        try {
            // Departamentos
            List<Departamento> departamentos = departamentoDAO.findAll();
            comboDepartamento.getItems().clear();
            comboDepartamento.getItems().add(null);
            comboDepartamento.getItems().addAll(departamentos);

            // Horarios
            List<Horario> horarios = horarioDAO.findAll();
            comboHorario.getItems().clear();
            comboHorario.getItems().add(null);
            comboHorario.getItems().addAll(horarios);
        } catch (Exception e) {
            System.err.println("Error cargando combos: " + e.getMessage());
        }
    }

    private void cargarTrabajadores() {
        try {
            todosTrabajadores = trabajadorDAO.findAll();
            ObservableList<Trabajador> trabajadores = FXCollections.observableArrayList(todosTrabajadores);
            tableTrabajadores.setItems(trabajadores);
            actualizarContador();
        } catch (Exception e) {
            System.err.println("Error al cargar trabajadores: " + e.getMessage());
        }
    }

    private void actualizarContador() {
        int total = tableTrabajadores.getItems().size();
        lblContador.setText(total + " trabajador" + (total != 1 ? "es" : ""));
    }

    @FXML
    private void handleBuscar() {
        String filtro = txtBuscar.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            tableTrabajadores.setItems(FXCollections.observableArrayList(todosTrabajadores));
        } else {
            List<Trabajador> filtrados = todosTrabajadores.stream()
                    .filter(t -> t.getNombre().toLowerCase().contains(filtro) ||
                                t.getApellidos().toLowerCase().contains(filtro) ||
                                t.getNumeroTarjeta().contains(filtro) ||
                                (t.getDepartamento() != null && t.getDepartamento().getNombre().toLowerCase().contains(filtro)))
                    .toList();
            tableTrabajadores.setItems(FXCollections.observableArrayList(filtrados));
        }
        actualizarContador();
    }

    @FXML
    private void handleNuevo() {
        modoEdicion = false;
        trabajadorEditando = null;
        lblFormularioTitulo.setText("➕ Nuevo Trabajador");
        limpiarFormulario();
        mostrarFormulario(true);
    }

    private void editarTrabajador(Trabajador trabajador) {
        modoEdicion = true;
        trabajadorEditando = trabajador;
        lblFormularioTitulo.setText("✏️ Editar Trabajador: " + trabajador.getNombreCompleto());

        txtNombre.setText(trabajador.getNombre());
        txtApellidos.setText(trabajador.getApellidos());
        txtDni.setText(trabajador.getDni() != null ? trabajador.getDni() : "");
        txtTarjeta.setText(trabajador.getNumeroTarjeta());
        txtPin.setText(trabajador.getPin());
        txtEmail.setText(trabajador.getEmail() != null ? trabajador.getEmail() : "");
        txtTelefono.setText(trabajador.getTelefono() != null ? trabajador.getTelefono() : "");
        txtCargo.setText(trabajador.getCargo() != null ? trabajador.getCargo() : "");
        comboDepartamento.setValue(trabajador.getDepartamento());
        comboHorario.setValue(trabajador.getHorario());

        mostrarFormulario(true);
    }

    private void eliminarTrabajador(Trabajador trabajador) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("⚠️ ¿Eliminar trabajador?");
        alert.setContentText("Se eliminará a " + trabajador.getNombreCompleto() + " y todos sus fichajes.\n\nEsta acción no se puede deshacer.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    trabajadorDAO.delete(trabajador);
                    cargarTrabajadores();
                    mostrarExito("Trabajador eliminado correctamente");
                } catch (Exception e) {
                    mostrarError("Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String tarjeta = txtTarjeta.getText().trim();
        String pin = txtPin.getText().trim();

        // Validaciones
        if (nombre.isEmpty() || apellidos.isEmpty() || tarjeta.isEmpty() || pin.isEmpty()) {
            mostrarError("Los campos Nombre, Apellidos, Tarjeta y PIN son obligatorios");
            return;
        }

        if (pin.length() != 4 || !pin.matches("\\d+")) {
            mostrarError("El PIN debe ser de 4 dígitos numéricos");
            return;
        }

        try {
            Trabajador trabajador;
            if (modoEdicion && trabajadorEditando != null) {
                trabajador = trabajadorEditando;
            } else {
                trabajador = new Trabajador();
            }

            trabajador.setNombre(nombre);
            trabajador.setApellidos(apellidos);
            trabajador.setNumeroTarjeta(tarjeta);
            trabajador.setPin(pin);
            trabajador.setDni(txtDni.getText().trim().isEmpty() ? null : txtDni.getText().trim());
            trabajador.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
            trabajador.setTelefono(txtTelefono.getText().trim().isEmpty() ? null : txtTelefono.getText().trim());
            trabajador.setCargo(txtCargo.getText().trim().isEmpty() ? null : txtCargo.getText().trim());
            trabajador.setDepartamento(comboDepartamento.getValue());
            trabajador.setHorario(comboHorario.getValue());

            if (modoEdicion) {
                trabajadorDAO.update(trabajador);
                mostrarExito("Trabajador actualizado correctamente");
            } else {
                trabajadorDAO.save(trabajador);
                mostrarExito("Trabajador creado correctamente");
            }

            cargarTrabajadores();
            handleCancelar();

        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        mostrarFormulario(false);
        limpiarFormulario();
    }

    private void mostrarFormulario(boolean visible) {
        formulario.setVisible(visible);
        formulario.setManaged(visible);
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellidos.clear();
        txtDni.clear();
        txtTarjeta.clear();
        txtPin.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtCargo.clear();
        comboDepartamento.setValue(null);
        comboHorario.setValue(null);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("✅ " + mensaje);
        alert.showAndWait();
    }
}

