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
import Vista.AlertaPDV;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdministracionEmpleadosController {

    @FXML
    private Button BShowAgregar, BShowEliminar, BShowEditar, BAtras, BEliminar, BAgregar;
    @FXML
    private Pane PAgregar, PEliminar, PEditar;
    @FXML
    private ComboBox<String> CBRol, CBParametros, CBEstado;
    @FXML
    private TextField TFNombre, TFContraseña, TFCodigoSeguridad, TFEliminar, TFBuscar;
    @FXML
    private TableView<EmpleadoVO> TV1;
    @FXML
    private TableColumn<EmpleadoVO, ?> Columna1, Columna2, Columna3, Columna4, Columna5;

    private EmpleadoDAO db;
    private ObservableList<EmpleadoVO> empleadosList;

    private final Map<String, String> parametrosMap = new LinkedHashMap<String, String>() {
        {
            put("ID", "id");
            put("Nombre", "nombre");
            put("Rol", "rol");
            put("Código de Seguridad", "codigoSeguridad");
            put("Estado", "estado");
        }
    };

    /**
     * Método de inicialización de la interfaz. Se ejecuta automáticamente
     * cuando la vista es cargada.
     *
     * Inicializa la instancia de EmpleadoDAOImp.
     */
    @FXML
    private void initialize() throws SQLException {
        db = EmpleadoDAOImp.getInstance();
        configurarComponentes();
        cargarEmpleados();
        configurarColumnasIniciales();
        TFBuscar.setOnAction(event -> buscarEmpleado());
    }

    private void configurarComponentes() {
        CBRol.getItems().addAll("Dueño", "Administrador", "Cajero");
        CBEstado.getItems().addAll("Activo", "Inactivo");
        CBParametros.getItems().addAll(parametrosMap.keySet());
        CBParametros.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actualizarColumnas(newVal);
                BuscarText();
            }
        });
    }

    private void cargarEmpleados() {
        try {
            List<EmpleadoVO> empleados = db.obtenerTodosEmpleados();
            empleadosList = FXCollections.observableArrayList(empleados);
            TV1.setItems(empleadosList);
        } catch (SQLException e) {
            mostrarError("Error al cargar empleados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarColumnasIniciales() {
        actualizarColumnas("ID");
    }

    private void actualizarColumnas(String parametroPrincipal) {
        List<String> otrosParametros = new ArrayList<>(parametrosMap.keySet());
        otrosParametros.remove(parametroPrincipal);
        TV1.getColumns().clear();
        configurarColumna(Columna1, parametrosMap.get(parametroPrincipal), parametroPrincipal);
        configurarColumna(Columna2, parametrosMap.get(otrosParametros.get(0)), otrosParametros.get(0));
        configurarColumna(Columna3, parametrosMap.get(otrosParametros.get(1)), otrosParametros.get(1));
        configurarColumna(Columna4, parametrosMap.get(otrosParametros.get(2)), otrosParametros.get(2));
        configurarColumna(Columna5, parametrosMap.get(otrosParametros.get(3)), otrosParametros.get(3));
        TV1.getColumns().addAll(Columna1, Columna2, Columna3, Columna4, Columna5);
        TV1.setItems(empleadosList);
    }

    private void configurarColumna(TableColumn<EmpleadoVO, ?> columna, String property, String titulo) {
        columna.setCellValueFactory(new PropertyValueFactory<>(property));
        columna.setText(titulo);
        columna.setPrefWidth(150);
    }

    @FXML
    private void BuscarText() {
        String textoBusqueda = TFBuscar.getText().toLowerCase();
        String parametro = CBParametros.getValue();
        if (parametro != null && !textoBusqueda.isEmpty()) {
            ObservableList<EmpleadoVO> resultados = FXCollections.observableArrayList();
            for (EmpleadoVO empleado : empleadosList) {
                String valor = getValorParametro(empleado, parametro);
                if (valor != null && valor.toLowerCase().contains(textoBusqueda)) {
                    resultados.add(empleado);
                }
            }
            TV1.setItems(resultados);
        } else {
            TV1.setItems(empleadosList);
        }
    }

    private String getValorParametro(EmpleadoVO empleado, String parametro) {
        switch (parametro) {
            case "Nombre":
                return empleado.getNombre();
            case "Rol":
                return empleado.getRol();
            case "Código de Seguridad":
                return empleado.getCodigoSeguridad();
            case "Estado":
                return empleado.getEstado();
            case "ID":
                return String.valueOf(empleado.getId());
            default:
                return null;
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    @FXML
    private void buscarEmpleado() {
        String textoBusqueda = TFBuscar.getText().trim();
        String parametro = CBParametros.getValue();

        if (parametro == null) {
            mostrarError("Por favor seleccione un parámetro de búsqueda");
            return;
        }
        if (textoBusqueda.isEmpty()) {
            TV1.setItems(empleadosList); 
            return;
        }

        ObservableList<EmpleadoVO> resultados = FXCollections.observableArrayList();

        for (EmpleadoVO empleado : empleadosList) {
            String valor = getValorParametro(empleado, parametro);
            if (valor != null && valor.toLowerCase().contains(textoBusqueda.toLowerCase())) {
                resultados.add(empleado);
            }
        }

        TV1.setItems(resultados);

        if (resultados.isEmpty()) {
            mostrarMensaje("No se encontraron empleados con esos criterios");
        }
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

    @FXML
    private boolean verificarCamposAgregarEmpleado() {
        return !TFNombre.getText().trim().isEmpty()
                && !TFContraseña.getText().trim().isEmpty()
                && !TFCodigoSeguridad.getText().trim().isEmpty()
                && CBRol.getValue() != null;
    }

    @FXML
    private boolean validarCodigoSeguridad() {
        return TFCodigoSeguridad.getText().length() == 4;
    }

    @FXML
    private void agregarEmpleado() throws SQLException {
        if (!verificarCamposAgregarEmpleado()) {
            AlertaPDV.mostrarError("", "Todos los campos deben estar completos.");
            return;
        }
        if (!validarCodigoSeguridad()) {
            AlertaPDV.mostrarError("", "El código de seguridad debe tener exactamente 4 caracteres.");
            return;
        }
        String nombre = TFNombre.getText();
        String codigoSeguridad = TFCodigoSeguridad.getText();
        String contraseña = TFContraseña.getText();
        String rol = CBRol.getValue();

        EmpleadoVO empleado = crearEmpleado(nombre, codigoSeguridad, contraseña, rol);

        if (existeEmpleado(empleado)) {
            return;
        }
        if (db.createUser(empleado)) {
            AlertaPDV.mostrarExito("", "Empleado agregado correctamente");
            limpiarCampos();
        } else {
            AlertaPDV.mostrarError("", "Error al agregar al empleado");
        }
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
            AlertaPDV.mostrarError("", "El nombre de usuario ya existe.");
            return true;
        }
        if (db.existeCodigoSeguridad(empleado.getCodigoSeguridad())) {
            AlertaPDV.mostrarError("", "El código de seguridad ya existe.");
            return true;
        }
        return false;
    }

    private void limpiarCampos() {
        TFNombre.setText("");
        TFContraseña.setText("");
        TFCodigoSeguridad.setText("");
    }

    @FXML
    private void eliminarEmpleado() {
        String idEmpleado = TFEliminar.getText().trim();
        if (idEmpleado.isEmpty()) {
            AlertaPDV.mostrarError("", "Debe ingresar el ID del empleado a eliminar.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idEmpleado);
        } catch (NumberFormatException e) {
            AlertaPDV.mostrarError("", "El ID debe ser un número entero.");
            return;
        }
        if (!db.existeEmpleadoPorId(id)) {
            AlertaPDV.mostrarError("", "No existe un empleado con ese ID.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Estás seguro de que deseas eliminar al empleado con ID: " + id + "?",
                "Confirmación de eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (db.eliminarEmpleado(id)) {
                AlertaPDV.mostrarExito("", "Empleado eliminado correctamente.");
                TFEliminar.setText("");
            } else {
                AlertaPDV.mostrarError("Error al eliminar", "Se produjo un error al eliminar al empleado.");
            }
        } else {
            AlertaPDV.mostrarError("", "Eliminación cancelada.");
        }
    }

}
