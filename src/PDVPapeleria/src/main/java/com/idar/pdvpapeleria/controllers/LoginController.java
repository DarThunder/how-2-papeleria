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
import DAO.EmpleadoDAO;
import DAOImp.EmpleadoDAOImp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

/**
 * Controlador de la vista de inicio de sesión en la aplicación.
 *
 * Este controlador maneja la interacción del usuario con la pantalla de inicio
 * de sesión, valida las credenciales de usuario y cambia entre las vistas
 * correspondientes según el rol del usuario (Dueño, Administrador o Cajero).
 *
 * @author Dylan
 */
public class LoginController {

    @FXML
    private Button B1, BCambiarContraseña;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessageLabel;
    private EmpleadoDAO empleado;

    /**
     * Inicializa el controlador. Establece la conexión a la base de datos. Si
     * no se puede conectar a la base de datos, muestra un mensaje de error.
     */
    @FXML
    public void initialize() {
        empleado = EmpleadoDAOImp.getInstance();
    }

    /**
     * Maneja el evento de inicio de sesión cuando el usuario hace clic en el
     * botón de inicio de sesión. Verifica las credenciales proporcionadas por
     * el usuario y redirige a la vista correspondiente según el rol del usuario
     * (Dueño, Administrador, Cajero). Si las credenciales no son válidas,
     * muestra un mensaje de error.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (empleado.isValidCredentials(username, password)) {
                String role = empleado.getRole(username);
                switch (role) {
                    case "Dueño" ->
                        switchToDueñoView();
                    case "Administrador" ->
                        switchToAdminView();
                    case "Cajero" ->
                        switchToCajeroView();
                    default ->
                        errorMessageLabel.setText("Rol desconocido o no autorizado.");
                }
            } else {
                errorMessageLabel.setText("Nombre de usuario o contraseña incorrectos.");
            }
        } catch (SQLException | IOException e) {
            errorMessageLabel.setText("Error al iniciar sesión.");
            e.printStackTrace();
        }
    }

    /**
     * Cambia la escena actual a una nueva vista especificada por el archivo
     * FXML.
     *
     * @param fxmlPath la ruta del archivo FXML que representa la nueva vista.
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    private void switchToView(String fxmlPath) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            errorMessageLabel.setText("Error al cargar la vista.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Cambia la escena actual a la pantalla de opciones del Dueño.
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
     * Cambia la escena actual a la pantalla de opciones del Administrador.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    @FXML
    private void switchToAdminView() throws IOException {
        //File fxmlFile = new File("src/main/resources/scenes/adminInventarioView.fxml");
        //CARLOS ACA PON LA INTERFAZ DONDE SELECCIONAS SI ES LA GESTION DE PROVEEDORES O DE PRODUCTOS
        File fxmlFile = new File("src/main/resources/scenes/adminProveedoresView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Cambia la escena actual a la pantalla de opciones del Cajero.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    @FXML
    private void switchToCajeroView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/cajeroView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Cambia la escena actual a la pantalla de verificación del código de
     * seguridad.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    @FXML
    private void switchToVerificacionCodigoSeguridad() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/verificacionCodigoSeguridad.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BCambiarContraseña.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
}
