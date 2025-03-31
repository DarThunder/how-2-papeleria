package com.idar.pdvpapeleria.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lib.SqlLib;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class LoginController {

    @FXML private Button loginButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField; 
    @FXML private Label errorMessageLabel;
    private SqlLib db;

    @FXML
    public void initialize() {
        try {
            db = SqlLib.getInstance("", "", ""); 
        } catch (SQLException e) {
            errorMessageLabel.setText("Error al conectar a la base de datos.");
            e.printStackTrace(); // Log del error
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (db.isValidCredentials(username, password)) {
                String role = db.getRole(username);
                switch (role) {
                    case "Dueño" -> switchToDueñoView();
                    case "Administrador" -> switchToAdminView();
                    case "Cajero" -> switchToCajeroView();
                    default -> errorMessageLabel.setText("Rol desconocido o no autorizado.");
                }
            } else {
                errorMessageLabel.setText("Nombre de usuario o contraseña incorrectos.");
            }
        } catch (SQLException | IOException e) {
            errorMessageLabel.setText("Error al iniciar sesión.");
            e.printStackTrace();
        }
    }

    private void switchToView(String fxmlPath) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath); 
        if (fxmlUrl == null) {
            errorMessageLabel.setText("Error al cargar la vista.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
    @FXML
    private void switchToDueñoView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/dueñoView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
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
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    @FXML
     /**
     * Cambia la escena actual a la pantalla de opciones del administrador.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
 
    private void switchToCajeroView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/dueñoView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

}