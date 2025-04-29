package com.idar.pdvpapeleria.controllers;

import DAO.EmpleadoDAO;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import DAOImp.EmpleadoDAOImp;
import VO.EmpleadoVO;

public class AdministracionEmpleadosController {

    @FXML
    private Button BShowAgregar, BShowEliminar, BShowEditar, BAtras;
    @FXML
    private Pane PAgregar, PEliminar, PEditar;
    @FXML
    private ComboBox<String> CBRol;
    @FXML
    private TextField TFNombre, TFContraseña, TFCodigoSeguridad;
    @FXML
    private TableView TV1;
    private EmpleadoDAO db;

    /**
     * Método de inicialización de la interfaz. Se ejecuta automáticamente
     * cuando la vista es cargada.
     *
     * Inicializa la instancia de EmpleadoDAOImp.
     */
    @FXML
    private void initialize() throws SQLException {
        db = EmpleadoDAOImp.getInstance();
        CBRol.getItems().addAll("Dueño", "Administrador", "Cajero");
    }

    /**
     * Cambia la escena actual a la pantalla de inicio de sesión. Este método
     * carga el archivo FXML correspondiente a la pantalla de inicio de sesión y
     * establece esa escena como la activa.
     *
     * @throws IOException si ocurre un error al cargar el archivo FXML
     * correspondiente.
     */
    @FXML
    private void switchToDueñoView() throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/dueñoView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) BAtras.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Muestra el panel para agregar un empleado y oculta los demás paneles.
     * Este método se utiliza para cambiar la interfaz y permitir al usuario
     * agregar un empleado específico.
     */
    @FXML
    private void mostrarPanelAgregar() {
        PAgregar.setVisible(true);
        PEliminar.setVisible(false);
        PEditar.setVisible(false);
    }

    /**
     * Muestra el panel para eliminar un empleado y oculta los demás paneles.
     * Este método se utiliza para cambiar la interfaz y permitir al usuario
     * eliminar un empleado específico.
     */
    @FXML
    private void mostrarPanelEliminar() {
        PAgregar.setVisible(false);
        PEliminar.setVisible(true);
        PEditar.setVisible(false);
    }

    /**
     * Muestra el panel para editar los datos de un empleado y oculta los demás
     * paneles. Este método se utiliza para cambiar la interfaz y permitir al
     * usuario editar los datos de un empleado específico.
     */
    @FXML
    private void mostrarPanelEditar() {
        PAgregar.setVisible(false);
        PEliminar.setVisible(false);
        PEditar.setVisible(true);
    }

    /**
     * Verifica si los campos de entrada son válidos. Los campos que se
     * verifican incluyen: nombre, contraseña, rol y código de seguridad. El
     * código de seguridad debe tener exactamente 4 caracteres.
     *
     * @return true si todos los campos están completos y el código de seguridad
     * es válido; false en caso contrario.
     */
    @FXML
    private boolean verificarCampos() {
        boolean camposCompletos = !TFNombre.getText().trim().isEmpty()
                && !TFContraseña.getText().trim().isEmpty()
                && CBRol.getValue() != null;
        boolean codigoSeguridadValido = TFCodigoSeguridad.getText().length() == 4;
        if (!camposCompletos) {
            return false;
        }
        return codigoSeguridadValido;
    }

    /**
     * Agrega un nuevo empleado al sistema. Verifica primero que los campos sean
     * válidos y que el nombre de usuario y el código de seguridad no existan.
     * Si los datos son correctos, se crea el usuario en la base de datos. Si
     * ocurre algún error, se muestra un mensaje adecuado.
     *
     * @throws SQLException si ocurre un error al interactuar con la base de
     * datos.
     */
    @FXML
    private void agregarEmpleado() throws SQLException {
        if (!verificarCampos()) {
            mostrarError("Todos los campos deben estar completos");
            return;
        }

        String nombre = TFNombre.getText();
        String codigoSeguridad = TFCodigoSeguridad.getText();
        String contraseña = TFContraseña.getText();
        String rol = CBRol.getValue();

        if (!validarCodigoSeguridad(codigoSeguridad)) {
            mostrarError("El código de seguridad debe tener exactamente 4 caracteres.");
            return;
        }

        EmpleadoVO empleado = crearEmpleado(nombre, codigoSeguridad, contraseña, rol);

        if (existeEmpleado(empleado)) {
            return;
        }

        if (db.createUser(empleado)) {
            mostrarMensaje("Empleado agregado correctamente");
            limpiarCampos();
        } else {
            mostrarError("Error al agregar al empleado");
        }
    }

    private boolean validarCodigoSeguridad(String codigoSeguridad) {
        return codigoSeguridad.length() == 4;
    }

    private EmpleadoVO crearEmpleado(String nombre, String codigoSeguridad, String contraseña, String rol) {
        EmpleadoVO empleado = new EmpleadoVO();
        empleado.setNombre(nombre);
        empleado.setCodigoSeguridad(codigoSeguridad);
        empleado.setPassword(contraseña);
        empleado.setRol(rol);
        return empleado;
    }

    private boolean existeEmpleado(EmpleadoVO empleado) {
        if (db.existeNombreUsuario(empleado.getNombre())) {
            mostrarError("El nombre de usuario ya existe.");
            return true;
        }
        if (db.existeCodigoSeguridad(empleado.getCodigoSeguridad())) {
            mostrarError("El código de seguridad ya existe.");
            return true;
        }
        return false;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    private void limpiarCampos() {
        TFNombre.setText("");
        TFContraseña.setText("");
        TFCodigoSeguridad.setText("");
    }

}
