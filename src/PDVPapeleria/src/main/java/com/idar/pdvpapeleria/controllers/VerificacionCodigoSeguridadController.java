package com.idar.pdvpapeleria.controllers;

import DAO.EmpleadoDAO;
import DAOImp.EmpleadoDAOImp;
import Vista.AlertaPDV;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author laura
 */
public class VerificacionCodigoSeguridadController {

    @FXML
    private TextField TFNombre, TFCodigoSeguridad;
    @FXML
    private Button BVerificar, BAtras;
    @FXML
    private Label L1;
    @FXML
    private Text L2, L3;

    private EmpleadoDAO db;
    private String codigoSeguridad;

    public VerificacionCodigoSeguridadController() {
        db = EmpleadoDAOImp.getInstance();
    }

    @FXML
    private void verificarCodigoSeguridad() {
        String username = TFNombre.getText().trim();
        String codigoSeguridad = TFCodigoSeguridad.getText().trim();

        if (username.isEmpty() || codigoSeguridad.isEmpty()) {
            AlertaPDV.mostrarError("Campos incompletos", "Por favor complete todos los campos.");
            return;
        }

        if (codigoSeguridad.length() != 4) {
            AlertaPDV.mostrarError("Código de seguridad inválido", "El código de seguridad debe tener exactamente 4 caracteres.");
            return;
        }

        if (db.verificarCodigoSeguridad(username, codigoSeguridad)) {
            try {
                switchToCambiarContraseña(codigoSeguridad);
            } catch (Exception e) {
                AlertaPDV.mostrarExcepcion("Error de carga", "Error al cargar la pantalla de cambiar contraseña", e);
            }
        } else {
            AlertaPDV.mostrarError("Error", "Código de seguridad incorrecto o usuario no encontrado.");
        }
    }

    @FXML
    private void switchToCambiarContraseña(String codigoSeguridad) throws IOException {
        System.out.println("Código de seguridad a pasar: " + codigoSeguridad);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/cambiarContraseña.fxml"));
        Parent root = loader.load();
        CambiarContraseñaController controller = loader.getController();
        controller.setCodigoSeguridad(codigoSeguridad);
        Stage stage = (Stage) BVerificar.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    // Método para cambiar a la pantalla del login si el usuario cancela
    @FXML
    private void switchToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) BAtras.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
}

