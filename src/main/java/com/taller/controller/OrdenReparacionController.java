package com.taller.controller;

import com.taller.model.Maquina;
import com.taller.model.OrdenReparacion;
import com.taller.service.SincronizacionService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controlador para la ventana de creación de orden de reparación
 */
public class OrdenReparacionController {

    @FXML private Label lblMaquina;
    @FXML private Label lblCliente;

    @FXML private TextArea txtDescripcionProblema;
    @FXML private TextField txtTecnicoAsignado;
    @FXML private DatePicker dpFechaEstimada;
    @FXML private TextField txtPresupuesto;

    @FXML private CheckBox chkCrearEnOdoo;
    @FXML private Button btnCrearOrden;

    private Maquina maquina;
    private SincronizacionService sincronizacionService;

    @FXML
    public void initialize() {
        sincronizacionService = new SincronizacionService();

        // Establecer fecha estimada por defecto (7 días desde hoy)
        dpFechaEstimada.setValue(LocalDate.now().plusDays(7));

        // Checkbox de crear en Odoo marcado por defecto
        chkCrearEnOdoo.setSelected(true);
    }

    public void setMaquina(Maquina maquina) {
        this.maquina = maquina;

        lblMaquina.setText(String.format("%s - %s %s %s",
                maquina.getNumeroSerie(),
                maquina.getTipo(),
                maquina.getMarca(),
                maquina.getModelo()
        ));

        lblCliente.setText(maquina.getClienteNombre());
    }

    @FXML
    private void crearOrdenReparacion() {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            // Crear orden de reparación
            OrdenReparacion orden = new OrdenReparacion();
            orden.setNumeroSerieMaquina(maquina.getNumeroSerie());
            orden.setDescripcionProblema(txtDescripcionProblema.getText().trim());
            orden.setTecnicoAsignado(txtTecnicoAsignado.getText().trim());
            orden.setFechaEstimadaEntrega(dpFechaEstimada.getValue());

            // Parsear presupuesto
            try {
                double presupuesto = Double.parseDouble(txtPresupuesto.getText().trim().replace(",", "."));
                orden.setPresupuestoEstimado(presupuesto);
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "El presupuesto debe ser un número válido", Alert.AlertType.ERROR);
                return;
            }

            // Crear orden completa (MongoDB + Odoo si está marcado)
            if (chkCrearEnOdoo.isSelected()) {
                boolean exito = sincronizacionService.crearOrdenReparacionCompleta(orden);

                if (exito) {
                    mostrarAlerta("Éxito",
                            "Orden de reparación creada correctamente en MongoDB y Odoo",
                            Alert.AlertType.INFORMATION);
                    cerrar();
                } else {
                    mostrarAlerta("Error",
                            "No se pudo crear la orden. Revisa la consola para más detalles.",
                            Alert.AlertType.ERROR);
                }
            } else {
                // Solo MongoDB (sin Odoo)
                boolean exito = sincronizacionService.crearOrdenReparacionCompleta(orden);

                if (exito) {
                    mostrarAlerta("Éxito",
                            "Orden de reparación creada en MongoDB",
                            Alert.AlertType.INFORMATION);
                    cerrar();
                }
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear orden: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtDescripcionProblema.getText().trim().isEmpty()) {
            errores.append("- Descripción del problema\n");
        }

        if (txtTecnicoAsignado.getText().trim().isEmpty()) {
            errores.append("- Técnico asignado\n");
        }

        if (dpFechaEstimada.getValue() == null) {
            errores.append("- Fecha estimada de entrega\n");
        }

        if (txtPresupuesto.getText().trim().isEmpty()) {
            errores.append("- Presupuesto estimado\n");
        }

        if (dpFechaEstimada.getValue() != null && dpFechaEstimada.getValue().isBefore(LocalDate.now())) {
            errores.append("- La fecha estimada debe ser futura\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta("Campos obligatorios", errores.toString(), Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) btnCrearOrden.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

