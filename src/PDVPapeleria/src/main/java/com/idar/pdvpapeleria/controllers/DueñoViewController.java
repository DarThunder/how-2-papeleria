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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class DueñoViewController {

    @FXML private Pane panel1, panel2;
    @FXML private Button BEmpleados, BInventario, BVentas;
    @FXML private Label titleLabel;

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
            titleLabel.setText("Error al cargar la escena");
        }
    }

    @FXML
    private void switchToAdministracionEmpleados() {
        switchToScene(BEmpleados, "src/main/resources/scenes/administracionEmpleados.fxml");
    }
    @FXML
    private void switchToInventario() {
        switchToScene(BInventario, "src/main/resources/scenes/inventario.fxml");
    }
    @FXML
    private void switchToVentas() {
        switchToScene(BVentas, "src/main/resources/scenes/ventas.fxml");
    }
}