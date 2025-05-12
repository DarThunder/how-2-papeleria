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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Obtener la instancia de DatabaseConnection
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            
            // Inicializar los DAOs
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
    // Definir las categorías
    ObservableList<String> categorias = FXCollections.observableArrayList(
        "Material de Escritura",
        "Papelería y Cuadernos",
        "Arte y Manualidades", 
        "Oficina y Organización",
        "Tecnología y Electrónica"
    );
    
    // Configurar un StringConverter personalizado
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
    
    // Establecer valor inicial nulo con prompt
    categoriaComboBox.setValue(null);
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
        // Configurar el StringConverter
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

        // Cargar los proveedores
        proveedorComboBox.getItems().addAll(proveedorDAO.obtenerTodosProveedores());
        
        // Establecer valor inicial nulo con prompt
        proveedorComboBox.setValue(null);
        proveedorComboBox.setPromptText("Seleccione un proveedor");
        
    } catch (SQLException e) {
        AlertaPDV.mostrarExcepcion("Error de carga", "No se pudieron cargar los proveedores", e);
        e.printStackTrace();
    }
}

    @FXML
    private void guardarProducto() {
        try {
            // Validaciones
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