package com.idar.pdvpapeleria.controllers;

import DAOImp.ProveedorDAOImp;
import VO.ProveedorVO;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import DAO.DatabaseConnection;
import VO.HistorialProveedorVO;
import VO.ProductoVO;
import Vista.AlertaPDV;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AdminProveedoresViewController implements Initializable {

    AlertaPDV alerta = new AlertaPDV();
    List<ProveedorVO> proveedores = new ArrayList<ProveedorVO>();
    List<HistorialProveedorVO> historialCambios = new ArrayList<>();

    public int Id;
    private DatabaseConnection db;
    private ProveedorDAOImp proveedorDAO;
    public static final String REGEX_TELEFONO = "^[0-9]{10}$";

    @FXML
    private TableView<Object> tablaProveedores;

    @FXML
    private TableColumn<Object, Integer> colPrincipal;

    @FXML
    private TableColumn<Object, String> colSecundaria;

    @FXML
    private TableColumn<Object, String> colTerciaria;

    @FXML
    private TableColumn<Object, String> colCuaternaria;

    //Botones
    @FXML
    private Button agregarViewButton;

    @FXML
    private Button editarViewButton;

    @FXML
    private Button eliminarViewButton;

    @FXML
    private Button regresarViewButton;

    @FXML
    private Button accionButton;

    @FXML
    private Button resetearCamposButton;

    @FXML
    private Button historialButton;

    //Texts
    @FXML
    private Text descripcionLabel;

    @FXML
    private Label labelNombre;

    @FXML
    private Label labelServicio;

    @FXML
    private Label labelTelefono;

    //TextFields
    @FXML
    private TextField TF_Nombre;

    @FXML
    private TextField TF_Servicio;

    @FXML
    private TextField TF_Telefono;
    
    @FXML private ComboBox<String> comboBoxProveedores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setDB();
            proveedorDAO = new ProveedorDAOImp(db);
            configurarTabla();
            cargarProveedores();
            mostrarInfo();
            cargarComboBoxProveedores();

        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            alerta.mostrarError("Error BD", "Error al conectar a la base de datos");
        }
    }

    /**
     * Agarra el contenido de los textFields, valida que no esten vacios o
     * incorrectos y si todo esta bien, manda a llamar al metodo
     * "agregarProveedor" del objeto "proveedorDAO" Adivina que hace el metodo
     * ese..
     *
     * Accion que ocurre al presionar el boton "Crear Nuevo Proveedor"
     */
    @FXML
    private void agregarProveedor() {
        String Nombre = TF_Nombre.getText();
        String Servicio = TF_Servicio.getText();
        String Telefono = TF_Telefono.getText();

        if (validarCampos(Nombre, Servicio, Telefono)) {
            ProveedorVO p = new ProveedorVO(0, Nombre, Servicio, Telefono);
            try {
                proveedorDAO.agregarProveedor(p);
                cargarProveedores();
                alerta.mostrarExito("Exito", "Se agregó correctamente al proveedor");
                resetearCampos();
            } catch (SQLException e) {
                alerta.mostrarExcepcion("Error", "Error al agregar proveedor", e);
                System.err.println("Error al agregar proveedor: " + e.getMessage());
            }
        }
    }

    @FXML
    private void editarProveedor() {
        String nombreNuevo = TF_Nombre.getText();
        String servicioNuevo = TF_Servicio.getText();
        String telefonoNuevo = TF_Telefono.getText();

        if (validarCampos(nombreNuevo, servicioNuevo, telefonoNuevo)) {
            try {
                ProveedorVO proveedorViejo = proveedorDAO.obtenerProveedorPorId(Id);

                proveedorDAO.editarProveedor(Id, nombreNuevo, servicioNuevo, telefonoNuevo);

                // Registrar cambio en historial
                String fechaHora = java.time.LocalDateTime.now().toString();
                HistorialProveedorVO cambio = new HistorialProveedorVO(
                        Id,
                        proveedorViejo.getNombreProveedor(),
                        proveedorViejo.getServicioProveedor(),
                        proveedorViejo.getTelefonoProveedor(),
                        nombreNuevo,
                        servicioNuevo,
                        telefonoNuevo,
                        fechaHora
                );
                historialCambios.add(cambio);

                cargarProveedores();
                alerta.mostrarExito("Éxito", "Proveedor editado correctamente");
                resetearCampos();

            } catch (SQLException e) {
                alerta.mostrarExcepcion("Error", "Error al editar proveedor", e);
                System.err.println("Error al editar proveedor: " + e.getMessage());
            }
        }
    }

    /**
     * Se saca el indice seleccionado de la tabla, este junto al arraylist
     * "provedores" son utilizados para el metodo "eliminarProveedor" del objeto
     * "proveedorDAO". Adivina que es lo que hace ese metodo...
     *
     * Accion que ocurre al presionar el boton "Eliminar Proveedor"
     */
    @FXML
    private void eliminarProveedor() throws SQLException {
        int indice = tablaProveedores.getSelectionModel().getSelectedIndex();
        //System.out.println("Indice Seleccionado: " + indice);

        if (indice > -1) {
            boolean resultado = alerta.mostrarConfirmacion("Eliminar",
                    "¡Seguro que desea eliminar el siguiente proveedor?");
            if (resultado) {
                proveedorDAO.eliminarProveedor(proveedores, indice);
                cargarProveedores();
                alerta.mostrarExito("Exito", "El proveedor fue eliminado");
            } else {
                alerta.mostrarExito("Proceso Cancelado", "Operación cancelada exitosamente");
            }
        } else {
            alerta.mostrarError("Error", "Selecciona a un proveedor de la tabla");
        }
    }

    public void accionButtonClicked() throws SQLException {
        switch (accionButton.getText()) {
            case "Agregar Proveedor":
                agregarProveedor();
                break;

            case "Guardar Cambios":
                editarProveedor();
                break;

            case "Eliminar Proveedor":
                eliminarProveedor();
                break;

            default:
                System.out.println("Acción no reconocida: " + accionButton.getText());
                break;
        }
    }

    public void salirPresionado() throws MalformedURLException, IOException {
        File fxmlFile = new File("src/main/resources/scenes/OpcionesAdministrador.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) regresarViewButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void mostrarItems() {
        labelNombre.setVisible(true);
        labelNombre.setManaged(true);
        labelServicio.setVisible(true);
        labelServicio.setManaged(true);
        labelTelefono.setVisible(true);
        labelTelefono.setManaged(true);

        TF_Nombre.setVisible(true);
        TF_Nombre.setManaged(true);
        TF_Servicio.setVisible(true);
        TF_Servicio.setManaged(true);
        TF_Telefono.setVisible(true);
        TF_Telefono.setManaged(true);

        resetearCamposButton.setVisible(true);
        resetearCamposButton.setManaged(true);
    }

    private void ocultarItems() {
        labelNombre.setVisible(false);
        labelNombre.setManaged(false);
        labelServicio.setVisible(false);
        labelServicio.setManaged(false);
        labelTelefono.setVisible(false);
        labelTelefono.setManaged(false);

        TF_Nombre.setVisible(false);
        TF_Nombre.setManaged(false);
        TF_Servicio.setVisible(false);
        TF_Servicio.setManaged(false);
        TF_Telefono.setVisible(false);
        TF_Telefono.setManaged(false);
    }

    /**
     * Pone los textFields vacios
     */
    public void resetearCampos() {
        TF_Nombre.setText("");
        TF_Servicio.setText("");
        TF_Telefono.setText("");
    }

    public void mostrarInfo() {
        tablaProveedores.setOnMouseClicked(event -> {
            if (accionButton.getText().equals("Guardar Cambios")) {
                ProveedorVO seleccionado = proveedores.get(
                        tablaProveedores.getSelectionModel().getSelectedIndex());
                if (seleccionado != null) {
                    //System.out.println("Seleccionado: " + seleccionado);

                    TF_Nombre.setText(seleccionado.getNombreProveedor());
                    TF_Servicio.setText(seleccionado.getServicioProveedor());
                    TF_Telefono.setText(seleccionado.getTelefonoProveedor());

                    Id = seleccionado.getIdProveedor();
                }
            }
        });
    }

    /**
     * Al hacer click en el boton "Agregar" abre el respectivo FXML
     *
     * @throws IOException
     */
    @FXML
    private void onAgregarViewButtonClicked() throws IOException {
        descripcionLabel.setText("INGRESA LOS DATOS DE UN PROVEEDOR NUEVO");
        accionButton.setText("Agregar Proveedor");
        mostrarItems();
    }

    /**
     * Al hacer click en el boton "Editar" abre el respectivo FXML
     *
     * @throws IOException
     */
    @FXML
    private void onEditarViewButtonClicked() throws IOException {
        descripcionLabel.setText("ACTUALIZA LOS DATOS DE UN PROVEEDOR");
        accionButton.setText("Guardar Cambios");
        mostrarItems();
    }

    /**
     * Al hacer click en el boton "Eliminar" abre el respectivo FXML
     *
     * @throws IOException
     */
    @FXML
    private void onEliminarViewButtonClicked() throws IOException {
        descripcionLabel.setText("SELECCIONA A UN PROVEEDOR EN LA TABLA Y PRESIONA EL BOTON DE \"ELIMINAR PROVEEDOR\"");
        accionButton.setText("Eliminar Proveedor");
        resetearCamposButton.setVisible(false);
        resetearCamposButton.setManaged(false);
        ocultarItems();
    }

    /**
     * Valida que el telefono tenga el formato de uno real: 10 digitos y que no
     * tenga ninguna letra. True si cumple, false si no
     *
     * @param telefono
     * @return
     */
    @FXML
    public boolean validarTelefono(String telefono) {
        return telefono.matches(REGEX_TELEFONO);
    }

    /**
     * Valida los campos
     *
     * @param Nombre
     * @param Servicio
     * @param Telefono
     * @return
     */
    @FXML
    public boolean validarCampos(String Nombre, String Servicio, String Telefono) {
        boolean nombreVacio = Nombre.isEmpty();
        boolean servicioVacio = Servicio.isEmpty();
        boolean telefonoVacio = Telefono.isEmpty();

        if (nombreVacio && servicioVacio && telefonoVacio) {
            alerta.mostrarError("Campos vacios", "Llena todos los campos");
            return false;
        }
        if (nombreVacio) {
            alerta.mostrarError("Campo Nombre vacio", "Llena el campo de nombre");
            return false;
        }
        if (servicioVacio) {
            alerta.mostrarError("Campo Servicio vacio", "Llena el campo de servicio");
            return false;
        }
        if (telefonoVacio) {
            alerta.mostrarError("Campo Telefono vacio", "Llena el campo de telefono");
            return false;
        }
        if (!validarTelefono(Telefono)) {
            alerta.mostrarError("El telefono no es valido", "Ingresa un numero de telefono valido '10 digitos'");
            return false;
        }
        return true;
    }

    /**
     * Configura las columnas de la tabla para que muestren los campos
     * correspondientes
     */
    private void configurarTabla() {
        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colSecundaria.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colCuaternaria.setCellValueFactory(new PropertyValueFactory<>("servicioProveedor"));
        colTerciaria.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));
    }

    /**
     * Carga todos los proveedores desde la bd en un arraylist para su lectura
     * en la tabla
     */
    private void cargarProveedores() {
        try {
            proveedores = proveedorDAO.obtenerTodosProveedores();
            ObservableList<Object> datos = FXCollections.observableArrayList(proveedores);
            tablaProveedores.setItems(datos);
        } catch (SQLException e) {
            alerta.mostrarExcepcion("Error", "No se cargaron los proveedores", e);
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }

    /**
     * Establece la conexion con la bd usando "DataConnection"
     *
     * @throws SQLException
     */
    public void setDB() throws SQLException {
        this.db = DatabaseConnection.getInstance();
    }

    @FXML
    public void mostrarHistorialCambios() {
        if (historialCambios.isEmpty()) {
            alerta.mostrarError("Historial vacío", "No hay cambios registrados aún.");
        } else {
            StringBuilder historialTexto = new StringBuilder();
            for (HistorialProveedorVO cambio : historialCambios) {
                historialTexto.append(cambio.toString()).append("\n--------------------------\n");
            }
            alerta.mostrarHistorial("Historial de Cambios", historialTexto.toString());
        }
    }
    
    private void cargarComboBoxProveedores() throws SQLException {
        List<String> nombresProveedores = proveedorDAO.obtenerNombresProveedores();
        nombresProveedores.add(0, "Todos los proveedores");
        ObservableList<String> items = FXCollections.observableArrayList(nombresProveedores);
        comboBoxProveedores.setItems(items);
        comboBoxProveedores.getSelectionModel().selectFirst();
        comboBoxProveedores.getSelectionModel().selectedItemProperty().addListener(
    (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
        if (newValue != null) {
            if (newValue.equals("Todos los proveedores")) {
                configurarColumnasParaProveedores(); // <-- Restaura columnas
                cargarProveedores();
            } else {
                cargarProductos(newValue);
            }
        }
    });
    }
    
    private void cargarProductos(String nombreProveedor) {
    try {
        configurarColumnasParaProductos();
        List<ProductoVO> productos = proveedorDAO.obtenerProductosPorProveedor(nombreProveedor);
        ObservableList<Object> items = FXCollections.observableArrayList(productos); // Usa ProductoVO
        tablaProveedores.setItems(items);
    } catch (SQLException e) {
        alerta.mostrarError("Error", "Error al cargar productos: " + e.getMessage());
    }
}
    
    private void configurarColumnasParaProductos() {
    tablaProveedores.getColumns().clear();

    // Crea nuevas columnas específicas para ProductoVO
    TableColumn<Object, String> colNombreProducto = new TableColumn<>("Producto");
    TableColumn<Object, Integer> colPrecioCompra = new TableColumn<>("Precio Compra");
    TableColumn<Object, Integer> colPrecioVenta = new TableColumn<>("Precio Venta");
    TableColumn<Object, Integer> colStock = new TableColumn<>("Stock");

    // Configura las propiedades usando lambda para manejar Object
    colNombreProducto.setCellValueFactory(cellData -> 
        new SimpleStringProperty(((ProductoVO) cellData.getValue()).getNombre()));
    colPrecioCompra.setCellValueFactory(cellData -> 
        new SimpleIntegerProperty(((ProductoVO) cellData.getValue()).getPrecioDeCompra()).asObject());
    colPrecioVenta.setCellValueFactory(cellData -> 
        new SimpleIntegerProperty(((ProductoVO) cellData.getValue()).getPrecioDeVenta()).asObject());
    colStock.setCellValueFactory(cellData -> 
        new SimpleIntegerProperty(((ProductoVO) cellData.getValue()).getStock()).asObject());

    tablaProveedores.getColumns().addAll(colNombreProducto, colPrecioCompra, colPrecioVenta, colStock);
}
    
    private void configurarColumnasParaProveedores() {
    tablaProveedores.getColumns().clear();
    
    colPrincipal.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
    colSecundaria.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
    colCuaternaria.setCellValueFactory(new PropertyValueFactory<>("servicioProveedor"));
    colTerciaria.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));

    tablaProveedores.getColumns().addAll(colPrincipal, colSecundaria, colTerciaria, colCuaternaria);
}

}
