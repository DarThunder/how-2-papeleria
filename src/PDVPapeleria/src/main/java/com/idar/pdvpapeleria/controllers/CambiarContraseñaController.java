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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author laura
 */
public class CambiarContraseñaController {

    @FXML
    private TextField TFCambiarContraseña, TFRepetirContraseña;
    @FXML
    private Button BCambiarContraseña, BAtras;

    private String codigoSeguridad;
    private EmpleadoDAO db;

    public CambiarContraseñaController() {
        db = EmpleadoDAOImp.getInstance();
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
        System.out.println("Código de seguridad recibido en CambiarContraseñaController: " + this.codigoSeguridad);
    }

    @FXML
    private void cambiarContraseña() {
        String nuevaContraseña = TFCambiarContraseña.getText().trim();
        String repetirContraseña = TFRepetirContraseña.getText().trim();

        if (nuevaContraseña.isEmpty() || repetirContraseña.isEmpty()) {
            AlertaPDV.mostrarError("Campos incompletos", "Por favor, complete todos los campos");
            return;
        }

        if (!nuevaContraseña.equals(repetirContraseña)) {
            AlertaPDV.mostrarError("Error", "Las contraseñas no coinciden.");
            return;
        }

        if (db.cambiarContraseña(nuevaContraseña, codigoSeguridad)) {
            AlertaPDV.mostrarExito("Éxito", "Contraseña cambiada exitosamente.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                AlertaPDV.mostrarExcepcion("Interrupción del Thread.Sleep", "Ocurrio una interrupción inesperada durante la pausa del hilo", e);
            }
            try {
                switchToLogin();
            } catch (Exception e) {
                AlertaPDV.mostrarExcepcion("Error de carga", "Se produjo un error al cargar la pantalla de inicio de sesión", e);
            }
        } else {
            AlertaPDV.mostrarError("Error", "Error al cambiar la contraseña.");
        }
    }

    // Método para cambiar a la pantalla de verificación de código de seguridad
    @FXML
    private void switchToVerificacionCodigoSeguridad() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/verificacionCodigoSeguridad.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) BAtras.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    // Método para cambiar a la pantalla del login
    @FXML
    private void switchToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scees/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) BCambiarContraseña.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
}
