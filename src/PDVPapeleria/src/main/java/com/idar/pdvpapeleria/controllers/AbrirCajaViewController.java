/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.idar.pdvpapeleria.controllers;

import DAO.EmpleadoDAO;
import DAOImp.EmpleadoDAOImp;
import Vista.AlertaPDV;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;



/**
 * FXML Controller class
 *
 * @author jeshu
 */
public class AbrirCajaViewController implements Initializable {
    
    @FXML
    private Button BAbrirCaja; 
    
    @FXML
    private Button BAtras; 
    
    @FXML
    private TextField TFNombre; 
    
    @FXML
    private TextField TFCodigoSeguridad; 
    
   
    private EmpleadoDAO db;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = EmpleadoDAOImp.getInstance();
    }    
    
     @FXML
     public void switchToCaja() throws MalformedURLException, IOException {
        File fxmlFile = new File("src/main/resources/scenes/cajeroView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BAbrirCaja.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
    @FXML
    private void validarCodigoSeguridad() {
        String username = TFNombre.getText().trim();
        String codigoSeguridad = TFCodigoSeguridad.getText().trim(); 
        if (username.isEmpty() || codigoSeguridad.isEmpty()) {
            AlertaPDV.mostrarError("Error", "Todos los campos deben estar llenos"); 
            return;
        }
        if (codigoSeguridad.length() != 4) {
            AlertaPDV.mostrarError("Error", "El código de seguridad deben ser 4 números");
            return;
        }
        if (db.verificarCodigoSeguridad(username, codigoSeguridad)) {
            try {
                switchToCaja();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            AlertaPDV.mostrarError("Error", "Código de seguridad incorrecto o usuario no encontrado");
        }
    }
    
    @FXML
    public void regresarAlogin() throws MalformedURLException, IOException {
        File fxmlFile = new File("src/main/resources/scenes/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BAtras.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
}
