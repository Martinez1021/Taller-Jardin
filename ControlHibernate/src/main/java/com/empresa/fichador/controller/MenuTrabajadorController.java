package com.empresa.fichador.controller;

import com.empresa.fichador.dao.FichajeDAO;
import com.empresa.fichador.model.Fichaje;
import com.empresa.fichador.model.Trabajador;
import com.empresa.fichador.service.SessionService;
import com.empresa.fichador.util.HibernateUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class MenuTrabajadorController {

    @FXML private BorderPane mainPane;
    @FXML private Label lblNombreTrabajador;
    @FXML private Label lblFechaHora;
    @FXML private Label lblFechaTexto;
    @FXML private Label lblTarjeta;
    @FXML private Label lblEstadoHoy;
    @FXML private Label lblUltimoFichaje;
    @FXML private Label lblHorasHoy;
    @FXML private Label lblClima;
    @FXML private Label lblClimaIcono;
    @FXML private Label lblMensaje;
    @FXML private Label lblDiasTrabajados;
    @FXML private Label lblHorasSemana;
    @FXML private Label lblMediaDiaria;
    @FXML private Button btnEntrada;
    @FXML private Button btnSalida;
    @FXML private VBox vboxHistorial;

    private Trabajador trabajador;
    private FichajeDAO fichajeDAO;
    private Timeline reloj;

    @FXML
    public void initialize() {
        try {
            fichajeDAO = new FichajeDAO();
        } catch (Exception e) {
            System.err.println("Error al inicializar DAO: " + e.getMessage());
        }
        iniciarReloj();
        actualizarFechaTexto();
        if (lblMensaje != null) lblMensaje.setText("");
        cargarClima();
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
        SessionService.getInstance().setTrabajadorActual(trabajador);

        if (lblNombreTrabajador != null) {
            lblNombreTrabajador.setText("👤 " + trabajador.getNombreCompleto());
        }
        if (lblTarjeta != null) {
            lblTarjeta.setText(trabajador.getNumeroTarjeta());
        }

        actualizarEstado();
        cargarHistorialHoy();
        cargarResumenSemanal();
    }

    private void iniciarReloj() {
        reloj = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            if (lblFechaHora != null) {
                lblFechaHora.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        }));
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }

    private void actualizarFechaTexto() {
        if (lblFechaTexto != null) {
            LocalDate hoy = LocalDate.now();
            String diaSemana = hoy.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            diaSemana = diaSemana.substring(0, 1).toUpperCase() + diaSemana.substring(1);
            String mes = hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            lblFechaTexto.setText(String.format("%s, %d de %s de %d",
                    diaSemana, hoy.getDayOfMonth(), mes, hoy.getYear()));
        }
    }

    private void cargarClima() {
        try {
            // Datos de clima estáticos
            if (lblClima != null) {
                lblClima.setText("20°C - Soleado");
            }
            if (lblClimaIcono != null) {
                lblClimaIcono.setText("☀️");
            }
        } catch (Exception e) {
            if (lblClima != null) lblClima.setText("No disponible");
        }
    }

    private String obtenerIconoClima(String descripcion) {
        String desc = descripcion.toLowerCase();
        if (desc.contains("sol") || desc.contains("despejado")) return "☀️";
        if (desc.contains("nub") || desc.contains("parcial")) return "⛅";
        if (desc.contains("lluv")) return "🌧️";
        if (desc.contains("nieve")) return "❄️";
        if (desc.contains("torment")) return "⛈️";
        return "🌤️";
    }

    private void actualizarEstado() {
        if (trabajador == null || fichajeDAO == null) return;

        try {
            List<Fichaje> fichajesHoy = fichajeDAO.findByTrabajadorAndFecha(trabajador, LocalDate.now());

            boolean tieneEntrada = fichajesHoy.stream()
                    .anyMatch(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA);
            boolean tieneSalida = fichajesHoy.stream()
                    .anyMatch(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA);

            if (fichajesHoy.isEmpty()) {
                if (lblEstadoHoy != null) {
                    lblEstadoHoy.setText("⏳ Sin fichar");
                    lblEstadoHoy.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #f59e0b;");
                }
                if (lblUltimoFichaje != null) lblUltimoFichaje.setText("--:--");
                if (btnEntrada != null) btnEntrada.setDisable(false);
                if (btnSalida != null) btnSalida.setDisable(true);
            } else if (tieneEntrada && tieneSalida) {
                if (lblEstadoHoy != null) {
                    lblEstadoHoy.setText("✅ Jornada completa");
                    lblEstadoHoy.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #10b981;");
                }
                if (btnEntrada != null) btnEntrada.setDisable(true);
                if (btnSalida != null) btnSalida.setDisable(true);
            } else if (tieneEntrada) {
                if (lblEstadoHoy != null) {
                    lblEstadoHoy.setText("🔵 Trabajando...");
                    lblEstadoHoy.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #3b82f6;");
                }
                if (btnEntrada != null) btnEntrada.setDisable(true);
                if (btnSalida != null) btnSalida.setDisable(false);
            }

            // Último fichaje
            Fichaje ultimoFichaje = fichajeDAO.findUltimoFichaje(trabajador);
            if (ultimoFichaje != null && lblUltimoFichaje != null) {
                LocalDateTime hora = ultimoFichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA
                        ? ultimoFichaje.getHoraEntrada()
                        : ultimoFichaje.getHoraSalida();
                if (hora != null) {
                    lblUltimoFichaje.setText(hora.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }

            // Calcular horas trabajadas hoy
            calcularHorasHoy(fichajesHoy);

        } catch (Exception e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
        }
    }

    private void calcularHorasHoy(List<Fichaje> fichajesHoy) {
        LocalDateTime entrada = null;
        LocalDateTime salida = null;

        for (Fichaje f : fichajesHoy) {
            if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null) {
                entrada = f.getHoraEntrada();
            } else if (f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA && f.getHoraSalida() != null) {
                salida = f.getHoraSalida();
            }
        }

        if (entrada != null && lblHorasHoy != null) {
            LocalDateTime fin = salida != null ? salida : LocalDateTime.now();
            long minutos = java.time.Duration.between(entrada, fin).toMinutes();
            lblHorasHoy.setText(String.format("%dh %02dm", minutos / 60, minutos % 60));
        } else if (lblHorasHoy != null) {
            lblHorasHoy.setText("0h 00m");
        }
    }

    private void cargarHistorialHoy() {
        if (trabajador == null || fichajeDAO == null || vboxHistorial == null) return;

        try {
            vboxHistorial.getChildren().clear();
            List<Fichaje> fichajesHoy = fichajeDAO.findByTrabajadorAndFecha(trabajador, LocalDate.now());

            if (fichajesHoy.isEmpty()) {
                Label lblVacio = new Label("No hay fichajes registrados hoy");
                lblVacio.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
                vboxHistorial.getChildren().add(lblVacio);
            } else {
                for (Fichaje fichaje : fichajesHoy) {
                    vboxHistorial.getChildren().add(crearFilaFichaje(fichaje));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar historial: " + e.getMessage());
        }
    }

    private HBox crearFilaFichaje(Fichaje fichaje) {
        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: " +
                (fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "#f0fdf4" : "#fef2f2") +
                "; -fx-padding: 14 18; -fx-background-radius: 10;");

        String icono = fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "⬇️" : "⬆️";
        String tipo = fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "Entrada" : "Salida";
        String color = fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA ? "#10b981" : "#ef4444";

        LocalDateTime hora = fichaje.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA
                ? fichaje.getHoraEntrada()
                : fichaje.getHoraSalida();
        String horaStr = hora != null ? hora.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "--:--";

        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 20px;");

        Label lblTipo = new Label(tipo);
        lblTipo.setStyle("-fx-font-weight: 700; -fx-text-fill: " + color + "; -fx-font-size: 15px;");

        Label lblHora = new Label(horaStr);
        lblHora.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        HBox.setHgrow(lblHora, Priority.ALWAYS);

        hbox.getChildren().addAll(lblIcono, lblTipo, lblHora);

        if (fichaje.getClima() != null && !fichaje.getClima().isEmpty()) {
            Label lblClima = new Label("🌤️ " + fichaje.getClima());
            if (fichaje.getTemperatura() != null) {
                lblClima.setText(String.format("🌡️ %.0f°C", fichaje.getTemperatura()));
            }
            lblClima.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
            hbox.getChildren().add(lblClima);
        }

        return hbox;
    }

    private void cargarResumenSemanal() {
        if (trabajador == null || fichajeDAO == null) return;

        try {
            LocalDate hoy = LocalDate.now();
            int diasTrabajados = 0;
            double horasTotales = 0;

            // Calcular para los últimos 7 días
            for (int i = 0; i < 7; i++) {
                LocalDate fecha = hoy.minusDays(i);
                List<Fichaje> fichajes = fichajeDAO.findByTrabajadorAndFecha(trabajador, fecha);

                LocalDateTime entrada = null;
                LocalDateTime salida = null;

                for (Fichaje f : fichajes) {
                    if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null) {
                        entrada = f.getHoraEntrada();
                    } else if (f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA && f.getHoraSalida() != null) {
                        salida = f.getHoraSalida();
                    }
                }

                if (entrada != null && salida != null) {
                    diasTrabajados++;
                    horasTotales += java.time.Duration.between(entrada, salida).toMinutes() / 60.0;
                }
            }

            if (lblDiasTrabajados != null) lblDiasTrabajados.setText(String.valueOf(diasTrabajados));
            if (lblHorasSemana != null) lblHorasSemana.setText(String.format("%.1fh", horasTotales));
            if (lblMediaDiaria != null) {
                double media = diasTrabajados > 0 ? horasTotales / diasTrabajados : 0;
                lblMediaDiaria.setText(String.format("%.1fh", media));
            }

        } catch (Exception e) {
            System.err.println("Error al cargar resumen semanal: " + e.getMessage());
        }
    }

    @FXML
    private void handleEntrada() {
        registrarFichaje(Fichaje.TipoFichaje.ENTRADA);
    }

    @FXML
    private void handleSalida() {
        registrarFichaje(Fichaje.TipoFichaje.SALIDA);
    }

    private void registrarFichaje(Fichaje.TipoFichaje tipo) {
        if (trabajador == null || fichajeDAO == null) {
            mostrarError("Error: No se puede registrar el fichaje");
            return;
        }

        try {
            Fichaje fichaje = new Fichaje();
            fichaje.setTrabajador(trabajador);
            fichaje.setFecha(LocalDate.now());
            fichaje.setTipoFichaje(tipo);

            if (tipo == Fichaje.TipoFichaje.ENTRADA) {
                fichaje.setHoraEntrada(LocalDateTime.now());
                // Datos estáticos de clima
                fichaje.setClima("Soleado");
                fichaje.setTemperatura(20.0);
            } else {
                fichaje.setHoraSalida(LocalDateTime.now());
            }

            fichajeDAO.save(fichaje);

            String mensaje = tipo == Fichaje.TipoFichaje.ENTRADA
                    ? "✅ Entrada registrada correctamente a las " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    : "✅ Salida registrada correctamente a las " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

            mostrarExito(mensaje);
            actualizarEstado();
            cargarHistorialHoy();
            cargarResumenSemanal();

        } catch (Exception e) {
            mostrarError("Error al registrar fichaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCerrarSesion() {
        if (reloj != null) {
            reloj.stop();
        }
        SessionService.getInstance().cerrarSesion();

        try {
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

    private void mostrarExito(String mensaje) {
        if (lblMensaje == null) return;
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(
                "-fx-background-color: #d1fae5; " +
                "-fx-border-color: #10b981; " +
                "-fx-border-width: 2; " +
                "-fx-text-fill: #065f46; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: 700; " +
                "-fx-padding: 18 35; " +
                "-fx-background-radius: 12; " +
                "-fx-border-radius: 12;"
        );
    }

    private void mostrarError(String mensaje) {
        if (lblMensaje == null) return;
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(
                "-fx-background-color: #fee2e2; " +
                "-fx-border-color: #ef4444; " +
                "-fx-border-width: 2; " +
                "-fx-text-fill: #991b1b; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: 700; " +
                "-fx-padding: 18 35; " +
                "-fx-background-radius: 12; " +
                "-fx-border-radius: 12;"
        );
    }
}

