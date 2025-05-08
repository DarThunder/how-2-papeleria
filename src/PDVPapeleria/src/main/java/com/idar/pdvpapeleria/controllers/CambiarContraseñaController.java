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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

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
            JOptionPane.showMessageDialog(null, "Por favor complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!nuevaContraseña.equals(repetirContraseña)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (db.cambiarContraseña(nuevaContraseña, codigoSeguridad)) {
            JOptionPane.showMessageDialog(null, "Contraseña cambiada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                switchToLogin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al cambiar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para cambiar a la pantalla de login si el usuario cancela
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

    // Método para cambiar a la pantalla del login si el usuario cancela
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
