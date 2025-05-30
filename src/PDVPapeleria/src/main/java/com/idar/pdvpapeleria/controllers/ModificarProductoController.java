package com.idar.pdvpapeleria.controllers;

import DAO.DatabaseConnection;
import DAO.ProductoDAO;
import DAO.ProveedorDAO;
import DAOImp.ProductoDAOImp;
import DAOImp.ProveedorDAOImp;
import VO.HistorialProductoVO;
import VO.ProductoVO;
import VO.ProveedorVO;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import Vista.AlertaPDV;

/**
 * Controlador para la ventana de modificación de productos en el sistema PDV.
 * Permite editar información de un producto, validarla, actualizar la base de
 * datos y registrar los cambios en un historial.
 */
public class ModificarProductoController implements Initializable {

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
    private ComboBox<String> categoriaComboBox;
    @FXML
    private ComboBox<ProveedorVO> proveedorComboBox;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private ProductoVO productoOriginal;
    private ProductoDAO productoDAO;
    private ProveedorDAO proveedorDAO;

    private static List<HistorialProductoVO> historialProductos = new ArrayList<>();

    /**
     * Inicializa el controlador, configurando la conexión a la base de datos,
     * los ComboBox y las validaciones para los campos numéricos.
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
     * Configura el ComboBox con categorías de productos.
     */
    private void configurarComboBoxCategorias() {
        ObservableList<String> categorias = FXCollections.observableArrayList(
                "Material de Escritura",
                "Papelería y Cuadernos",
                "Arte y Manualidades",
                "Oficina y Organización",
                "Tecnología y Electrónica");

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
        categoriaComboBox.setPromptText("Seleccione una categoría");
    }

    /**
     * Aplica validaciones para que los campos de precio y stock acepten solo
     * números enteros.
     */
    private void configurarValidacionesNumericas() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        precioCompraField.setTextFormatter(new TextFormatter<>(filter));
        precioVentaField.setTextFormatter(new TextFormatter<>(filter));
        stockField.setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * Carga la lista de proveedores desde la base de datos y los muestra en el
     * ComboBox.
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
                    return null;
                }
            });

            proveedorComboBox.getItems().addAll(proveedorDAO.obtenerTodosProveedores());
            proveedorComboBox.setPromptText("Seleccione un proveedor");

        } catch (SQLException e) {
            AlertaPDV.mostrarExcepcion("Error de carga", "No se pudieron cargar los proveedores", e);
        }
    }

    /**
     * Inicializa el formulario con los datos del producto seleccionado para
     * modificación.
     *
     * @param producto ProductoVO que se desea modificar.
     */
    public void initData(ProductoVO producto) {
        this.productoOriginal = producto;

        nombreField.setText(producto.getNombre());
        precioCompraField.setText(String.valueOf(producto.getPrecioDeCompra()));
        precioVentaField.setText(String.valueOf(producto.getPrecioDeVenta()));
        stockField.setText(String.valueOf(producto.getStock()));
        descripcionField.setText(producto.getDescripcion());
        categoriaComboBox.setValue(producto.getCategoria());

        try {
            ProveedorVO proveedorActual = proveedorDAO.obtenerProveedorDeProducto(producto.getIdProducto());
            if (proveedorActual != null) {
                proveedorComboBox.setValue(proveedorActual);
            }
        } catch (SQLException e) {
            AlertaPDV.mostrarExcepcion("Error", "No se pudo cargar el proveedor actual", e);
        }
    }

    /**
     * Maneja el evento del botón "Guardar". Valida los campos, actualiza el
     * producto en la base de datos y registra el historial de cambios.
     */
    @FXML
    private void guardarCambios() {
        try {
            if (camposInvalidos()) {
                AlertaPDV.mostrarError("Campos incompletos", "Complete todos los campos obligatorios");
                return;
            }

            int precioCompra = Integer.parseInt(precioCompraField.getText());
            int precioVenta = Integer.parseInt(precioVentaField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (precioCompra <= 0 || precioVenta <= 0 || stock < 0) {
                AlertaPDV.mostrarError("Error", "Los valores deben ser positivos");
                return;
            }

            if (precioVenta <= precioCompra) {
                AlertaPDV.mostrarError("Error", "El precio de venta debe ser mayor al de compra");
                return;
            }

            if (proveedorComboBox.getValue() == null) {
                AlertaPDV.mostrarError("Error", "Seleccione un proveedor");
                return;
            }

            if (!AlertaPDV.mostrarConfirmacion("Confirmar cambios", "¿Está seguro de guardar los cambios?")) {
                return;
            }

            ProductoVO productoModificado = new ProductoVO(
                    productoOriginal.getIdProducto(),
                    nombreField.getText(),
                    precioCompra,
                    precioVenta,
                    stock,
                    descripcionField.getText(),
                    categoriaComboBox.getValue());

            String fechaHoraActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            HistorialProductoVO historial = new HistorialProductoVO(
                    productoOriginal.getIdProducto(),
                    productoOriginal.getNombre(),
                    productoOriginal.getPrecioDeCompra(),
                    productoOriginal.getPrecioDeVenta(),
                    productoOriginal.getStock(),
                    productoOriginal.getDescripcion(),
                    productoOriginal.getCategoria(),
                    productoModificado.getNombre(),
                    productoModificado.getPrecioDeCompra(),
                    productoModificado.getPrecioDeVenta(),
                    productoModificado.getStock(),
                    productoModificado.getDescripcion(),
                    productoModificado.getCategoria(),
                    fechaHoraActual);

            historialProductos.add(historial);

            ProveedorVO proveedorSeleccionado = proveedorComboBox.getValue();

            boolean actualizado = productoDAO.actualizarProductoConProveedor(
                    productoModificado,
                    proveedorSeleccionado.getIdProveedor());

            if (actualizado) {
                AlertaPDV.mostrarExito("Éxito", "Producto actualizado correctamente");
                cerrarVentana();
            } else {
                AlertaPDV.mostrarError("Error", "No se pudo actualizar el producto");
            }

        } catch (NumberFormatException e) {
            AlertaPDV.mostrarError("Error", "Valores numéricos inválidos");
        } catch (SQLException e) {
            AlertaPDV.mostrarExcepcion("Error de base de datos", "Error al actualizar el producto", e);
        }
    }

    /**
     * Devuelve la lista de cambios registrados en el historial de productos.
     *
     * @return Lista de objetos HistorialProductoVO.
     */
    public static List<HistorialProductoVO> getHistorial() {
        return historialProductos;
    }

    /**
     * Verifica si hay campos vacíos o inválidos en el formulario.
     *
     * @return true si hay campos inválidos, false si todo es válido.
     */
    private boolean camposInvalidos() {
        return nombreField.getText().isEmpty()
                || precioCompraField.getText().isEmpty()
                || precioVentaField.getText().isEmpty()
                || stockField.getText().isEmpty()
                || categoriaComboBox.getValue() == null;
    }

    /**
     * Maneja el evento del botón "Cancelar", cerrando la ventana actual.
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual del formulario.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
