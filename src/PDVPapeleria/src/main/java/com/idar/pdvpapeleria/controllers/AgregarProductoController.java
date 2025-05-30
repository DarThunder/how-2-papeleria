package com.idar.pdvpapeleria.controllers;

import DAO.DatabaseConnection;
import DAO.ProductoDAO;
import DAO.ProveedorDAO;
import DAOImp.ProductoDAOImp;
import DAOImp.ProveedorDAOImp;
import VO.ProductoVO;
import VO.ProveedorVO;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import Vista.AlertaPDV;

/**
 * Controlador para la vista de agregar un nuevo producto en el sistema de punto
 * de venta. Se encarga de inicializar los componentes, configurar validaciones,
 * cargar proveedores y categorías, y gestionar la lógica de guardado y
 * cancelación.
 *
 * Utiliza DAOs para comunicarse con la base de datos y muestra alertas en caso
 * de errores.
 *
 * @author Alvaro
 */
public class AgregarProductoController implements Initializable {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField descripcionField;
    @FXML
    private TextField precioCompraField;
    @FXML
    private TextField precioVentaField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField categoriaField;
    @FXML
    private ComboBox<ProveedorVO> proveedorComboBox;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private ComboBox<String> categoriaComboBox;

    private ProductoDAO productoDAO;
    private ProveedorDAO proveedorDAO;

    /**
     * Inicializa el controlador. Se ejecuta automáticamente al cargar la vista.
     * Configura las validaciones, ComboBox de proveedores y categorías.
     *
     * @param url no utilizado
     * @param rb no utilizado
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            productoDAO = ProductoDAOImp.getInstance();
            proveedorDAO = new ProveedorDAOImp(dbConnection);

            configurarValidacionesNumericas();
            configurarComboBoxProveedores();
            configurarComboBoxCategorias();
        } catch (SQLException e) {
            AlertaPDV.mostrarError("Error de conexión", "No se pudo establecer conexión con la base de datos");
            e.printStackTrace();
        }
    }

    /**
     * Configura la ComboBox de categorías disponibles.
     */
    private void configurarComboBoxCategorias() {
        ObservableList<String> categorias = FXCollections.observableArrayList(
                "Material de Escritura",
                "Papelería y Cuadernos",
                "Arte y Manualidades",
                "Oficina y Organización",
                "Tecnología y Electrónica"
        );

        categoriaComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String categoria) {
                return categoria == null ? "Sin selección" : categoria;
            }

            @Override
            public String fromString(String string) {
                return string.equals("Sin selección") ? null : string;
            }
        });

        categoriaComboBox.setItems(categorias);
        categoriaComboBox.setValue(null);
        categoriaComboBox.setPromptText("Seleccione una categoría");
    }

    /**
     * Configura filtros para que solo se acepten valores numéricos en los
     * campos de precio de compra, precio de venta y stock.
     */
    private void configurarValidacionesNumericas() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };

        precioCompraField.setTextFormatter(new TextFormatter<>(filter));
        precioVentaField.setTextFormatter(new TextFormatter<>(filter));
        stockField.setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * Configura la ComboBox de proveedores con los datos obtenidos desde la
     * base de datos.
     */
    private void configurarComboBoxProveedores() {
        try {
            proveedorComboBox.setConverter(new StringConverter<ProveedorVO>() {
                @Override
                public String toString(ProveedorVO proveedor) {
                    return proveedor == null ? "Sin selección" : proveedor.getNombreProveedor();
                }

                @Override
                public ProveedorVO fromString(String string) {
                    return null; // No necesario
                }
            });

            proveedorComboBox.getItems().addAll(proveedorDAO.obtenerTodosProveedores());
            proveedorComboBox.setValue(null);
            proveedorComboBox.setPromptText("Seleccione un proveedor");

        } catch (SQLException e) {
            AlertaPDV.mostrarExcepcion("Error de carga", "No se pudieron cargar los proveedores", e);
            e.printStackTrace();
        }
    }

    /**
     * Evento asociado al botón "Guardar". Valida los campos y, si son
     * correctos, guarda el producto en la base de datos.
     */
    @FXML
    private void guardarProducto() {
        try {
            if (camposInvalidos()) {
                AlertaPDV.mostrarError("Campos incompletos", "Todos los campos son obligatorios, excepto descripción");
                return;
            }

            if (proveedorComboBox.getValue() == null) {
                AlertaPDV.mostrarError("Selección requerida", "Debe seleccionar un proveedor");
                return;
            }

            if (categoriaComboBox.getValue() == null) {
                AlertaPDV.mostrarError("Selección requerida", "Debe seleccionar una categoría");
                return;
            }

            ProveedorVO proveedorSeleccionado = proveedorComboBox.getValue();

            ProductoVO nuevoProducto = new ProductoVO(
                    0, // ID temporal
                    nombreField.getText(),
                    Integer.parseInt(precioCompraField.getText()),
                    Integer.parseInt(precioVentaField.getText()),
                    Integer.parseInt(stockField.getText()),
                    descripcionField.getText(),
                    categoriaComboBox.getValue()
            );

            if (productoDAO.agregarProductoConProveedor(nuevoProducto, proveedorSeleccionado.getIdProveedor())) {
                AlertaPDV.mostrarExito("Operación exitosa", "Producto agregado correctamente");
                cerrarVentana();
            } else {
                AlertaPDV.mostrarError("Error de operación", "No se pudo agregar el producto");
            }
        } catch (SQLException e) {
            AlertaPDV.mostrarExcepcion("Error de base de datos", "Ocurrió un error al guardar el producto", e);
        } catch (NumberFormatException e) {
            AlertaPDV.mostrarError("Formato inválido", "Los campos numéricos deben contener valores válidos");
        }
    }

    /**
     * Verifica si alguno de los campos obligatorios está vacío o sin selección.
     *
     * @return true si algún campo es inválido; false en caso contrario.
     */
    private boolean camposInvalidos() {
        return nombreField.getText().isEmpty()
                || precioCompraField.getText().isEmpty()
                || precioVentaField.getText().isEmpty()
                || stockField.getText().isEmpty()
                || categoriaComboBox.getValue() == null;
    }

    /**
     * Evento asociado al botón "Cancelar". Cierra la ventana actual sin guardar
     * cambios.
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
