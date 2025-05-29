/**
 * Controlador para la vista de cambio de contraseña.
 * 
 * <p>Permite al usuario ingresar una nueva contraseña, validarla y actualizarla en la base de datos
 * utilizando el código de seguridad proporcionado previamente.</p>
 * 
 * @author dylxn999
 * @date 28/05/2025
 */
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

public class CambiarContraseñaController {

    @FXML
    private TextField TFCambiarContraseña, TFRepetirContraseña;

    @FXML
    private Button BCambiarContraseña, BAtras;

    private String codigoSeguridad;
    private EmpleadoDAO db;

    /**
     * Constructor que inicializa la instancia de acceso a datos del empleado.
     */
    public CambiarContraseñaController() {
        db = EmpleadoDAOImp.getInstance();
    }

    /**
     * Asigna el código de seguridad usado para identificar al empleado.
     * 
     * @param codigoSeguridad Código único de seguridad del empleado.
     */
    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    /**
     * Maneja el evento para cambiar la contraseña del usuario.
     * Valida los campos de entrada y actualiza la contraseña en la base de datos.
     */
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
                AlertaPDV.mostrarExcepcion("Interrupción del Thread.Sleep", "Ocurrió una interrupción inesperada durante la pausa del hilo", e);
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

    /**
     * Cambia la vista a la pantalla de verificación de código de seguridad.
     *
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
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

    /**
     * Cambia la vista a la pantalla de inicio de sesión (login).
     *
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @FXML
    private void switchToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) BCambiarContraseña.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
}
