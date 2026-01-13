package com.taller.controller;

import com.taller.model.ItemInventario;
import com.taller.service.InventarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

public class InventarioController {
    
    @FXML private TableView<ItemInventario> tableInventario;
    @FXML private TableColumn<ItemInventario, String> colCodigo;
    @FXML private TableColumn<ItemInventario, String> colNombre;
    @FXML private TableColumn<ItemInventario, String> colCategoria;
    @FXML private TableColumn<ItemInventario, Integer> colStock;
    @FXML private TableColumn<ItemInventario, Integer> colStockMin;
    @FXML private TableColumn<ItemInventario, Double> colPrecioCompra;
    @FXML private TableColumn<ItemInventario, Double> colPrecioVenta;
    
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextArea txtDescripcion;
    @FXML private Spinner<Integer> spnStock;
    @FXML private Spinner<Integer> spnStockMin;
    @FXML private TextField txtPrecioCompra;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtProveedor;
    @FXML private Label lblTotalItems;
    @FXML private Label lblValorTotal;
    @FXML private Label lblStockBajo;
    @FXML private Button btnGuardar;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    
    private final InventarioService inventarioService = new InventarioService();
    private final ObservableList<ItemInventario> inventarioData = FXCollections.observableArrayList();
    private ItemInventario itemSeleccionado;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarFormulario();
        cargarInventario();
        actualizarEstadisticas();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        
        // Resaltar items con stock bajo
        colStock.setCellFactory(column -> new TableCell<ItemInventario, Integer>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(stock));
                    ItemInventario item = getTableView().getItems().get(getIndex());
                    if (item != null && item.necesitaReposicion()) {
                        setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        tableInventario.setItems(inventarioData);
        
        tableInventario.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetallesItem(newSelection);
                }
            }
        );
    }

    private void configurarFormulario() {
        cmbCategoria.setItems(FXCollections.observableArrayList(
            "repuesto", "herramienta", "consumible", "accesorio"
        ));
        cmbCategoria.setValue("repuesto");
        
        SpinnerValueFactory<Integer> stockFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        spnStock.setValueFactory(stockFactory);
        
        SpinnerValueFactory<Integer> stockMinFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 5);
        spnStockMin.setValueFactory(stockMinFactory);
    }

    private void cargarInventario() {
        inventarioData.clear();
        inventarioData.addAll(inventarioService.obtenerTodos());
    }

    private void actualizarEstadisticas() {
        long total = inventarioService.contarTotal();
        lblTotalItems.setText(String.valueOf(total));
        
        double valorTotal = inventarioService.obtenerValorTotalInventario();
        lblValorTotal.setText(String.format("%.2f€", valorTotal));
        
        long stockBajo = inventarioService.contarBajoStock();
        lblStockBajo.setText(String.valueOf(stockBajo));
    }

    private void mostrarDetallesItem(ItemInventario item) {
        itemSeleccionado = item;
        txtCodigo.setText(item.getCodigo());
        txtNombre.setText(item.getNombre());
        cmbCategoria.setValue(item.getCategoria());
        txtDescripcion.setText(item.getDescripcion());
        spnStock.getValueFactory().setValue(item.getStockActual());
        spnStockMin.getValueFactory().setValue(item.getStockMinimo());
        txtPrecioCompra.setText(String.valueOf(item.getPrecioCompra()));
        txtPrecioVenta.setText(String.valueOf(item.getPrecioVenta()));
        txtProveedor.setText(item.getProveedor());
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleBuscar() {
        String texto = txtBuscar.getText();
        if (texto == null || texto.trim().isEmpty()) {
            cargarInventario();
        } else {
            inventarioData.clear();
            inventarioData.addAll(inventarioService.buscar(texto));
        }
    }

    @FXML
    private void handleFiltrarCategoria() {
        String categoria = cmbCategoria.getValue();
        if (categoria != null) {
            inventarioData.clear();
            inventarioData.addAll(inventarioService.buscarPorCategoria(categoria));
        }
    }

    @FXML
    private void handleVerBajoStock() {
        inventarioData.clear();
        inventarioData.addAll(inventarioService.obtenerBajoStock());
    }

    @FXML
    private void handleNuevo() {
        limpiarFormulario();
        itemSeleccionado = null;
        btnGuardar.setText("Guardar");
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        ItemInventario item = itemSeleccionado != null ? itemSeleccionado : new ItemInventario();
        item.setCodigo(txtCodigo.getText());
        item.setNombre(txtNombre.getText());
        item.setCategoria(cmbCategoria.getValue());
        item.setDescripcion(txtDescripcion.getText());
        item.setStockActual(spnStock.getValue());
        item.setStockMinimo(spnStockMin.getValue());
        item.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText()));
        item.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText()));
        item.setProveedor(txtProveedor.getText());

        boolean exito = itemSeleccionado != null ? 
            inventarioService.actualizar(item) : 
            inventarioService.crear(item);

        if (exito) {
            mostrarMensaje("Item guardado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarInventario();
            actualizarEstadisticas();
        } else {
            mostrarMensaje("Error al guardar el item", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEliminar() {
        if (itemSeleccionado == null) {
            mostrarMensaje("Seleccione un item para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este item?");
        confirmacion.setContentText(itemSeleccionado.getNombre());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            if (inventarioService.eliminar(itemSeleccionado.getId())) {
                mostrarMensaje("Item eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarInventario();
                actualizarEstadisticas();
            } else {
                mostrarMensaje("Error al eliminar el item", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleAjustarStock() {
        if (itemSeleccionado == null) {
            mostrarMensaje("Seleccione un item", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(itemSeleccionado.getStockActual()));
        dialog.setTitle("Ajustar Stock");
        dialog.setHeaderText("Ajustar stock de: " + itemSeleccionado.getNombre());
        dialog.setContentText("Nuevo stock:");

        dialog.showAndWait().ifPresent(resultado -> {
            try {
                int nuevoStock = Integer.parseInt(resultado);
                if (inventarioService.actualizarStock(itemSeleccionado.getId(), nuevoStock)) {
                    mostrarMensaje("Stock actualizado", Alert.AlertType.INFORMATION);
                    cargarInventario();
                    actualizarEstadisticas();
                }
            } catch (NumberFormatException e) {
                mostrarMensaje("Stock inválido", Alert.AlertType.ERROR);
            }
        });
    }

    private boolean validarFormulario() {
        if (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) {
            mostrarMensaje("El código es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("El nombre es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        try {
            Double.parseDouble(txtPrecioCompra.getText());
            Double.parseDouble(txtPrecioVenta.getText());
        } catch (NumberFormatException e) {
            mostrarMensaje("Los precios deben ser números válidos", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue("repuesto");
        txtDescripcion.clear();
        spnStock.getValueFactory().setValue(0);
        spnStockMin.getValueFactory().setValue(5);
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        txtProveedor.clear();
        itemSeleccionado = null;
        tableInventario.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
