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
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controlador para la administración de proveedores en el sistema PDV Papelería.
 * 
 * Esta clase maneja todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * relacionadas con los proveedores, incluyendo la visualización en tabla,
 * validación de datos, historial de cambios y búsqueda de proveedores.
 * 
 * @author goatt
 * @version 1.0
 * @since 2025
 */
public class AdminProveedoresViewController implements Initializable {

    /** Instancia para mostrar alertas y mensajes al usuario */
    AlertaPDV alerta = new AlertaPDV();
    
    /** Lista que contiene todos los proveedores cargados desde la base de datos */
    List<ProveedorVO> proveedores = new ArrayList<ProveedorVO>();
    
    /** Lista que mantiene el historial de cambios realizados en los proveedores */
    List<HistorialProveedorVO> historialCambios = new ArrayList<>();

    /** ID del proveedor seleccionado para operaciones de edición */
    public int Id;
    
    /** Conexión a la base de datos */
    private DatabaseConnection db;
    
    /** Objeto de acceso a datos para operaciones con proveedores */
    private ProveedorDAOImp proveedorDAO;
    
    /** Expresión regular para validar números de teléfono (10 dígitos) */
    public static final String REGEX_TELEFONO = "^[0-9]{10}$";

    // Componentes de la interfaz gráfica
    
    /** Tabla principal que muestra los proveedores o productos según el contexto */
    @FXML
    private TableView<Object> tablaProveedores;

    /** Columna principal de la tabla (ID o nombre de producto) */
    @FXML
    private TableColumn<Object, Integer> colPrincipal;

    /** Columna secundaria de la tabla (nombre del proveedor o precio de compra) */
    @FXML
    private TableColumn<Object, String> colSecundaria;

    /** Columna terciaria de la tabla (teléfono o precio de venta) */
    @FXML
    private TableColumn<Object, String> colTerciaria;

    /** Columna cuaternaria de la tabla (servicio o stock) */
    @FXML
    private TableColumn<Object, String> colCuaternaria;

    // Botones de la interfaz
    
    /** Botón para acceder a la vista de agregar proveedor */
    @FXML
    private Button agregarViewButton;

    /** Botón para acceder a la vista de editar proveedor */
    @FXML
    private Button editarViewButton;

    /** Botón para acceder a la vista de eliminar proveedor */
    @FXML
    private Button eliminarViewButton;

    /** Botón para regresar a la vista anterior */
    @FXML
    private Button regresarViewButton;

    /** Botón dinámico que cambia su función según el contexto (Agregar/Editar/Eliminar) */
    @FXML
    private Button accionButton;

    /** Botón para limpiar todos los campos de entrada */
    @FXML
    private Button resetearCamposButton;

    /** Botón para mostrar el historial de cambios */
    @FXML
    private Button historialButton;

    /** Botón para ejecutar búsquedas */
    @FXML
    private Button BBuscar;

    // Etiquetas y campos de texto
    
    /** Etiqueta que describe la acción actual en la interfaz */
    @FXML
    private Text descripcionLabel;

    /** Etiqueta del campo nombre */
    @FXML
    private Label labelNombre;

    /** Etiqueta del campo servicio */
    @FXML
    private Label labelServicio;

    /** Etiqueta del campo teléfono */
    @FXML
    private Label labelTelefono;

    /** Campo de texto para el nombre del proveedor */
    @FXML
    private TextField TF_Nombre;

    /** Campo de texto para el servicio que ofrece el proveedor */
    @FXML
    private TextField TF_Servicio;

    /** Campo de texto para el teléfono del proveedor */
    @FXML
    private TextField TF_Telefono;

    /** Campo de texto para realizar búsquedas */
    @FXML
    private TextField TFBusqueda;

    /** ComboBox para seleccionar proveedores y filtrar productos */
    @FXML
    private ComboBox<String> comboBoxProveedores;

    /**
     * Inicializa el controlador después de que se haya cargado el archivo FXML.
     * Configura la conexión a la base de datos, inicializa los componentes
     * y carga los datos iniciales.
     * 
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz
     * @param rb Los recursos utilizados para localizar el objeto raíz
     */
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
     * Agrega un nuevo proveedor al sistema.
     * 
     * Obtiene los datos de los campos de texto, los valida y si son correctos,
     * crea un nuevo proveedor en la base de datos. Actualiza la tabla y el
     * ComboBox después de la operación exitosa.
     * 
     * @throws SQLException si ocurre un error al acceder a la base de datos
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
                cargarComboBoxProveedores();
                alerta.mostrarExito("Exito", "Se agregó correctamente al proveedor");
                resetearCampos();
            } catch (SQLException e) {
                alerta.mostrarExcepcion("Error", "Error al agregar proveedor", e);
                System.err.println("Error al agregar proveedor: " + e.getMessage());
            }
        }
    }

    /**
     * Edita un proveedor existente en el sistema.
     * 
     * Obtiene los nuevos datos de los campos de texto, los valida y actualiza
     * el proveedor en la base de datos. Registra el cambio en el historial
     * para mantener un seguimiento de las modificaciones.
     * 
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
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
                cargarComboBoxProveedores();
                alerta.mostrarExito("Éxito", "Proveedor editado correctamente");
                resetearCampos();

            } catch (SQLException e) {
                alerta.mostrarExcepcion("Error", "Error al editar proveedor", e);
                System.err.println("Error al editar proveedor: " + e.getMessage());
            }
        }
    }

    /**
     * Elimina un proveedor seleccionado del sistema.
     * 
     * Obtiene el proveedor seleccionado de la tabla, solicita confirmación
     * al usuario y procede con la eliminación si es confirmada. Actualiza
     * la tabla y el ComboBox después de la operación.
     * 
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    @FXML
    private void eliminarProveedor() throws SQLException {
        ProveedorVO seleccionado = (ProveedorVO) tablaProveedores.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
            boolean resultado = alerta.mostrarConfirmacion("Eliminar", 
                "¿Seguro que desea eliminar a " + seleccionado.getNombreProveedor() + "?");
            if (resultado) {
                proveedorDAO.eliminarProveedor(seleccionado.getIdProveedor());
                cargarProveedores();
                cargarComboBoxProveedores();
                alerta.mostrarExito("Éxito", "Proveedor eliminado");
            }
        } else {
            alerta.mostrarError("Error", "Selecciona un proveedor de la tabla");
        }
    }

    /**
     * Maneja las acciones del botón dinámico según su texto actual.
     * 
     * Este método centraliza la lógica de las diferentes acciones que puede
     * realizar el botón principal, dependiendo del contexto actual de la interfaz.
     * 
     * @throws SQLException si ocurre un error durante las operaciones de base de datos
     */
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

    /**
     * Navega de regreso a la vista de opciones del administrador.
     * 
     * Carga el archivo FXML de opciones del administrador y cambia la escena actual.
     * 
     * @throws MalformedURLException si la URL del archivo FXML es malformada
     * @throws IOException si ocurre un error al cargar el archivo FXML
     */
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

    /**
     * Hace visibles y manejables los elementos de entrada de datos.
     * 
     * Muestra las etiquetas y campos de texto necesarios para las operaciones
     * de agregar y editar proveedores.
     */
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

    /**
     * Oculta los elementos de entrada de datos.
     * 
     * Hace invisibles las etiquetas y campos de texto cuando no son necesarios,
     * como en la vista de eliminación de proveedores.
     */
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
     * Limpia todos los campos de entrada de datos.
     * 
     * Establece el texto de todos los TextField en cadena vacía,
     * permitiendo al usuario empezar con campos limpios.
     */
    public void resetearCampos() {
        TF_Nombre.setText("");
        TF_Servicio.setText("");
        TF_Telefono.setText("");
    }

    /**
     * Configura el comportamiento de selección de la tabla.
     * 
     * Cuando se está en modo de edición y se selecciona un proveedor de la tabla,
     * llena automáticamente los campos de texto con los datos del proveedor seleccionado.
     */
    public void mostrarInfo() {
        tablaProveedores.setOnMouseClicked(event -> {
            if (accionButton.getText().equals("Guardar Cambios")) {
                ProveedorVO seleccionado = (ProveedorVO) tablaProveedores.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    TF_Nombre.setText(seleccionado.getNombreProveedor());
                    TF_Servicio.setText(seleccionado.getServicioProveedor());
                    TF_Telefono.setText(seleccionado.getTelefonoProveedor());
                    Id = seleccionado.getIdProveedor();
                }
            }
        });
    }

    /**
     * Maneja el evento de clic en el botón "Agregar".
     * 
     * Configura la interfaz para el modo de agregar proveedor,
     * actualiza las etiquetas y botones correspondientes.
     * 
     * @throws IOException si ocurre un error al configurar la vista
     */
    @FXML
    private void onAgregarViewButtonClicked() throws IOException {
        descripcionLabel.setText("INGRESA LOS DATOS DE UN PROVEEDOR NUEVO");
        accionButton.setText("Agregar Proveedor");
        mostrarItems();
        configurarColumnasParaProveedores();
        cargarProveedores();
    }

    /**
     * Maneja el evento de clic en el botón "Editar".
     * 
     * Configura la interfaz para el modo de editar proveedor,
     * actualiza las etiquetas y botones correspondientes.
     * 
     * @throws IOException si ocurre un error al configurar la vista
     */
    @FXML
    private void onEditarViewButtonClicked() throws IOException {
        descripcionLabel.setText("ACTUALIZA LOS DATOS DE UN PROVEEDOR");
        accionButton.setText("Guardar Cambios");
        mostrarItems();
        configurarColumnasParaProveedores();
        cargarProveedores();
    }

    /**
     * Maneja el evento de clic en el botón "Eliminar".
     * 
     * Configura la interfaz para el modo de eliminar proveedor,
     * oculta los campos de entrada y actualiza las etiquetas correspondientes.
     * 
     * @throws IOException si ocurre un error al configurar la vista
     */
    @FXML
    private void onEliminarViewButtonClicked() throws IOException {
        descripcionLabel.setText("SELECCIONA A UN PROVEEDOR EN LA TABLA Y PRESIONA EL BOTON DE \"ELIMINAR PROVEEDOR\"");
        accionButton.setText("Eliminar Proveedor");
        resetearCamposButton.setVisible(false);
        resetearCamposButton.setManaged(false);
        ocultarItems();
        configurarColumnasParaProveedores();
        cargarProveedores();
    }

    /**
     * Valida que un número de teléfono tenga el formato correcto.
     * 
     * Utiliza una expresión regular para verificar que el teléfono
     * contenga exactamente 10 dígitos numéricos.
     * 
     * @param telefono el número de teléfono a validar
     * @return true si el teléfono es válido, false en caso contrario
     */
    @FXML
    public boolean validarTelefono(String telefono) {
        return telefono.matches(REGEX_TELEFONO);
    }

    /**
     * Valida todos los campos de entrada de datos.
     * 
     * Verifica que todos los campos requeridos estén llenos y que
     * el teléfono tenga un formato válido. Muestra mensajes de error
     * específicos para cada tipo de validación fallida.
     * 
     * @param Nombre el nombre del proveedor
     * @param Servicio el servicio que ofrece el proveedor
     * @param Telefono el teléfono del proveedor
     * @return true si todos los campos son válidos, false en caso contrario
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
     * Configura las columnas de la tabla para mostrar datos de proveedores.
     * 
     * Establece las propiedades de cada columna para que muestren
     * los campos correspondientes del objeto ProveedorVO.
     */
    private void configurarTabla() {
        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colSecundaria.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colTerciaria.setCellValueFactory(new PropertyValueFactory<>("servicioProveedor"));
        colCuaternaria.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));
    }

    /**
     * Carga todos los proveedores desde la base de datos.
     * 
     * Obtiene la lista completa de proveedores y la muestra en la tabla.
     * Maneja las excepciones SQL que puedan ocurrir durante la carga.
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
     * Establece la conexión con la base de datos.
     * 
     * Inicializa la instancia de DatabaseConnection utilizando el patrón Singleton.
     * 
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    public void setDB() throws SQLException {
        this.db = DatabaseConnection.getInstance();
    }

    /**
     * Muestra el historial de cambios realizados en los proveedores.
     * 
     * Si existen cambios registrados, los muestra en una ventana de diálogo.
     * Si no hay cambios, muestra un mensaje informativo.
     */
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

    /**
     * Carga y configura el ComboBox de proveedores.
     * 
     * Obtiene los nombres de todos los proveedores y los agrega al ComboBox,
     * incluyendo una opción para mostrar todos los proveedores. Configura
     * el listener para cambios de selección.
     * 
     * @throws SQLException si ocurre un error al obtener los nombres de proveedores
     */
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
                        configurarColumnasParaProveedores();
                        cargarProveedores();
                    } else {
                        cargarProductos(newValue);
                    }
                }
            });
    }

    /**
     * Carga los productos de un proveedor específico.
     * 
     * Configura la tabla para mostrar productos y carga los productos
     * asociados al proveedor seleccionado.
     * 
     * @param nombreProveedor el nombre del proveedor cuyos productos se van a cargar
     */
    private void cargarProductos(String nombreProveedor) {
        try {
            configurarColumnasParaProductos();
            List<ProductoVO> productos = proveedorDAO.obtenerProductosPorProveedor(nombreProveedor);
            ObservableList<Object> items = FXCollections.observableArrayList(productos);
            tablaProveedores.setItems(items);
        } catch (SQLException e) {
            alerta.mostrarError("Error", "Error al cargar productos: " + e.getMessage());
        }
    }

    /**
     * Configura las columnas de la tabla para mostrar datos de productos.
     * 
     * Limpia las columnas existentes y crea nuevas columnas específicas
     * para mostrar información de productos (nombre, precios, stock).
     */
    private void configurarColumnasParaProductos() {
        tablaProveedores.getColumns().clear();

        // Crea nuevas columnas específicas para ProductoVO
        TableColumn<Object, String> colNombreProducto = new TableColumn<>("Producto");
        TableColumn<Object, Integer> colPrecioCompra = new TableColumn<>("Precio Compra");
        TableColumn<Object, Integer> colPrecioVenta = new TableColumn<>("Precio Venta");
        TableColumn<Object, Integer> colStock = new TableColumn<>("Stock");

        // Configura las propiedades usando lambda para manejar Object
        colNombreProducto.setCellValueFactory(cellData
            -> new SimpleStringProperty(((ProductoVO) cellData.getValue()).getNombre()));
        colPrecioCompra.setCellValueFactory(cellData
            -> new SimpleIntegerProperty(((ProductoVO) cellData.getValue()).getPrecioDeCompra()).asObject());
        colPrecioVenta.setCellValueFactory(cellData
            -> new SimpleIntegerProperty(((ProductoVO) cellData.getValue()).getPrecioDeVenta()).asObject());
        colStock.setCellValueFactory(cellData
            -> new SimpleIntegerProperty(((ProductoVO) cellData.getValue()).getStock()).asObject());

        tablaProveedores.getColumns().addAll(colNombreProducto, colPrecioCompra, colPrecioVenta, colStock);
    }

    /**
     * Configura las columnas de la tabla para mostrar datos de proveedores.
     * 
     * Limpia las columnas existentes y restaura la configuración original
     * para mostrar información de proveedores.
     */
    private void configurarColumnasParaProveedores() {
        tablaProveedores.getColumns().clear();

        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colSecundaria.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colCuaternaria.setCellValueFactory(new PropertyValueFactory<>("servicioProveedor"));
        colTerciaria.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));

        tablaProveedores.getColumns().addAll(colPrincipal, colSecundaria, colTerciaria, colCuaternaria);
    }

    /**
     * Busca proveedores por nombre utilizando el campo de búsqueda.
     * 
     * Filtra la lista de proveedores basándose en el texto ingresado,
     * realizando una búsqueda parcial que no distingue entre mayúsculas y minúsculas.
     * Si no se encuentra ningún proveedor, muestra un mensaje de error.
     */
    @FXML
    private void buscarProveedorPorNombre() {
        String textoBusqueda = TFBusqueda.getText().trim().toLowerCase();

        // Asegurar que la tabla muestre proveedores
        configurarColumnasParaProveedores();

        if (textoBusqueda.isEmpty()) {
            cargarProveedores();
            return;
        }

        List<ProveedorVO> proveedoresFiltrados = proveedores.stream()
            .filter(p -> p.getNombreProveedor().toLowerCase().contains(textoBusqueda))
            .collect(Collectors.toList());

        if (proveedoresFiltrados.isEmpty()) {
            alerta.mostrarError("Búsqueda sin resultados", "No se encontró ningún proveedor con el nombre: " + textoBusqueda);
            return;
        }

        ObservableList<Object> datos = FXCollections.observableArrayList(proveedoresFiltrados);
        tablaProveedores.setItems(datos);
    }
}