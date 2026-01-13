package com.taller.controller;

import com.taller.database.MongoDBConnection;
import com.taller.database.OdooConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class NavigationController {
    
    @FXML private BorderPane mainContainer;
    @FXML private StackPane contentArea;
    @FXML private Label lblEstadoOdoo;
    @FXML private Label lblEstadoMongo;
    @FXML private Label lblFps;
    @FXML private Label lblLatencia;

    private Node dashboardView;
    private Node maquinasView;
    private Node clientesView;
    private Node inventarioView;
    private Node reservasView;
    private Node facturacionView;
    private Node garantiasView;

    @FXML
    public void initialize() {
        // Cargar dashboard por defecto
        cargarDashboard();
        
        // Verificar conexiones reales en background para no bloquear UI
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Pequeña pausa para efecto de carga
                javafx.application.Platform.runLater(this::verificarConexiones);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        // Actualizar métricas dummy live
        iniciarMetricasLive();
    }
    
    private void iniciarMetricasLive() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                    final int fps = 58 + (int)(Math.random() * 5);
                    final int lat = 10 + (int)(Math.random() * 15);
                    
                    javafx.application.Platform.runLater(() -> {
                        if (lblFps != null) lblFps.setText(fps + "");
                        if (lblLatencia != null) lblLatencia.setText(lat + "ms");
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void verificarConexiones() {
        // Verificar Odoo
        try {
            // Intento de conexión real
            OdooConnection conn = OdooConnection.getInstance();
            if (conn != null) {
                lblEstadoOdoo.setText("● Odoo Conectado");
                lblEstadoOdoo.setStyle("-fx-text-fill: #2ecc71;"); // Green
            }
        } catch (Exception e) {
            lblEstadoOdoo.setText("● Odoo: Desconectado");
            lblEstadoOdoo.setStyle("-fx-text-fill: #e74c3c;"); // Red
        }

        // Verificar MongoDB
        try {
            MongoDBConnection.getInstance().getDatabase().listCollectionNames().first();
            lblEstadoMongo.setText("● MongoDB Conectado");
            lblEstadoMongo.setStyle("-fx-text-fill: #2ecc71;"); // Green
        } catch (Exception e) {
            lblEstadoMongo.setText("● MongoDB: Error");
            lblEstadoMongo.setStyle("-fx-text-fill: #e74c3c;"); // Red
        }
    }

    @FXML
    private void cargarDashboard() {
        cargarVista("/fxml/dashboard.fxml", "dashboardView");
    }

    @FXML
    private void cargarMaquinas() {
        cargarVista("/fxml/main.fxml", "maquinasView");
    }

    @FXML
    private void cargarClientes() {
        cargarVista("/fxml/clientes.fxml", "clientesView");
    }

    @FXML
    private void cargarInventario() {
        cargarVista("/fxml/inventario.fxml", "inventarioView");
    }

    @FXML
    private void cargarReservas() {
        cargarVista("/fxml/reservas.fxml", "reservasView");
    }

    @FXML
    private void cargarFacturacion() {
        cargarVista("/fxml/facturacion.fxml", "facturacionView");
    }

    @FXML
    private void cargarGarantias() {
        cargarVista("/fxml/garantias.fxml", "garantiasView");
    }

    @FXML
    private void cargarReportes() {
        cargarVista("/fxml/reportes.fxml", "reportesView");
    }

    @FXML
    private void handleSalir() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Salir");
        alert.setHeaderText("¿Estás seguro de que deseas salir?");
        alert.setContentText("Se cerrará la conexión con la base de datos.");
        
        if (alert.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private void cargarAnaliticas() {
        cargarVista("/fxml/analiticas.fxml", "analiticasView");
    }

    private void cargarVista(String fxmlPath, String cacheField) {
        try {
            Node view;
            
            // Usar caché si existe
            switch (cacheField) {
                case "dashboardView":
                    if (dashboardView == null) {
                        dashboardView = FXMLLoader.load(getClass().getResource(fxmlPath));
                    }
                    view = dashboardView;
                    break;
                case "maquinasView":
                    // Siempre recargar para actualizar datos
                    view = FXMLLoader.load(getClass().getResource(fxmlPath));
                    break;
                case "clientesView":
                    if (clientesView == null) {
                        clientesView = FXMLLoader.load(getClass().getResource(fxmlPath));
                    }
                    view = clientesView;
                    break;
                case "inventarioView":
                    if (inventarioView == null) {
                        inventarioView = FXMLLoader.load(getClass().getResource(fxmlPath));
                    }
                    view = inventarioView;
                    break;
                case "reservasView":
                    if (reservasView == null) {
                        reservasView = FXMLLoader.load(getClass().getResource(fxmlPath));
                    }
                    view = reservasView;
                    break;
                case "facturacionView":
                    if (facturacionView == null) {
                        facturacionView = FXMLLoader.load(getClass().getResource(fxmlPath));
                    }
                    view = facturacionView;
                    break;
                case "garantiasView":
                    if (garantiasView == null) {
                        garantiasView = FXMLLoader.load(getClass().getResource(fxmlPath));
                    }
                    view = garantiasView;
                    break;
                case "reportesView":
                    // Siempre recargar reportes para actualizar conexión
                    view = FXMLLoader.load(getClass().getResource(fxmlPath));
                    break;
                case "analiticasView":
                    // Siempre recargar analíticas para actualizar datos
                    view = FXMLLoader.load(getClass().getResource(fxmlPath));
                    break;
                default:
                    return;
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar vista: " + fxmlPath);
            
            // Mostrar error visual al usuario
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudo cargar la vista: " + fxmlPath);
            String content = e.getMessage() != null ? e.getMessage() : e.toString();
            // Añadir stack trace
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            content += "\n\n" + sw.toString();
            
            Label label = new Label(content);
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
        }
    }
}
