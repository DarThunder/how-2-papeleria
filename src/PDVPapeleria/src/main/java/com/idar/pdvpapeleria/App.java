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
import DAO.EmpleadoDAO;
import DAOImp.EmpleadoDAOImp;
import VO.EmpleadoVO;
import com.idar.pdvpapeleria.controllers.LoginController;
import java.io.File;
import static javafx.application.Application.launch;

/**
 * Clase principal de la aplicación JavaFX. Se encarga de inicializar la ventana
 * principal y cargar la escena del login.
 */
public class App extends Application {

    /**
     * Método start que inicia la aplicación de JavaFX
     * @param stage El stage para iniciar 
     * @throws IOException 
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = loadFXML("login");
        Parent root = loader.load();
        LoginController controller = loader.getController();
        Scene scene = new Scene(root, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/css/Estilos.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método para cargar un FXML de acuerdo a su ruta
     * @param fxml El nombre de la vista FXML 
     * @return La URL del FXML
     * @throws IOException 
     */
    private FXMLLoader loadFXML(String fxml) throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/" + fxml + ".fxml");
        return new FXMLLoader(fxmlFile.toURI().toURL());
    }

    /**
     * Main de la aplicación, donde se agrega un Cajero, un Administrador y un Dueño, además de llamar al método
     * launch
     * @param args 
     */
    public static void main(String[] args) {
        EmpleadoVO nuevoEmpleado = new EmpleadoVO("Cajero", "9998", "Juan", "321");
        EmpleadoVO nuevoAdmin = new EmpleadoVO("Administrador", "8888", "Luis", "123");
        EmpleadoVO nuevoDueño = new EmpleadoVO("Dueño", "7777", "Jose", "456");
        EmpleadoDAO empleadoDAO = new EmpleadoDAOImp();
        boolean success = empleadoDAO.createUser(nuevoAdmin) && empleadoDAO.createUser(nuevoDueño) && empleadoDAO.createUser(nuevoEmpleado);
        if (success) {
            System.out.println("Usuarios agregado correctamente.");
        } else {
            System.out.println("Error al agregar los usuarios.");
        }
        launch(args);
    }
}
