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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import Vista.AlertaPDV;

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
        categoriaComboBox.setPromptText("Seleccione una categoría");
    }

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

    private void configurarComboBoxProveedores() {
        try {
            proveedorComboBox.setConverter(new StringConverter<ProveedorVO>() {
                @Override
                public String toString(ProveedorVO proveedor) {
                    return proveedor == null ? "Sin selección" : proveedor.getNombreProveedor();
                }

                @Override
                public ProveedorVO fromString(String string) {
                    return null; // No necesario para selección simple
                }
            });

            proveedorComboBox.getItems().addAll(proveedorDAO.obtenerTodosProveedores());
            proveedorComboBox.setPromptText("Seleccione un proveedor");

        } catch (SQLException e) {
            AlertaPDV.mostrarExcepcion("Error de carga", "No se pudieron cargar los proveedores", e);
        }
    }

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

    @FXML
    private void guardarCambios() {
        try {
            // Validaciones
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

            // Confirmación
            if (!AlertaPDV.mostrarConfirmacion("Confirmar cambios", "¿Está seguro de guardar los cambios?")) {
                return;
            }

            // Crear objeto modificado
            ProductoVO productoModificado = new ProductoVO(
                    productoOriginal.getIdProducto(),
                    nombreField.getText(),
                    precioCompra,
                    precioVenta,
                    stock,
                    descripcionField.getText(),
                    categoriaComboBox.getValue()
            );

            ProveedorVO proveedorSeleccionado = proveedorComboBox.getValue();

            // Actualizar en BD
            boolean actualizado = productoDAO.actualizarProductoConProveedor(
                    productoModificado,
                    proveedorSeleccionado.getIdProveedor()
            );

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

    private boolean camposInvalidos() {
        return nombreField.getText().isEmpty()
                || precioCompraField.getText().isEmpty()
                || precioVentaField.getText().isEmpty()
                || stockField.getText().isEmpty()
                || categoriaComboBox.getValue() == null;
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
