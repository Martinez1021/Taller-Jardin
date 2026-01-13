package com.taller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX
 * Integración Taller de Reparación con MongoDB y Odoo
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║  TALLER DE REPARACIÓN - HERRAMIENTAS DE JARDÍN        ║");
            System.out.println("║  Iniciando aplicación...                               ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println();

            // Cargar pantalla de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Configurar la ventana de login
            primaryStage.setTitle("Taller de Jardín - Inicio de Sesión");
            primaryStage.setScene(new Scene(root, 1000, 700));
            primaryStage.setResizable(false);
            primaryStage.show();

            System.out.println("✓ Pantalla de login cargada");
            System.out.println();

        } catch (Exception e) {
            System.err.println("✗ Error crítico al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            mostrarErrorYSalir("Error al iniciar", e.getMessage());
        }
    }

    @Override
    public void stop() {
        // Cerrar conexiones al cerrar la aplicación
        try {
            com.taller.database.MongoDBConnection.getInstance().close();
            System.out.println("✓ Conexión a MongoDB cerrada");
        } catch (Exception e) {
            System.err.println("MongoDB no estaba conectado o error al cerrar: " + e.getMessage());
        }

        System.out.println("✓ Aplicación finalizada");
    }

    private void mostrarErrorYSalir(String titulo, String mensaje) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText("Error de Conexión");
            alert.setContentText(mensaje);
            alert.showAndWait();
            System.exit(1);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

