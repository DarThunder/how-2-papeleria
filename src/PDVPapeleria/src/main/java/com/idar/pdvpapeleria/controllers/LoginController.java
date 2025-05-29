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
import Vista.AlertaPDV;
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
 * También proporciona acceso a la verificación del código de seguridad para
 * cambiar la contraseña.
 *
 * @author dylxn999
 * @date 28/05/2025
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
     * Inicializa el controlador y establece la instancia de conexión con el DAO
     * de empleados.
     */
    @FXML
    public void initialize() {
        empleado = EmpleadoDAOImp.getInstance();
    }

    /**
     * Maneja el evento de inicio de sesión al hacer clic en el botón
     * correspondiente. Verifica que los campos no estén vacíos, comprueba la
     * existencia del usuario y la validez de sus credenciales, y redirige a la
     * vista correspondiente según el rol del usuario. En caso de error, muestra
     * una alerta con el motivo.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertaPDV.mostrarError("Campos incompletos", "Por favor, complete todos los campos.");
            return;
        }

        try {
            if (empleado.existeNombreUsuario(username)) {
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
                            AlertaPDV.mostrarError("Error", "Rol desconocido o no autorizado.");
                    }
                } else {
                    AlertaPDV.mostrarError("Error de autenticación", "Usuario inactivo.");
                }
            } else {
                AlertaPDV.mostrarError("Error de autenticación", "Nombre de usuario incorrecto.");
            }
        } catch (SQLException | IOException e) {
            AlertaPDV.mostrarExcepcion("Error de conexión con la base de datos", "Error al iniciar sesión, inténtelo más tarde", e);
            e.printStackTrace();
        }
    }

    /**
     * Cambia la escena actual a una nueva vista según la ruta proporcionada del
     * archivo FXML.
     *
     * @param fxmlPath Ruta relativa del archivo FXML de la nueva vista.
     * @throws IOException si ocurre un error al cargar la vista.
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
     * Cambia la vista actual a la interfaz del Dueño.
     *
     * @throws IOException si ocurre un error al cargar la vista.
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
     * Cambia la vista actual a la interfaz del Administrador.
     *
     * @throws IOException si ocurre un error al cargar la vista.
     */
    @FXML
    private void switchToAdminView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/OpcionesAdministrador.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Cambia la vista actual a la interfaz del Cajero.
     *
     * @throws IOException si ocurre un error al cargar la vista.
     */
    @FXML
    private void switchToCajeroView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/abrirCajaView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) B1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Cambia la vista actual a la pantalla de verificación de código de
     * seguridad, utilizada para cambiar la contraseña del usuario.
     *
     * @throws IOException si ocurre un error al cargar la vista.
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
