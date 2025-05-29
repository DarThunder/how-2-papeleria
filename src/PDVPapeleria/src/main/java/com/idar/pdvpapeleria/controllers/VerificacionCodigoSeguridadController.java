package com.idar.pdvpapeleria.controllers;

import DAO.EmpleadoDAO;
import DAOImp.EmpleadoDAOImp;
import Vista.AlertaPDV;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controlador de la vista de verificación del código de seguridad.
 *
 * Esta clase permite a los usuarios validar su identidad mediante un código de
 * seguridad asociado a su nombre de usuario. Si la verificación es exitosa, se
 * redirige a la pantalla para cambiar la contraseña.
 *
 * También permite regresar a la pantalla de inicio de sesión si el usuario así
 * lo desea.
 *
 * @author dylxn999
 * @date 28/05/2025
 */
public class VerificacionCodigoSeguridadController {

    @FXML
    private TextField TFNombre, TFCodigoSeguridad;

    @FXML
    private Button BVerificar, BAtras;

    @FXML
    private Label L1;

    @FXML
    private Text L2, L3;

    private EmpleadoDAO db;
    private String codigoSeguridad;

    /**
     * Constructor de la clase.
     *
     * Inicializa la instancia del DAO de empleados para la verificación del
     * código.
     */
    public VerificacionCodigoSeguridadController() {
        db = EmpleadoDAOImp.getInstance();
    }

    /**
     * Verifica que el nombre de usuario y el código de seguridad ingresados
     * sean válidos.
     *
     * Si los campos están vacíos o el código no tiene 4 caracteres, se muestra
     * un error. Si los datos son correctos, se redirige a la vista para cambiar
     * la contraseña.
     */
    @FXML
    private void verificarCodigoSeguridad() {
        String username = TFNombre.getText().trim();
        String codigoSeguridad = TFCodigoSeguridad.getText().trim();

        if (username.isEmpty() || codigoSeguridad.isEmpty()) {
            AlertaPDV.mostrarError("Campos incompletos", "Por favor complete todos los campos.");
            return;
        }

        if (codigoSeguridad.length() != 4) {
            AlertaPDV.mostrarError("Código de seguridad inválido", "El código de seguridad debe "
                    + "tener exactamente 4 caracteres.");
            return;
        }

        if (db.verificarCodigoSeguridad(username, codigoSeguridad)) {
            try {
                switchToCambiarContraseña(codigoSeguridad);
            } catch (Exception e) {
                AlertaPDV.mostrarExcepcion("Error de carga", "Error al cargar la pantalla de "
                        + "cambiar contraseña", e);
            }
        } else {
            AlertaPDV.mostrarError("Error", "Código de seguridad incorrecto o usuario no encontrado.");
        }
    }

    /**
     * Cambia la vista actual a la pantalla para cambiar la contraseña.
     *
     * Pasa el código de seguridad validado al controlador de la nueva vista.
     *
     * @param codigoSeguridad el código de seguridad que fue validado
     * correctamente.
     * @throws IOException si ocurre un error al cargar el archivo FXML.
     */
    @FXML
    private void switchToCambiarContraseña(String codigoSeguridad) throws IOException {
        System.out.println("Código de seguridad a pasar: " + codigoSeguridad);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/cambiarContraseña.fxml"));
        Parent root = loader.load();
        CambiarContraseñaController controller = loader.getController();
        controller.setCodigoSeguridad(codigoSeguridad);
        Stage stage = (Stage) BVerificar.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Regresa a la pantalla de inicio de sesión.
     *
     * Este método se ejecuta cuando el usuario decide no continuar con el
     * cambio de contraseña.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML de
     * login.
     */
    @FXML
    private void switchToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) BAtras.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
}
