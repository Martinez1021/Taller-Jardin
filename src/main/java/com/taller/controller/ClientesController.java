package com.taller.controller;

import com.taller.model.Cliente;
import com.taller.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ClientesController {
    
    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colTipo;
    @FXML private TableColumn<Cliente, Double> colSaldo;
    
    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private Button btnGuardar;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    
    private final ClienteService clienteService = new ClienteService();
    private final ObservableList<Cliente> clientesData = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarFormulario();
        cargarClientes();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoCliente"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldoPendiente"));
        
        tableClientes.setItems(clientesData);
        
        tableClientes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetallesCliente(newSelection);
                }
            }
        );
    }

    private void configurarFormulario() {
        cmbTipo.setItems(FXCollections.observableArrayList("particular", "empresa"));
        cmbTipo.setValue("particular");
    }

    private void cargarClientes() {
        clientesData.clear();
        clientesData.addAll(clienteService.obtenerTodos());
    }

    private void mostrarDetallesCliente(Cliente cliente) {
        clienteSeleccionado = cliente;
        txtNombre.setText(cliente.getNombre());
        txtDni.setText(cliente.getDni());
        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail());
        txtDireccion.setText(cliente.getDireccion());
        if (cliente.getTipoCliente() != null) {
            cmbTipo.setValue(cliente.getTipoCliente());
        }
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleBuscar() {
        String texto = txtBuscar.getText();
        if (texto == null || texto.trim().isEmpty()) {
            cargarClientes();
        } else {
            clientesData.clear();
            clientesData.addAll(clienteService.buscar(texto));
        }
    }

    @FXML
    private void handleNuevo() {
        limpiarFormulario();
        clienteSeleccionado = null;
        btnGuardar.setText("Guardar");
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        Cliente cliente = clienteSeleccionado != null ? clienteSeleccionado : new Cliente();
        cliente.setNombre(txtNombre.getText());
        cliente.setDni(txtDni.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setEmail(txtEmail.getText());
        cliente.setDireccion(txtDireccion.getText());
        cliente.setTipoCliente(cmbTipo.getValue());
        
        if (clienteSeleccionado == null) {
            cliente.setFechaRegistro(LocalDate.now());
        }

        boolean exito = clienteSeleccionado != null ? 
            clienteService.actualizar(cliente) : 
            clienteService.crear(cliente);

        if (exito) {
            mostrarMensaje("Cliente guardado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarClientes();
        } else {
            mostrarMensaje("Error al guardar el cliente", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEliminar() {
        if (clienteSeleccionado == null) {
            mostrarMensaje("Seleccione un cliente para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este cliente?");
        confirmacion.setContentText(clienteSeleccionado.getNombre());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            if (clienteService.eliminar(clienteSeleccionado.getId())) {
                mostrarMensaje("Cliente eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarClientes();
            } else {
                mostrarMensaje("Error al eliminar el cliente", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("El nombre es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        cmbTipo.setValue("particular");
        clienteSeleccionado = null;
        tableClientes.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
