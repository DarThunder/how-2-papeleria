package com.idar.pdvpapeleria.controllers;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controlador de la vista principal del dueño del sistema PDV Papelería.
 *
 * <p>
 * Este controlador permite la navegación entre diferentes vistas del sistema,
 * como administración de empleados, inventario, historial de ventas y
 * login.</p>
 *
 * @author dylxn999
 * Fecha: 28/05/2025
 */
public class DueñoViewController {

    @FXML
    private Pane P1, P2;

    @FXML
    private Button BEmpleados, BVentas, BInventario, BAtras;

    @FXML
    private Label L1;

    /**
     * Cambia la escena actual a la especificada mediante un archivo FXML.
     *
     * @param button Botón que fue presionado para realizar el cambio de escena.
     * @param fxmlPath Ruta del archivo FXML a cargar.
     */
    private void switchToScene(Button button, String fxmlPath) {
        try {
            File fxmlFile = new File(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Parent root = loader.load();
            Stage stage = (Stage) button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cambia a la vista de administración de empleados.
     */
    @FXML
    private void switchToAdministracionEmpleados() {
        switchToScene(BEmpleados, "src/main/resources/scenes/administracionEmpleados.fxml");
    }

    /**
     * Cambia a la vista de administración de inventario.
     */
    @FXML
    private void switchToInventario() {
        switchToScene(BInventario, "src/main/resources/scenes/adminInventarioView.fxml");
    }

    /**
     * Cambia a la vista de historial de ventas.
     */
    @FXML
    private void switchToVentas() {
        switchToScene(BVentas, "src/main/resources/scenes/historialVentas.fxml");
    }

    /**
     * Cambia a la vista de inicio de sesión.
     */
    @FXML
    private void switchToLogin() {
        switchToScene(BEmpleados, "src/main/resources/scenes/login.fxml");
    }
}
