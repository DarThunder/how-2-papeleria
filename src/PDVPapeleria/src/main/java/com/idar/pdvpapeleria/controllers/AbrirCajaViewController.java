/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.idar.pdvpapeleria.controllers;

import DAO.EmpleadoDAO;
import DAOImp.EmpleadoDAOImp;
import VO.EmpleadoVO;
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
            AlertaPDV.mostrarError("Error", "El código de seguridad debe tener 4 dígitos");
            return;
        }

        try {
            EmpleadoVO empleado = db.obtenerEmpleadoPorUsuarioYCodigo(username, codigoSeguridad);

            if (empleado != null) {
                // Verificar si el empleado es cajero o administrador
                if (!empleado.getRol().equals("Cajero") && !empleado.getRol().equals("Administrador")) {
                    AlertaPDV.mostrarError("Error", "Solo personal autorizado puede abrir caja");
                    return;
                }

                File fxmlFile = new File("src/main/resources/scenes/cajeroView.fxml");
                FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
                Parent root = loader.load();

                CajeroViewController cajeroController = loader.getController();
                cajeroController.setCajeroActual(empleado);

                Stage stage = (Stage) BAbrirCaja.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();

            } else {
                AlertaPDV.mostrarError("Error", "Credenciales incorrectas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertaPDV.mostrarError("Error", "Ocurrió un error al validar las credenciales");
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
