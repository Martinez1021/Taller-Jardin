package com.empresa.fichador.controller;

import com.empresa.fichador.service.SessionService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuAdminController {

    @FXML private BorderPane mainPane;
    @FXML private VBox contentArea;
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;

    @FXML private Button btnDashboard;
    @FXML private Button btnGestion;
    @FXML private Button btnEstadisticas;
    @FXML private Button btnTrabajadores;

    private Button botonActivo;

    @FXML
    public void initialize() {
        if (lblUsuario != null) {
            lblUsuario.setText(SessionService.getInstance().getNombreUsuario());
        }
        if (lblRol != null) {
            lblRol.setText(SessionService.getInstance().getRolDescripcion());
        }

        // Cargar dashboard por defecto
        Platform.runLater(() -> handleDashboard());
    }

    @FXML
    private void handleDashboard() {
        cargarVista("Dashboard.fxml");
        setBotonActivo(btnDashboard);
    }

    @FXML
    private void handleGestion() {
        cargarVista("Gestion.fxml");
        setBotonActivo(btnGestion);
    }

    @FXML
    private void handleEstadisticas() {
        cargarVista("Estadisticas.fxml");
        setBotonActivo(btnEstadisticas);
    }

    @FXML
    private void handleTrabajadores() {
        cargarVista("GestionTrabajadores.fxml");
        setBotonActivo(btnTrabajadores);
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            SessionService.getInstance().cerrarSesion();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mainPane.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Control de Presencia - Iniciar Sesión");
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarVista(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxml));
            Parent vista = loader.load();

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(vista);
                VBox.setVgrow(vista, Priority.ALWAYS);

                if (vista instanceof Region) {
                    ((Region) vista).setMaxWidth(Double.MAX_VALUE);
                    ((Region) vista).setMaxHeight(Double.MAX_VALUE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar vista: " + fxml);
        }
    }

    private void setBotonActivo(Button boton) {
        if (botonActivo != null) {
            botonActivo.getStyleClass().remove("sidebar-item-active");
        }
        if (boton != null) {
            boton.getStyleClass().add("sidebar-item-active");
            botonActivo = boton;
        }
    }
}

