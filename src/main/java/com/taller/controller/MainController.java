package com.taller.controller;

import com.taller.model.Maquina;
import com.taller.service.MaquinaService;
import com.taller.service.OdooService;
import com.taller.service.SincronizacionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la ventana principal
 */
public class MainController {

    @FXML private TableView<Maquina> tablaMaquinas;
    @FXML private TableColumn<Maquina, String> colNumeroSerie;
    @FXML private TableColumn<Maquina, String> colTipo;
    @FXML private TableColumn<Maquina, String> colMarca;
    @FXML private TableColumn<Maquina, String> colModelo;
    @FXML private TableColumn<Maquina, String> colCliente;
    @FXML private TableColumn<Maquina, String> colGarantia;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroGarantia;

    @FXML private TextField txtNumeroSerie;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtCliente;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpFechaCompra;
    @FXML private DatePicker dpGarantiaHasta;

    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnNuevaReparacion;
    @FXML private Button btnVerGarantias;
    @FXML private Label lblEstadoOdoo;

    private MaquinaService maquinaService;
    private OdooService odooService;
    private SincronizacionService sincronizacionService;
    private ObservableList<Maquina> listaMaquinas;
    private Maquina maquinaSeleccionada;

    @FXML
    public void initialize() {
        // Inicializar servicios
        maquinaService = new MaquinaService();
        odooService = new OdooService();
        sincronizacionService = new SincronizacionService();
        listaMaquinas = FXCollections.observableArrayList();

        // Configurar estado de Odoo
        if (odooService.isOdooDisponible()) {
            lblEstadoOdoo.setText("● Odoo Conectado");
            lblEstadoOdoo.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            
            // Sincronizar en segundo plano al iniciar
            new Thread(() -> {
                System.out.println("Iniciando autosincronización inicial...");
                sincronizacionService.sincronizarTodo();
            }).start();
            
        } else {
            lblEstadoOdoo.setText("● Odoo Desconectado (Modo MongoDB)");
            lblEstadoOdoo.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
        }

        // Configurar columnas de la tabla
        colNumeroSerie.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNumeroSerie()));
        colTipo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTipo()));
        colMarca.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMarca()));
        colModelo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getModelo()));
        colCliente.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClienteNombre()));
        colGarantia.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().isEnGarantia() ? "✓ Vigente" : "✗ Vencida";
            return new SimpleStringProperty(estado);
        });

        // Estilo para columna de garantía
        colGarantia.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("✓")) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Configurar ComboBox de tipos
        cmbTipo.setItems(FXCollections.observableArrayList(
                "Cortacésped", "Motosierra", "Desbrozadora", "Podadora",
                "Soplador", "Cortasetos", "Motocultor", "Otro"
        ));

        // Configurar filtro de garantía
        cmbFiltroGarantia.setItems(FXCollections.observableArrayList(
                "Todas", "En Garantía", "Garantía Vencida"
        ));
        cmbFiltroGarantia.setValue("Todas");
        cmbFiltroGarantia.setOnAction(e -> aplicarFiltroGarantia());

        // Listener para selección en la tabla
        tablaMaquinas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        maquinaSeleccionada = newValue;
                        cargarDatosMaquina(newValue);
                    }
                }
        );

        // Cargar datos iniciales
        cargarMaquinas();
    }

    private void cargarMaquinas() {
        System.out.println("\n=== CARGANDO MÁQUINAS ===");
        List<Maquina> maquinas = maquinaService.obtenerTodas();
        System.out.println("📋 Máquinas recibidas: " + maquinas.size());

        listaMaquinas.clear();
        listaMaquinas.addAll(maquinas);
        tablaMaquinas.setItems(listaMaquinas);

        System.out.println("✓ Tabla actualizada con " + listaMaquinas.size() + " máquinas");
        System.out.println("=========================\n");
    }

    private void cargarDatosMaquina(Maquina maquina) {
        txtNumeroSerie.setText(maquina.getNumeroSerie());
        txtNumeroSerie.setDisable(true);
        cmbTipo.setValue(maquina.getTipo());
        txtMarca.setText(maquina.getMarca());
        txtModelo.setText(maquina.getModelo());
        txtCliente.setText(maquina.getClienteNombre());
        txtTelefono.setText(maquina.getTelefono());
        txtEmail.setText(maquina.getEmail());
        dpFechaCompra.setValue(maquina.getFechaCompra());
        dpGarantiaHasta.setValue(maquina.getGarantiaHasta());

        btnAgregar.setDisable(true);
        btnActualizar.setDisable(false);
        btnNuevaReparacion.setDisable(false);
        btnVerGarantias.setDisable(false);
    }

    @FXML
    private void buscarMaquina() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarMaquinas();
            return;
        }

        // Buscar por número de serie
        Maquina maquina = maquinaService.buscarPorNumeroSerie(texto);
        if (maquina != null) {
            listaMaquinas.clear();
            listaMaquinas.add(maquina);
            tablaMaquinas.setItems(listaMaquinas);
            return;
        }

        // Buscar por cliente
        List<Maquina> maquinas = maquinaService.buscarPorCliente(texto);
        listaMaquinas.clear();
        listaMaquinas.addAll(maquinas);
        tablaMaquinas.setItems(listaMaquinas);
    }

    @FXML
    private void aplicarFiltroGarantia() {
        String filtro = cmbFiltroGarantia.getValue();
        List<Maquina> todas = maquinaService.obtenerTodas();

        listaMaquinas.clear();
        switch (filtro) {
            case "En Garantía":
                todas.stream().filter(Maquina::isEnGarantia).forEach(listaMaquinas::add);
                break;
            case "Garantía Vencida":
                todas.stream().filter(m -> !m.isEnGarantia()).forEach(listaMaquinas::add);
                break;
            default:
                listaMaquinas.addAll(todas);
        }
        tablaMaquinas.setItems(listaMaquinas);
    }

    @FXML
    private void agregarMaquina() {
        try {
            if (!validarCampos()) return;

            Maquina maquina = new Maquina();
            maquina.setNumeroSerie(txtNumeroSerie.getText().trim());
            maquina.setTipo(cmbTipo.getValue());
            maquina.setMarca(txtMarca.getText().trim());
            maquina.setModelo(txtModelo.getText().trim());
            maquina.setClienteNombre(txtCliente.getText().trim());
            maquina.setTelefono(txtTelefono.getText().trim());
            maquina.setEmail(txtEmail.getText().trim());
            maquina.setFechaCompra(dpFechaCompra.getValue());
            maquina.setGarantiaHasta(dpGarantiaHasta.getValue());

            // Sincronizar cliente con Odoo si está disponible
            if (odooService.isOdooDisponible()) {
                Integer clienteId = odooService.verificarCliente(
                        txtCliente.getText().trim(),
                        txtTelefono.getText().trim(),
                        txtEmail.getText().trim()
                );
                maquina.setClienteId(clienteId);

                // Crear producto en Odoo
                Integer productoId = odooService.crearProductoMaquina(maquina);
                maquina.setOdooProductId(productoId);
            }

            // Guardar en MongoDB
            if (maquinaService.guardar(maquina)) {
                mostrarAlerta("Éxito", "Máquina agregada correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarMaquinas();
            }

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void actualizarMaquina() {
        try {
            if (maquinaSeleccionada == null) {
                mostrarAlerta("Error", "Seleccione una máquina", Alert.AlertType.WARNING);
                return;
            }

            if (!validarCampos()) return;

            maquinaSeleccionada.setTipo(cmbTipo.getValue());
            maquinaSeleccionada.setMarca(txtMarca.getText().trim());
            maquinaSeleccionada.setModelo(txtModelo.getText().trim());
            maquinaSeleccionada.setClienteNombre(txtCliente.getText().trim());
            maquinaSeleccionada.setFechaCompra(dpFechaCompra.getValue());
            maquinaSeleccionada.setGarantiaHasta(dpGarantiaHasta.getValue());

            if (maquinaService.actualizar(maquinaSeleccionada)) {
                mostrarAlerta("Éxito", "Máquina actualizada", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarMaquinas();
            }

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarMaquina() {
        if (maquinaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una máquina", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar máquina?");
        confirmacion.setContentText("Nº Serie: " + maquinaSeleccionada.getNumeroSerie());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (maquinaService.eliminar(maquinaSeleccionada.getNumeroSerie())) {
                mostrarAlerta("Éxito", "Máquina eliminada", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarMaquinas();
            }
        }
    }

    @FXML
    private void nuevaReparacion() {
        if (maquinaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una máquina", Alert.AlertType.WARNING);
            return;
        }

        abrirVentana("/fxml/orden_reparacion.fxml", "Nueva Orden de Reparación", maquinaSeleccionada);
    }

    @FXML
    private void verGarantias() {
        if (maquinaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una máquina", Alert.AlertType.WARNING);
            return;
        }

        abrirVentana("/fxml/garantias.fxml", "Garantías y Reparaciones", maquinaSeleccionada);
    }

    @FXML
    private void sincronizarConOdoo() {
        if (!odooService.isOdooDisponible()) {
            mostrarAlerta("Odoo no disponible",
                    "No se puede sincronizar porque Odoo no está conectado",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Sincronizar con Odoo");
        confirmacion.setHeaderText("¿Iniciar sincronización?");
        confirmacion.setContentText("Se verificarán clientes y productos en Odoo");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            sincronizacionService.sincronizarTodo();
            cargarMaquinas();
            mostrarAlerta("Sincronización completada",
                    "Revisa la consola para más detalles",
                    Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void recargarDatos() {
        System.out.println("🔄 Recargando datos desde MongoDB...");
        cargarMaquinas();
        limpiarFormulario();
        int cantidad = listaMaquinas.size();
        mostrarAlerta("Datos Recargados",
                "✓ Se cargaron " + cantidad + " máquina(s) desde MongoDB",
                Alert.AlertType.INFORMATION);
        System.out.println("✓ Recarga completada: " + cantidad + " máquinas");
    }

    @FXML
    private void limpiarFormulario() {
        txtNumeroSerie.clear();
        txtNumeroSerie.setDisable(false);
        cmbTipo.setValue(null);
        txtMarca.clear();
        txtModelo.clear();
        txtCliente.clear();
        txtTelefono.clear();
        txtEmail.clear();
        dpFechaCompra.setValue(null);
        dpGarantiaHasta.setValue(null);

        maquinaSeleccionada = null;
        tablaMaquinas.getSelectionModel().clearSelection();

        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
        btnNuevaReparacion.setDisable(true);
        btnVerGarantias.setDisable(true);
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNumeroSerie.getText().trim().isEmpty()) errores.append("- Número de serie\n");
        if (cmbTipo.getValue() == null) errores.append("- Tipo\n");
        if (txtMarca.getText().trim().isEmpty()) errores.append("- Marca\n");
        if (txtModelo.getText().trim().isEmpty()) errores.append("- Modelo\n");
        if (txtCliente.getText().trim().isEmpty()) errores.append("- Cliente\n");
        if (dpFechaCompra.getValue() == null) errores.append("- Fecha de compra\n");
        if (dpGarantiaHasta.getValue() == null) errores.append("- Garantía hasta\n");

        if (dpFechaCompra.getValue() != null && dpGarantiaHasta.getValue() != null) {
            if (dpGarantiaHasta.getValue().isBefore(dpFechaCompra.getValue())) {
                errores.append("- La garantía debe ser posterior a la compra\n");
            }
        }

        if (errores.length() > 0) {
            mostrarAlerta("Campos obligatorios", errores.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void abrirVentana(String fxml, String titulo, Maquina maquina) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            if (fxml.contains("garantias")) {
                GarantiasController controller = loader.getController();
                controller.setMaquina(maquina);
            } else if (fxml.contains("orden_reparacion")) {
                OrdenReparacionController controller = loader.getController();
                controller.setMaquina(maquina);
            }

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarMaquinas();

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir ventana: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

