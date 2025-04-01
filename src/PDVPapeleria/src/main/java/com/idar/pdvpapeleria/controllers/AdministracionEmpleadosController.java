package com.idar.pdvpapeleria.controllers;

import com.idar.pdvpapeleria.objects.Empleado;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import lib.SqlLib;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdministracionEmpleadosController {

    @FXML
    private Button BShowAgregar, BShowEliminar, BShowEditar, BAtras;
    @FXML
    private Pane PAgregar, PEliminar, PEditar;
    @FXML
    private ComboBox<String> CBRol;  // Asegurándote de que es un ComboBox de tipo String
    @FXML
    private TextField TFNombre, TFContraseña;
    @FXML
    private TableView<Empleado> TV1;
    @FXML
    private TableColumn<Empleado, Integer> colID;
    @FXML
    private TableColumn<Empleado, String> colNombre;
    @FXML
    private TableColumn<Empleado, String> colContraseña;
    @FXML
    private TableColumn<Empleado, String> colRol;
    @FXML
    private TableColumn<Empleado, String> colEstado;
    
    private ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        // Configurar TableView
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContraseña.setCellValueFactory(new PropertyValueFactory<>("contraseña"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar ComboBox
        CBRol.getItems().addAll("Dueño", "Administrador", "Cajero");

        try {
            this.pdvpapeleria = SqlLib.getInstance("", "", "");
            cargarEmpleados();
        } catch (SQLException e) {
            mostrarAlertaError("Error de conexión", "No se pudo conectar a la base de datos");
            e.printStackTrace();
        }
    }
    
    private void mostrarAlertaError(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}

private void mostrarAlertaInformacion(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}
    
    @FXML
    private void agregarEmpleado() {
        if (!verificarCampos()) {
            mostrarAlertaError("Error", "Todos los campos deben estar completos");
            return;
        }

        String nombre = TFNombre.getText();
        String contraseña = TFContraseña.getText();
        String rol = CBRol.getValue();

        try {
            // Verifica y reconecta si es necesario
            if (pdvpapeleria.getConnection() == null || pdvpapeleria.getConnection().isClosed()) {
                pdvpapeleria.connect();
            }

            if (pdvpapeleria.createUser(rol, nombre, contraseña)) { 
                mostrarAlertaInformacion("Éxito", "Empleado agregado correctamente");
                TFNombre.clear();
                TFContraseña.clear();
                cargarEmpleados();
            } else {
                mostrarAlertaError("Error", "No se pudo agregar al empleado");
            }
        } catch (SQLException e) {
            mostrarAlertaError("Error de base de datos", 
                "Error al agregar empleado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cargarEmpleados() {
    listaEmpleados.clear();
    String sql = "SELECT * FROM Empleados";

    try (Connection conn = pdvpapeleria.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            listaEmpleados.add(new Empleado(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("contraseña"),
                    rs.getString("rol"),
                    rs.getString("estado")
            ));
        }

        TV1.setItems(listaEmpleados);
    } catch (SQLException e) {
        mostrarAlertaError("Error de base de datos", "No se pudieron cargar los empleados");
        e.printStackTrace();
    }
}

    private SqlLib pdvpapeleria;


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

    @FXML
    private boolean verificarCampos() {
        return !TFNombre.getText().trim().isEmpty()
                && !TFContraseña.getText().trim().isEmpty()
                && CBRol.getValue() != null;
    }

    /*@FXML
    private void agregarEmpleado() throws SQLException {
        if (!verificarCampos()) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nombre = TFNombre.getText();
        String contraseña = TFContraseña.getText();
        String rol = CBRol.getValue();
        if (db.createUser(rol, nombre, contraseña)) { 
            JOptionPane.showMessageDialog(null, "Empleado agregado correctamente");
            TFNombre.setText("");
            TFContraseña.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Error al agregar al empleado",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/
}
