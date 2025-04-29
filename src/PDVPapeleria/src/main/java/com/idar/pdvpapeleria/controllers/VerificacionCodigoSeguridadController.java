/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.idar.pdvpapeleria.controllers;

import DAO.EmpleadoDAO;
import DAOImp.EmpleadoDAOImp;
import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

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
            JOptionPane.showMessageDialog(null, "Por favor complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (codigoSeguridad.length() != 4) {
            JOptionPane.showMessageDialog(null, "El código de seguridad debe tener exactamente 4 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (db.verificarCodigoSeguridad(username, codigoSeguridad)) {
            try {
                switchToCambiarContraseña(codigoSeguridad);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Código de seguridad incorrecto o usuario no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
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
