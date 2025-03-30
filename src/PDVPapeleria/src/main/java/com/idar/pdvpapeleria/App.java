/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.idar.pdvpapeleria;

/**
 *
 * @author DYLAN
 */
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lib.SqlLib;
import com.idar.pdvpapeleria.controllers.LoginController;
import java.io.File;
import static javafx.application.Application.launch;

/**
 * Clase principal de la aplicación JavaFX.
 * Se encarga de inicializar la ventana principal y cargar la escena del login.
 */
public class App extends Application {
    private static SqlLib db;

    /**
     * Método de inicio de la aplicación JavaFX.
     * @param stage La ventana principal de la aplicación.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = loadFXML("login");
        Parent root = loader.load();
        LoginController controller = loader.getController();
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Carga un archivo FXML desde la carpeta de recursos.
     *
     * @param fxml Nombre del archivo FXML (sin la extensión).
     * @return FXMLLoader con el archivo cargado.
     * @throws IOException Si ocurre un error al acceder al archivo.
     */
    private FXMLLoader loadFXML(String fxml) throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/" + fxml + ".fxml");
        return new FXMLLoader(fxmlFile.toURI().toURL());
    }

    /**
     * Método principal que inicia la aplicación JavaFX.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch();
    }
}

