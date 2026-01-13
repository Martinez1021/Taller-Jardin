package com.taller.controller;

import com.taller.model.Maquina;
import com.taller.model.Reparacion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.taller.service.MaquinaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para la ventana de garantías y reparaciones
 */
public class GarantiasController {
    
    // Servicio para conectar con MongoDB
    private final MaquinaService maquinaService = new MaquinaService();

    @FXML private TableView<Maquina> tablaMaquinas;
    @FXML private TableColumn<Maquina, String> colModeloMaquina;
    @FXML private TableColumn<Maquina, String> colClienteMaquina;
    @FXML private TableColumn<Maquina, String> colEstadoGarantiaList;
    @FXML private TextField txtBuscarMaquina;

    @FXML private Label lblNumeroSerie;
    @FXML private Label lblTipo;
    @FXML private Label lblMarca;
    @FXML private Label lblModelo;
    @FXML private Label lblCliente;
    @FXML private Label lblFechaCompra;
    @FXML private Label lblGarantiaHasta;
    @FXML private Label lblDiasRestantes;
    @FXML private HBox hboxEstadoGarantia;
    @FXML private Label lblEstadoGarantia;

    @FXML private TableView<Reparacion> tablaReparaciones;
    @FXML private TableColumn<Reparacion, String> colFecha;
    @FXML private TableColumn<Reparacion, String> colDescripcion;
    @FXML private TableColumn<Reparacion, String> colTecnico;
    @FXML private TableColumn<Reparacion, String> colCoste;
    @FXML private TableColumn<Reparacion, String> colOdooId;

    @FXML private ProgressBar progressGarantia;

    private Maquina maquina;
    private ObservableList<Reparacion> listaReparaciones;
    private ObservableList<Maquina> listaMaquinas;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Inicializar listas
        listaReparaciones = FXCollections.observableArrayList();
        listaMaquinas = FXCollections.observableArrayList();

        // Configurar tabla de máquinas (Izquierda)
        configurarTablaMaquinas();
        
        // Agregar listener para cambiar detalles al seleccionar máquina
        tablaMaquinas.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    setMaquina(newValue);
                }
            }
        );

        // Configurar tabla de reparaciones (Derecha/Detalles)
        colFecha.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFecha();
            return new SimpleStringProperty(fecha != null ? fecha.format(formatter) : "");
        });

        colDescripcion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescripcion()));

        colTecnico.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTecnico()));

        colCoste.setCellValueFactory(cellData -> {
            Double coste = cellData.getValue().getCoste();
            return new SimpleStringProperty(coste != null ? String.format("€%.2f", coste) : "€0.00");
        });

        colOdooId.setCellValueFactory(cellData -> {
            Integer odooId = cellData.getValue().getOdooRepairId();
            return new SimpleStringProperty(odooId != null ? "#" + odooId : "-");
        });
        
        // Cargar datos reales de MongoDB
        cargarDatosReales();
        
        // Si no hay datos en la BBDD, cargar demo para que no se vea vacío
        if (listaMaquinas.isEmpty()) {
            cargarDatosDemo();
        }
        
        // Seleccionar el primero por defecto
        if (!listaMaquinas.isEmpty()) {
            tablaMaquinas.getSelectionModel().selectFirst();
        }
        
        // Filtro de búsqueda
        txtBuscarMaquina.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarMaquinas(newValue);
        });
    }
    
    private void configurarTablaMaquinas() {
        colModeloMaquina.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getModelo()));
            
        colClienteMaquina.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClienteNombre()));
            
        colEstadoGarantiaList.setCellValueFactory(cellData -> {
            boolean enGarantia = cellData.getValue().isEnGarantia();
            return new SimpleStringProperty(enGarantia ? "✅" : "❌");
        });
        
        colEstadoGarantiaList.setCellFactory(column -> new TableCell<Maquina, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-alignment: CENTER;");
            }
        });
        
        tablaMaquinas.setItems(listaMaquinas);
    }
    
    private void filtrarMaquinas(String texto) {
        if (texto == null || texto.isEmpty()) {
            tablaMaquinas.setItems(listaMaquinas);
            return;
        }
        
        ObservableList<Maquina> filtradas = FXCollections.observableArrayList();
        for (Maquina m : listaMaquinas) {
            if (m.getModelo().toLowerCase().contains(texto.toLowerCase()) || 
                m.getNumeroSerie().toLowerCase().contains(texto.toLowerCase()) ||
                m.getClienteNombre().toLowerCase().contains(texto.toLowerCase())) {
                filtradas.add(m);
            }
        }
        tablaMaquinas.setItems(filtradas);
    }
    
    private void cargarDatosReales() {
        try {
            List<Maquina> maquinasDB = maquinaService.obtenerTodas();
            if (maquinasDB != null && !maquinasDB.isEmpty()) {
                listaMaquinas.addAll(maquinasDB);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar máquinas de MongoDB: " + e.getMessage());
        }
    }
    
    private void cargarDatosDemo() {
        // --- MAQUINA 1: Activa ---
        Maquina maquina1 = new Maquina(
            "SN-2023-789012", 
            "Robot Cortacésped", 
            "Husqvarna", 
            "Automower 450X",
            "Jardines del Norte S.L.",
            LocalDate.now().minusMonths(6),
            LocalDate.now().plusMonths(18)
        );
        maquina1.getReparaciones().add(new Reparacion(
            LocalDate.now().minusMonths(2),
            "Mantenimiento programado: Cambio de cuchillas",
            "Carlos Técnico",
            45.50
        ));
        
        // --- MAQUINA 2: Vencida ---
        Maquina maquina2 = new Maquina(
            "SN-2020-555123", 
            "Motosierra Gasolina", 
            "Stihl", 
            "MS 170",
            "Pedro Agricultor",
            LocalDate.now().minusYears(3),
            LocalDate.now().minusYears(1) // Vencida hace 1 año
        );
        maquina2.getReparaciones().add(new Reparacion(
            LocalDate.now().minusYears(1).minusMonths(1),
            "Cambio de bujía y filtro de aire (En garantía)",
            "Ana Mecánica",
            0.0
        ));
        maquina2.getReparaciones().add(new Reparacion(
            LocalDate.now().minusMonths(1),
            "Reparación de cadena y tensado (Fuera de garantía)",
            "Ana Mecánica",
            35.00
        ));
        
        // --- MAQUINA 3: Activa (Nueva) ---
        Maquina maquina3 = new Maquina(
            "SN-2024-999888", 
            "Desbrozadora", 
            "Honda", 
            "UMK 435",
            "Ayuntamiento Local",
            LocalDate.now().minusWeeks(2),
            LocalDate.now().plusYears(2)
        );
        
        // --- MAQUINA 4: Vencida ---
        Maquina maquina4 = new Maquina(
            "SN-2019-111222", 
            "Tractor Cortacésped", 
            "John Deere", 
            "X350",
            "Club de Golf",
            LocalDate.now().minusYears(4),
            LocalDate.now().minusYears(2)
        );
        maquina4.getReparaciones().add(new Reparacion(
            LocalDate.now().minusYears(3),
            "Revisión anual 1",
            "Carlos Técnico",
            120.00
        ));
        maquina4.getReparaciones().add(new Reparacion(
            LocalDate.now().minusYears(2),
            "Revisión anual 2",
            "Carlos Técnico",
            125.00
        ));
        
        // --- MAQUINA 5: Activa ---
        Maquina maquina5 = new Maquina(
            "SN-2023-444555", 
            "Sopladora", 
            "Stihl", 
            "BR 600",
            "Limpiezas Rápidas S.A.",
            LocalDate.now().minusMonths(8),
            LocalDate.now().plusMonths(4)
        );
        
        listaMaquinas.addAll(maquina1, maquina2, maquina3, maquina4, maquina5);
    }

    public void setMaquina(Maquina maquina) {
        this.maquina = maquina;
        cargarDatosMaquina();
        cargarReparaciones();
    }

    private void cargarDatosMaquina() {
        lblNumeroSerie.setText(maquina.getNumeroSerie());
        lblTipo.setText(maquina.getTipo());
        lblMarca.setText(maquina.getMarca());
        lblModelo.setText(maquina.getModelo());
        lblCliente.setText(maquina.getClienteNombre());

        if (maquina.getFechaCompra() != null) {
            lblFechaCompra.setText(maquina.getFechaCompra().format(formatter));
        }

        if (maquina.getGarantiaHasta() != null) {
            lblGarantiaHasta.setText(maquina.getGarantiaHasta().format(formatter));
        }

        // Calcular días restantes
        long diasRestantes = maquina.getDiasGarantiaRestantes();
        if (diasRestantes >= 0) {
            lblDiasRestantes.setText(diasRestantes + " días restantes");
            progressGarantia.setProgress(1.0); // Full green usually implies 'good' but here we might want percentage
            // Calculate percentage of warranty remaining
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(maquina.getFechaCompra(), maquina.getGarantiaHasta());
            if (totalDays > 0) {
                progressGarantia.setProgress((double) diasRestantes / totalDays);
            }
            progressGarantia.setStyle("-fx-accent: #2ecc71;"); // Green
            
            lblEstadoGarantia.setText("EN GARANTÍA");
            hboxEstadoGarantia.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 20; -fx-padding: 5 15;");
        } else {
            lblDiasRestantes.setText("Vencida hace " + Math.abs(diasRestantes) + " días");
            progressGarantia.setProgress(1.0);
            progressGarantia.setStyle("-fx-accent: #e74c3c;"); // Red
            
            lblEstadoGarantia.setText("VENCIDA");
            hboxEstadoGarantia.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 20; -fx-padding: 5 15;");
        }

        // Configurar indicador de garantía
        if (maquina.isEnGarantia()) {
            lblEstadoGarantia.setText("✓ EN GARANTÍA");
            lblEstadoGarantia.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
            hboxEstadoGarantia.setStyle("-fx-background-color: #28a745; -fx-padding: 15; -fx-background-radius: 8;");

            // ProgressBar verde
            double progreso = calcularProgresoGarantia();
            progressGarantia.setProgress(progreso);
            progressGarantia.setStyle("-fx-accent: #28a745;");
        } else {
            lblEstadoGarantia.setText("✗ GARANTÍA VENCIDA");
            lblEstadoGarantia.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
            hboxEstadoGarantia.setStyle("-fx-background-color: #dc3545; -fx-padding: 15; -fx-background-radius: 8;");

            progressGarantia.setProgress(0);
            progressGarantia.setStyle("-fx-accent: #dc3545;");
        }
    }

    private double calcularProgresoGarantia() {
        if (maquina.getFechaCompra() == null || maquina.getGarantiaHasta() == null) {
            return 0;
        }

        long diasTotales = java.time.temporal.ChronoUnit.DAYS.between(
                maquina.getFechaCompra(),
                maquina.getGarantiaHasta()
        );

        long diasTranscurridos = java.time.temporal.ChronoUnit.DAYS.between(
                maquina.getFechaCompra(),
                LocalDate.now()
        );

        if (diasTotales <= 0) return 0;

        double progreso = 1.0 - ((double) diasTranscurridos / diasTotales);
        return Math.max(0, Math.min(1, progreso));
    }

    private void cargarReparaciones() {
        listaReparaciones.clear();
        if (maquina.getReparaciones() != null) {
            listaReparaciones.addAll(maquina.getReparaciones());
        }
        tablaReparaciones.setItems(listaReparaciones);
    }

    @FXML
    private void cerrar() {
        ((Stage) lblNumeroSerie.getScene().getWindow()).close();
    }
}

