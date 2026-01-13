package com.taller.controller;

import com.taller.database.MongoDBConnection;
import com.taller.database.OdooConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Label lblEstado;
    @FXML private Button btnLogin;
    @FXML private Button btnContinuarSinOdoo;
    
    private boolean mongoDBDisponible = false;
    private boolean odooDisponible = false;

    @FXML
    public void initialize() {
        // Valores por defecto
        txtUsuario.setText("admin");
        txtPassword.setText("admin");
        
        // Verificar conexiones en segundo plano
        verificarConexiones();
    }

    private void verificarConexiones() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Verificar MongoDB
                updateMessage("Verificando MongoDB...");
                try {
                    MongoDBConnection.getInstance();
                    mongoDBDisponible = true;
                    updateMessage("MongoDB conectado ✓");
                } catch (Exception e) {
                    updateMessage("MongoDB no disponible ✗");
                    System.err.println("Error MongoDB: " + e.getMessage());
                }
                
                Thread.sleep(500);
                
                // Verificar Odoo
                updateMessage("Verificando Odoo...");
                try {
                    OdooConnection.getInstance();
                    odooDisponible = true;
                    updateMessage("Odoo conectado ✓");
                } catch (Exception e) {
                    updateMessage("Odoo no disponible - Modo limitado");
                    System.err.println("Error Odoo: " + e.getMessage());
                }
                
                return null;
            }
        };
        
        task.messageProperty().addListener((obs, oldMsg, newMsg) -> {
            Platform.runLater(() -> lblEstado.setText("Estado: " + newMsg));
        });
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                btnLogin.setDisable(false);
                if (!mongoDBDisponible) {
                    mostrarError("MongoDB no está disponible. Verifica Docker.");
                    btnLogin.setDisable(true);
                }
                if (!odooDisponible) {
                    lblEstado.setText("Estado: Solo MongoDB disponible");
                }
            });
        });
        
        btnLogin.setDisable(true);
        new Thread(task).start();
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();
        
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingrese usuario y contraseña");
            return;
        }
        
        if (!mongoDBDisponible) {
            mostrarError("MongoDB no está disponible");
            return;
        }
        
        lblError.setVisible(false);
        btnLogin.setDisable(true);
        btnLogin.setText("Iniciando...");
        
        Task<Boolean> loginTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (odooDisponible) {
                    // Intentar login con Odoo
                    try {
                        OdooConnection odoo = OdooConnection.getInstance();
                        // Verificar si el usuario y contraseña son correctos
                        // Por simplicidad, aceptamos admin/admin
                        if ("admin".equals(usuario) && "admin".equals(password)) {
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        System.err.println("Error en login Odoo: " + e.getMessage());
                        return false;
                    }
                } else {
                    // Login básico sin Odoo
                    return "admin".equals(usuario) && "admin".equals(password);
                }
            }
        };
        
        loginTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (loginTask.getValue()) {
                    abrirAplicacionPrincipal();
                } else {
                    mostrarError("Usuario o contraseña incorrectos");
                    btnLogin.setDisable(false);
                    btnLogin.setText("Iniciar Sesión");
                }
            });
        });
        
        loginTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                mostrarError("Error al iniciar sesión: " + loginTask.getException().getMessage());
                btnLogin.setDisable(false);
                btnLogin.setText("Iniciar Sesión");
            });
        });
        
        new Thread(loginTask).start();
    }

    @FXML
    private void handleContinuarSinOdoo() {
        if (!mongoDBDisponible) {
            mostrarError("MongoDB es requerido para continuar");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Continuar sin Odoo");
        alert.setHeaderText("¿Desea continuar sin integración con Odoo?");
        alert.setContentText("La aplicación funcionará en modo limitado (solo MongoDB).\nNo estarán disponibles las funciones de facturación con Odoo.");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            abrirAplicacionPrincipal();
        }
    }

    private void abrirAplicacionPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/navigation.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1400, 900);
            
            stage.setTitle("Taller de Reparación - Sistema de Gestión");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            System.out.println("✓ Sesión iniciada correctamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar la aplicación: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
