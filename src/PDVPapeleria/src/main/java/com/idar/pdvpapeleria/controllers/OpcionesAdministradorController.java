/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.idar.pdvpapeleria.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador FXML para la vista de opciones del administrador.
 * Gestiona la navegación entre las vistas disponibles para el administrador,
 * incluyendo la gestión de productos, proveedores y el regreso a la pantalla de login.
 */
public class OpcionesAdministradorController {

    @FXML
    private Label L1;
    @FXML
    private Label L11;
    @FXML
    private Button BProducto;
    @FXML
    private Button BProveedor;
    @FXML
    private Button atrasButton;

    /**
     * Método de inicialización del controlador.
     * Se ejecuta automáticamente después de que se haya cargado el archivo FXML.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Abre la ventana de gestión de productos.
     * 
     * @throws MalformedURLException Si la URL del archivo FXML es inválida
     * @throws IOException Si ocurre un error al cargar el archivo FXML
     */
    @FXML
    public void abrirVentanaProducto() throws MalformedURLException, IOException {
        File fxmlFile = new File("src/main/resources/scenes/adminInventarioView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BProducto.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Abre la ventana de gestión de proveedores.
     * 
     * @throws MalformedURLException Si la URL del archivo FXML es inválida
     * @throws IOException Si ocurre un error al cargar el archivo FXML
     */
    @FXML
    public void abrirVentanaProveedor() throws MalformedURLException, IOException {
        File fxmlFile = new File("src/main/resources/scenes/adminProveedoresView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BProveedor.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Regresa a la pantalla de login cerrando la ventana actual.
     * 
     * @throws MalformedURLException Si la URL del archivo FXML es inválida
     * @throws IOException Si ocurre un error al cargar el archivo FXML
     */
    @FXML
    public void regresarAlogin() throws MalformedURLException, IOException {
        File fxmlFile = new File("src/main/resources/scenes/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BProveedor.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}