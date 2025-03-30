package com.idar.pdvpapeleria.controllers;

import javafx.scene.control.Button;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import lib.SqlLib;

public class LoginController {

    @FXML
    private Button B1;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    private SqlLib db;

    /**
     * Establece la conexión con la base de datos.
     *
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */
    public void setDb() throws SQLException {
        this.db = SqlLib.getInstance("", "", "");
    }

    /**
     * Inicializa el controlador y configura el estilo del panel de inicio de
     * sesión.
     *
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */
    @FXML
    private void initialize() throws SQLException {
        setDb();
    }
    
    /**
     * Maneja el inicio de sesión validando las credenciales ingresadas.
     *
     * @throws SQLException Si ocurre un error en la consulta a la base de
     * datos.
     */
    @FXML
    private void handleLogin() throws SQLException, IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (db.isValidCredentials(username, password)) {
            String role = db.getRole(username); 
            switch (role) {
                case "Dueño" ->
                    switchToDueñoView();
                case "Administrador" ->
                    switchToAdminView();
                case "Cajero" ->
                    switchToCajeroView();
                default ->
                    JOptionPane.showMessageDialog(null, "Rol desconocido o no autorizado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "El nombre de usuario o la contraseña son incorrectos.");
        }
    }

    /**
     * Cambia la escena actual a la pantalla de opciones del administrador.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    @FXML
    private void switchToDueñoView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/dueñoView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Cambia la escena actual a la pantalla de opciones del administrador.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    @FXML
    private void switchToAdminView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/dueñoView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Cambia la escena actual a la pantalla de opciones del administrador.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    @FXML
    private void switchToCajeroView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/dueñoView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

}
