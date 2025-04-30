/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.idar.pdvpapeleria;

/**
 *
 * @author DYLAN
 */
import DAO.DatabaseConnection;
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
import java.sql.SQLException;
import static javafx.application.Application.launch;

/**
 * Clase principal de la aplicación JavaFX. Se encarga de inicializar la ventana
 * principal y cargar la escena del login.
 */
public class App extends Application {

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

    private FXMLLoader loadFXML(String fxml) throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/" + fxml + ".fxml");
        return new FXMLLoader(fxmlFile.toURI().toURL());
    }

    public static void main(String[] args) throws SQLException {
        DatabaseConnection.getInstance();
        
        EmpleadoVO nuevoEmpleado = new EmpleadoVO("Administrador", "8888", "Luis", "123");
        EmpleadoDAO empleadoDAO = new EmpleadoDAOImp();
        boolean success = empleadoDAO.createUser(nuevoEmpleado);
        //boolean success = false;
        if (success) {
            System.out.println("Usuario agregado correctamente.");
        } else {
            System.out.println("Error al agregar el usuario.");
        }
        launch(args);
    }
}
