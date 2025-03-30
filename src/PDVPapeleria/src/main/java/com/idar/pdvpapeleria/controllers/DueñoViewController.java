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

public class DueñoViewController {
    
    @FXML
    private Pane P1,P2;
    @FXML
    private Button BEmpleados, BInventario, BVentas;
    @FXML
    private Label L1;

    @FXML
    private void switchToAdministracionEmpleados() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/administracionEmpleados.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BEmpleados.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
}
