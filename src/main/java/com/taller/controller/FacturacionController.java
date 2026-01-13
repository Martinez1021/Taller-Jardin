package com.taller.controller;

import com.taller.model.Factura;
import com.taller.model.ItemFactura;
import com.taller.model.Cliente;
import com.taller.service.FacturaService;
import com.taller.service.ClienteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class FacturacionController {
    
    @FXML private TableView<Factura> tableFacturas;
    @FXML private TableColumn<Factura, String> colNumero;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, LocalDate> colFecha;
    @FXML private TableColumn<Factura, LocalDate> colVencimiento;
    @FXML private TableColumn<Factura, Double> colTotal;
    @FXML private TableColumn<Factura, String> colEstado;
    
    @FXML private TableView<ItemFactura> tableItems;
    @FXML private TableColumn<ItemFactura, String> colConcepto;
    @FXML private TableColumn<ItemFactura, Integer> colCantidad;
    @FXML private TableColumn<ItemFactura, Double> colPrecioUnit;
    @FXML private TableColumn<ItemFactura, Double> colDescuento;
    @FXML private TableColumn<ItemFactura, Double> colTotalItem;
    
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private DatePicker dpFechaEmision;
    @FXML private DatePicker dpFechaVencimiento;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIVA;
    @FXML private Label lblTotal;
    @FXML private Label lblTotalFacturado;
    @FXML private Label lblTotalPagado;
    @FXML private Label lblTotalPendiente;
    @FXML private TextField txtConcepto;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextField txtDescuentoItem;
    @FXML private Button btnNueva;
    @FXML private Button btnGuardar;
    @FXML private Button btnEmitir;
    @FXML private Button btnMarcarPagada;
    
    private final FacturaService facturaService = new FacturaService();
    private final ClienteService clienteService = new ClienteService();
    private final ObservableList<Factura> facturasData = FXCollections.observableArrayList();
    private final ObservableList<ItemFactura> itemsData = FXCollections.observableArrayList();
    private Factura facturaSeleccionada;

    @FXML
    public void initialize() {
        configurarTablas();
        configurarFormulario();
        cargarFacturas();
        actualizarEstadisticas();
    }

    private void configurarTablas() {
        // Tabla de facturas
        colNumero.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumeroFactura()));
        colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClienteNombre()));
        colFecha.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaEmision()));
        colVencimiento.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaVencimiento()));
        colTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotal()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));
        
        tableFacturas.setItems(facturasData);
        
        tableFacturas.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetallesFactura(newSelection);
                }
            }
        );
        
        // Tabla de items
        colConcepto.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getConcepto()));
        colCantidad.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCantidad()));
        colPrecioUnit.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioUnitario()));
        colDescuento.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescuento()));
        colTotalItem.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotal()));
        
        tableItems.setItems(itemsData);
    }

    private void configurarFormulario() {
        cmbEstado.setItems(FXCollections.observableArrayList(
            "borrador", "emitida", "pagada", "vencida", "cancelada"
        ));
        cmbEstado.setValue("borrador");
        
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
            "Todas", "borrador", "emitida", "pagada", "vencida", "cancelada"
        ));
        cmbFiltroEstado.setValue("Todas");
        
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
            "efectivo", "tarjeta", "transferencia", "cheque"
        ));
        
        // Cargar clientes
        ObservableList<Cliente> clientes = FXCollections.observableArrayList(
            clienteService.obtenerTodos()
        );
        cmbCliente.setItems(clientes);
        
        dpFechaEmision.setValue(LocalDate.now());
        dpFechaVencimiento.setValue(LocalDate.now().plusDays(30));
        
        SpinnerValueFactory<Integer> cantidadFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1);
        spnCantidad.setValueFactory(cantidadFactory);
    }

    private void cargarFacturas() {
        facturasData.clear();
        facturasData.addAll(facturaService.obtenerTodas());
    }

    private void actualizarEstadisticas() {
        double totalFacturado = facturaService.obtenerTotalFacturado();
        lblTotalFacturado.setText(String.format("%.2f€", totalFacturado));
        
        double totalPagado = facturaService.obtenerTotalPagado();
        lblTotalPagado.setText(String.format("%.2f€", totalPagado));
        
        double totalPendiente = facturaService.obtenerTotalPendiente();
        lblTotalPendiente.setText(String.format("%.2f€", totalPendiente));
    }

    private void mostrarDetallesFactura(Factura factura) {
        facturaSeleccionada = factura;
        
        // Buscar y seleccionar cliente
        for (Cliente c : cmbCliente.getItems()) {
            if (c.getId() != null && c.getId().equals(factura.getClienteId())) {
                cmbCliente.setValue(c);
                break;
            }
        }
        
        dpFechaEmision.setValue(factura.getFechaEmision());
        dpFechaVencimiento.setValue(factura.getFechaVencimiento());
        cmbEstado.setValue(factura.getEstado());
        cmbMetodoPago.setValue(factura.getMetodoPago());
        txtObservaciones.setText(factura.getObservaciones());
        
        // Cargar items
        itemsData.clear();
        if (factura.getItems() != null) {
            itemsData.addAll(factura.getItems());
        }
        
        actualizarTotales();
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleNueva() {
        limpiarFormulario();
        facturaSeleccionada = new Factura();
        btnGuardar.setText("Guardar");
    }

    @FXML
    private void handleAgregarItem() {
        if (!validarItem()) {
            return;
        }

        ItemFactura item = new ItemFactura();
        item.setConcepto(txtConcepto.getText());
        item.setCantidad(spnCantidad.getValue());
        item.setPrecioUnitario(Double.parseDouble(txtPrecioUnitario.getText()));
        item.setDescuento(txtDescuentoItem.getText().isEmpty() ? 0 : 
            Double.parseDouble(txtDescuentoItem.getText()));
        item.calcularTotal();
        
        itemsData.add(item);
        if (facturaSeleccionada != null) {
            facturaSeleccionada.agregarItem(item);
        }
        
        limpiarFormularioItem();
        actualizarTotales();
    }

    @FXML
    private void handleEliminarItem() {
        ItemFactura item = tableItems.getSelectionModel().getSelectedItem();
        if (item != null) {
            itemsData.remove(item);
            if (facturaSeleccionada != null) {
                facturaSeleccionada.eliminarItem(item);
            }
            actualizarTotales();
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        if (facturaSeleccionada == null) {
            facturaSeleccionada = new Factura();
        }
        
        Cliente cliente = cmbCliente.getValue();
        
        facturaSeleccionada.setClienteId(cliente.getId());
        facturaSeleccionada.setClienteNombre(cliente.getNombre());
        facturaSeleccionada.setFechaEmision(dpFechaEmision.getValue());
        facturaSeleccionada.setFechaVencimiento(dpFechaVencimiento.getValue());
        facturaSeleccionada.setEstado(cmbEstado.getValue());
        facturaSeleccionada.setMetodoPago(cmbMetodoPago.getValue());
        facturaSeleccionada.setObservaciones(txtObservaciones.getText());
        facturaSeleccionada.setItems(itemsData);

        boolean esNueva = facturaSeleccionada.getId() == null;
        boolean exito = esNueva ? 
            facturaService.crear(facturaSeleccionada) : 
            facturaService.actualizar(facturaSeleccionada);

        if (exito) {
            mostrarMensaje("Factura guardada correctamente", Alert.AlertType.INFORMATION);
            cargarFacturas();
            actualizarEstadisticas();
        } else {
            mostrarMensaje("Error al guardar la factura", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEmitir() {
        if (facturaSeleccionada == null || facturaSeleccionada.getId() == null) {
            mostrarMensaje("Guarde la factura primero", Alert.AlertType.WARNING);
            return;
        }

        if (facturaService.actualizarEstado(facturaSeleccionada.getId(), "emitida")) {
            mostrarMensaje("Factura emitida correctamente", Alert.AlertType.INFORMATION);
            cargarFacturas();
            actualizarEstadisticas();
        }
    }

    @FXML
    private void handleMarcarPagada() {
        if (facturaSeleccionada == null) {
            mostrarMensaje("Seleccione una factura", Alert.AlertType.WARNING);
            return;
        }

        if (facturaService.actualizarEstado(facturaSeleccionada.getId(), "pagada")) {
            mostrarMensaje("Factura marcada como pagada", Alert.AlertType.INFORMATION);
            cargarFacturas();
            actualizarEstadisticas();
        }
    }

    @FXML
    private void handleFiltrarPorEstado() {
        String estado = cmbEstado.getValue();
        if (estado != null) {
            facturasData.clear();
            facturasData.addAll(facturaService.obtenerPorEstado(estado));
        }
    }

    @FXML
    private void handleVerVencidas() {
        facturasData.clear();
        facturasData.addAll(facturaService.obtenerVencidas());
    }

    private void actualizarTotales() {
        if (facturaSeleccionada == null) {
            facturaSeleccionada = new Factura();
            facturaSeleccionada.setItems(itemsData);
        }
        
        facturaSeleccionada.calcularTotales();
        
        lblSubtotal.setText(String.format("%.2f€", facturaSeleccionada.getSubtotal()));
        lblIVA.setText(String.format("%.2f€", facturaSeleccionada.getSubtotal() * facturaSeleccionada.getIva()));
        lblTotal.setText(String.format("%.2f€", facturaSeleccionada.getTotal()));
    }

    private boolean validarFormulario() {
        if (cmbCliente.getValue() == null) {
            mostrarMensaje("Seleccione un cliente", Alert.AlertType.WARNING);
            return false;
        }
        if (itemsData.isEmpty()) {
            mostrarMensaje("Agregue al menos un item a la factura", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean validarItem() {
        if (txtConcepto.getText() == null || txtConcepto.getText().trim().isEmpty()) {
            mostrarMensaje("Ingrese el concepto del item", Alert.AlertType.WARNING);
            return false;
        }
        try {
            Double.parseDouble(txtPrecioUnitario.getText());
        } catch (NumberFormatException e) {
            mostrarMensaje("El precio debe ser un número válido", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        cmbCliente.setValue(null);
        dpFechaEmision.setValue(LocalDate.now());
        dpFechaVencimiento.setValue(LocalDate.now().plusDays(30));
        cmbEstado.setValue("borrador");
        cmbMetodoPago.setValue(null);
        txtObservaciones.clear();
        itemsData.clear();
        facturaSeleccionada = null;
        tableFacturas.getSelectionModel().clearSelection();
        actualizarTotales();
    }

    private void limpiarFormularioItem() {
        txtConcepto.clear();
        spnCantidad.getValueFactory().setValue(1);
        txtPrecioUnitario.clear();
        txtDescuentoItem.clear();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
