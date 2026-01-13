package com.empresa.fichador.controller;

import com.empresa.fichador.dao.TrabajadorDAO;
import com.empresa.fichador.dao.UsuarioDAO;
import com.empresa.fichador.model.Trabajador;
import com.empresa.fichador.model.Usuario;
import com.empresa.fichador.service.SessionService;
import com.empresa.fichador.util.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    private TrabajadorDAO trabajadorDAO;
    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        try {
            if (!HibernateUtil.hasConnectionError()) {
                trabajadorDAO = new TrabajadorDAO();
                usuarioDAO = new UsuarioDAO();
            }
        } catch (Exception e) {
            System.out.println("Error al inicializar: " + e.getMessage());
        }
        if (lblError != null) lblError.setText("");
        if (txtPassword != null) txtPassword.setOnAction(event -> handleLogin());
        if (txtUsuario != null) txtUsuario.setOnAction(event -> txtPassword.requestFocus());
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, introduce usuario y contraseña");
            return;
        }

        // Login admin por defecto
        if (usuario.equals("admin") && password.equals("admin123")) {
            Usuario adminDemo = new Usuario("admin", "admin123", "Administrador", "Sistema", Usuario.Rol.ADMIN);
            SessionService.getInstance().iniciarSesion(adminDemo);
            abrirVistaAdmin();
            return;
        }

        // Buscar usuario en base de datos
        try {
            if (usuarioDAO != null) {
                Usuario usuarioSistema = usuarioDAO.findByUsernameAndPassword(usuario, password);
                if (usuarioSistema != null) {
                    usuarioDAO.actualizarUltimoAcceso(usuarioSistema);
                    SessionService.getInstance().iniciarSesion(usuarioSistema);
                    abrirVistaAdmin();
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Buscar trabajador
        try {
            if (trabajadorDAO != null) {
                Trabajador trabajador = trabajadorDAO.findByTarjetaAndPin(usuario, password);
                if (trabajador != null) {
                    if (!trabajador.isActivo()) {
                        mostrarError("Esta cuenta está desactivada");
                        return;
                    }
                    SessionService.getInstance().setTrabajadorActual(trabajador);
                    abrirVistaTrabajador(trabajador);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        mostrarError("Usuario o contraseña incorrectos");
    }

    private void abrirVistaAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Control de Presencia - Panel de Administración");
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir panel de administración");
        }
    }

    private void abrirVistaTrabajador(Trabajador trabajador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuTrabajador.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof MenuTrabajadorController) {
                ((MenuTrabajadorController) controller).setTrabajador(trabajador);
            }

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Control de Presencia - " + trabajador.getNombreCompleto());
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir panel de trabajador");
        }
    }

    private void mostrarError(String mensaje) {
        if (lblError != null) {
            lblError.setText("❌ " + mensaje);
        }
    }
}

