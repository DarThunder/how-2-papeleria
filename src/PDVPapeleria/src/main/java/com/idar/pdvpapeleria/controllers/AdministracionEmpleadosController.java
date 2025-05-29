/**
 * Controlador para la gestión de empleados en el sistema PDV Papelería.
 * Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los empleados,
 * así como modificar sus atributos como nombre, código de seguridad, rol y estado.
 * 
 * <p>Esta clase maneja la interfaz gráfica correspondiente y se comunica con la capa de acceso a datos
 * a través de la interfaz EmpleadoDAO.</p>
 * 
 * @author laura
 */
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

    /**
     * Componentes de la interfaz gráfica y la lista de empleados
     */
    @FXML
    private Button BShowAgregar, BShowEliminar, BShowEditar, BAtras, BEliminar, BAgregar, BModificarNombre, BModificarCodigoSeguridad, BModificarRol, BModificarEstado;
    @FXML
    private Pane PAgregar, PEliminar, PEditar;
    @FXML
    private ComboBox<String> CBRol, CBParametros, CBEstado, CBRolModificar;
    @FXML
    private TextField TFNombre, TFContraseña, TFCodigoSeguridad, TFEliminar, TFBuscar, TFModificar, TFNuevoNombre, TFNuevoCodigoSeguridad;
    @FXML
    private TableView<EmpleadoVO> TV1;
    @FXML
    private TableColumn<EmpleadoVO, ?> Columna1, Columna2, Columna3, Columna4, Columna5;

    private EmpleadoDAO db;
    private ObservableList<EmpleadoVO> empleadosList;

    /**
     * Mapa que relaciona los nombres de los parámetros de búsqueda con sus 
     * correspondientes nombres de propiedades en la clase EmpleadoVO.
     */
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
     * Inicializa la instancia de EmpleadoDAOImp y configura los componentes.
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    @FXML
    private void initialize() throws SQLException {
        db = EmpleadoDAOImp.getInstance();
        configurarComponentes();
        cargarEmpleados();
        configurarColumnasIniciales();
        TFBuscar.setOnAction(event -> buscarEmpleado());
    }

    /**
     * Configura los componentes de la interfaz gráfica con sus valores iniciales.
     * La tabla y las columnas que esta contendra
     */
    private void configurarComponentes() {
        CBRol.getItems().addAll("Dueño", "Administrador", "Cajero");
        CBRolModificar.getItems().addAll("Dueño", "Administrador", "Cajero");
        CBEstado.getItems().addAll("Activo", "Inactivo");
        CBParametros.getItems().addAll(parametrosMap.keySet());
        CBParametros.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actualizarColumnas(newVal);
                BuscarText();
            }
        });
    }

    /**
     * Carga la lista de empleados desde la base de datos y los muestra en la tabla.
     * Con sus respectivas caracteristicas segun cuendo fueron agregados
     */
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

    /**
     * Configura las columnas inicial de la tabla con el parámetro "ID" como principal.
     */
    private void configurarColumnasIniciales() {
        actualizarColumnas("ID");
    }

    /**
     * Actualiza las columnas de la tabla según el parámetro principal seleccionado.
     * Y acomoda las demas columnas segun crea necesario.
     * @param parametroPrincipal El parámetro que determinará la primera columna
     */
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

    /**
     * Configura una columna específica de la tabla.
     * @param columna La columna a configurar
     * @param property La propiedad del objeto EmpleadoVO que se mostrará en la columna
     * @param titulo El título que se mostrará en la cabecera de la columna
     */
    private void configurarColumna(TableColumn<EmpleadoVO, ?> columna, String property, String titulo) {
        columna.setCellValueFactory(new PropertyValueFactory<>(property));
        columna.setText(titulo);
        columna.setPrefWidth(150);
    }

    /**
     * Realiza una búsqueda en tiempo real según el texto ingresado en el campo de búsqueda.
     * Se muestra en la tabla, segun el parametro ya seleccionado
     */
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

    /**
     * Obtiene el valor de un parámetro específico de un empleado.
     * @param empleado El empleado del cual se obtendrá el valor
     * @param parametro El parámetro cuyo valor se quiere obtener
     * @return El valor del parámetro solicitado, o null si no existe
     */
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

    /**
     * Muestra un mensaje de error en un diálogo.
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje informativo en un diálogo.
     * @param mensaje El mensaje a mostrar
     */
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    /**
     * Busca empleados según los criterios especificados en los campos de búsqueda.
     */
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

    /**
     * Verifica que todos los campos necesarios para agregar un empleado estén completos.
     * @return true si todos los campos están completos, false de lo contrario
     */
    @FXML
    private boolean verificarCamposAgregarEmpleado() {
        return !TFNombre.getText().trim().isEmpty()
                && !TFContraseña.getText().trim().isEmpty()
                && !TFCodigoSeguridad.getText().trim().isEmpty()
                && CBRol.getValue() != null;
    }

    /**
     * Método para validar que el código de seguridad tenga exactamente 4
     * caracteres.
     *
     * @param codigo El código de seguridad a validar.
     * @return true si tiene exactamente 4 caracteres, false de lo contrario.
     */
    private boolean validarCodigoSeguridad(String codigo) {
        return codigo.length() == 4;
    }

    /**
     * Valida que el código de seguridad en el campo correspondiente tenga 4 caracteres.
     * @return true si el código tiene 4 caracteres, false de lo contrario
     */
    @FXML
    private boolean validarCodigoSeguridad() {
        return TFCodigoSeguridad.getText().length() == 4;
    }

    /**
     * Agrega un nuevo empleado al sistema después de validar los campos.
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
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
            cargarEmpleados();
        } else {
            AlertaPDV.mostrarError("", "Error al agregar al empleado");
        }
    }

    /**
     * Crea un objeto EmpleadoVO con los datos proporcionados.
     * @param nombre El nombre del empleado
     * @param codigoSeguridad El código de seguridad del empleado
     * @param contraseña La contraseña del empleado
     * @param rol El rol del empleado
     * @return Un objeto EmpleadoVO con los datos proporcionados
     */
    private EmpleadoVO crearEmpleado(String nombre, String codigoSeguridad, String contraseña, String rol) {
        EmpleadoVO empleado = new EmpleadoVO();
        empleado.setNombre(nombre);
        empleado.setCodigoSeguridad(codigoSeguridad);
        empleado.setPassword(contraseña);
        empleado.setRol(rol);
        return empleado;
    }

    /**
     * Verifica si ya existe un empleado con el mismo nombre o código de seguridad.
     * @param empleado El empleado a verificar
     * @return true si ya existe un empleado con los mismos datos, false de lo contrario
     */
    public boolean existeEmpleado(EmpleadoVO empleado) {
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

    /**
     * Limpia los campos del formulario de agregar empleado.
     */
    private void limpiarCampos() {
        TFNombre.setText("");
        TFContraseña.setText("");
        TFCodigoSeguridad.setText("");
    }

    /**
     * Elimina un empleado del sistema después de validar su ID.
     */
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
        boolean confirmacion = AlertaPDV.mostrarConfirmacion(
                "Confirmación de eliminación",
                "¿Estás seguro de que deseas eliminar al empleado con ID: " + id + "?"
        );
        if (confirmacion) {
            if (db.eliminarEmpleado(id)) {
                AlertaPDV.mostrarExito("", "Empleado eliminado correctamente.");
                TFEliminar.setText("");
                cargarEmpleados();
            } else {
                AlertaPDV.mostrarError("Error al eliminar", "Se produjo un error al eliminar al empleado.");
            }
        } else {
            AlertaPDV.mostrarError("", "Eliminación cancelada.");
        }
    }

    /**
     * Modifica el nombre de un empleado existente.
     */
    @FXML
    private void modificarNombre() {
        String nuevoNombre = TFNuevoNombre.getText().trim();
        String idEmpleado = TFModificar.getText().trim();

        if (nuevoNombre.isEmpty() || idEmpleado.isEmpty()) {
            AlertaPDV.mostrarError("", "El ID de empleado y el nuevo nombre no pueden estar vacíos.");
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
        EmpleadoVO empleadoParaValidar = new EmpleadoVO();
        empleadoParaValidar.setNombre(nuevoNombre);
        if (existeEmpleado(empleadoParaValidar)) {
            return;
        }
        boolean confirmar = AlertaPDV.mostrarConfirmacion("Confirmar cambio", "¿Estás seguro de que deseas "
                + "cambiar el nombre del empleado?");
        if (!confirmar) {
            return;
        }

        if (db.actualizarNombreEmpleado(id, nuevoNombre)) {
            AlertaPDV.mostrarExito("", "Nombre del empleado actualizado correctamente.");
            cargarEmpleados();
        } else {
            AlertaPDV.mostrarError("", "Error al actualizar el nombre del empleado.");
        }
    }

    /**
     * Modifica el código de seguridad de un empleado existente.
     */
    @FXML
    private void modificarCodigoSeguridad() {
        String nuevoCodigoSeguridad = TFNuevoCodigoSeguridad.getText().trim();
        String idEmpleado = TFModificar.getText().trim();
        if (nuevoCodigoSeguridad.isEmpty() || idEmpleado.isEmpty()) {
            AlertaPDV.mostrarError("", "El ID de empleado y el nuevo código de seguridad no pueden estar vacíos.");
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
        if (!validarCodigoSeguridad(nuevoCodigoSeguridad)) {
            AlertaPDV.mostrarError("", "El código de seguridad debe tener exactamente 4 caracteres.");
            return;
        }
        EmpleadoVO empleadoParaValidar = new EmpleadoVO();
        empleadoParaValidar.setCodigoSeguridad(nuevoCodigoSeguridad);
        if (existeEmpleado(empleadoParaValidar)) {
            return;
        }
        boolean confirmar = AlertaPDV.mostrarConfirmacion("Confirmar cambio", "¿Estás seguro de que deseas cambiar el"
                + "código de seguridad del empleado?");
        if (!confirmar) {
            return;
        }
        boolean exito = db.actualizarCodigoSeguridadEmpleado(id, nuevoCodigoSeguridad);
        if (exito) {
            AlertaPDV.mostrarExito("", "Código de seguridad actualizado correctamente.");
            cargarEmpleados();
            TFNuevoCodigoSeguridad.setText("");
        } else {
            AlertaPDV.mostrarError("", "Error al actualizar el código de seguridad del empleado.");
        }
    }

    /**
     * Modifica el rol de un empleado existente.
     */
    @FXML
    private void modificarRol() {
        String nuevoRol = CBRolModificar.getValue();
        String idEmpleado = TFModificar.getText().trim();
        if (nuevoRol == null || idEmpleado.isEmpty()) {
            AlertaPDV.mostrarError("", "El ID de empleado y el nuevo rol no pueden estar vacíos.");
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
        boolean confirmar = AlertaPDV.mostrarConfirmacion("Confirmar cambio", "¿Estás seguro de que deseas "
                + "cambiar el rol del empleado?");
        if (!confirmar) {
            return;
        }
        if (db.actualizarRolEmpleado(id, nuevoRol)) {
            AlertaPDV.mostrarExito("", "Rol del empleado actualizado correctamente.");
            cargarEmpleados();
        } else {
            AlertaPDV.mostrarError("", "Error al actualizar el rol del empleado.");
        }
    }

    /**
     * Modifica el estado de un empleado existente.
     */
    @FXML
    private void modificarEstado() {
        String nuevoEstado = CBEstado.getValue();
        String idEmpleado = TFModificar.getText().trim();

        if (nuevoEstado == null || idEmpleado.isEmpty()) {
            AlertaPDV.mostrarError("", "El ID de empleado y el nuevo estado no pueden estar vacíos.");
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
        boolean confirmar = AlertaPDV.mostrarConfirmacion("Confirmar cambio", "¿Estás seguro de que deseas cambiar el estado del empleado?");
        if (!confirmar) {
            return;
        }
        if (db.actualizarEstadoEmpleado(id, nuevoEstado)) {
            AlertaPDV.mostrarExito("", "Estado del empleado actualizado correctamente.");
            cargarEmpleados();
        } else {
            AlertaPDV.mostrarError("", "Error al actualizar el estado del empleado.");
        }
    }
}