package com.idar.pdvpapeleria.controllers;

import DAO.ProductoDAO;
import DAOImp.ProductoDAOImp;
import VO.ProductoVO;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class ModificarProductoController implements Initializable {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField precioCompraField;
    @FXML
    private TextField precioVentaField;
    @FXML
    private TextField stockField;
    @FXML 
    private TextField descripcionField;
    @FXML
    private ComboBox<String> categoriaComboBox;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private ProductoVO productoOriginal;
    private ProductoDAO productoDAO;
    
    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("\\d*") && !newText.equals("0")) {
            return change;
        }
        return null;
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            productoDAO = ProductoDAOImp.getInstance();
            
            // Configurar formateadores para campos numéricos
            precioCompraField.setTextFormatter(new TextFormatter<>(numberFilter));
            precioVentaField.setTextFormatter(new TextFormatter<>(numberFilter));
            stockField.setTextFormatter(new TextFormatter<>(numberFilter));
            
            // Inicializar ComboBox de categorías
            if (categoriaComboBox != null) {
                categoriaComboBox.getItems().addAll(
                    "Material de Escritura",
                    "Papelería y Cuadernos",
                    "Arte y Manualidades",
                    "Oficina y Organización",
                    "Tecnología y Electrónica"
                );
            } else {
                System.err.println("Error: ComboBox de categoría no encontrado. Verifique el archivo FXML.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al inicializar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initData(ProductoVO producto) {
        this.productoOriginal = producto;

        nombreField.setText(producto.getNombre());
        precioCompraField.setText(String.valueOf(producto.getPrecioDeCompra()));
        precioVentaField.setText(String.valueOf(producto.getPrecioDeVenta()));
        stockField.setText(String.valueOf(producto.getStock()));
        descripcionField.setText(producto.getDescripcion());
        
        if (categoriaComboBox != null) {
            categoriaComboBox.setValue(producto.getCategoria());
        }
    }

    @FXML
    private void guardarCambios() {
        try {
            // Validación de campos
            if (nombreField.getText().isEmpty() || 
                precioCompraField.getText().isEmpty() ||
                precioVentaField.getText().isEmpty() || 
                stockField.getText().isEmpty() ||
                categoriaComboBox.getValue() == null) {
                
                mostrarAlerta("Error", "Todos los campos son obligatorios");
                return;
            }

            int precioCompra = Integer.parseInt(precioCompraField.getText());
            int precioVenta = Integer.parseInt(precioVentaField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (precioCompra <= 0 || precioVenta <= 0 || stock < 0) {
                mostrarAlerta("Error", "Los valores numéricos deben ser positivos");
                return;
            }

            if (precioVenta <= precioCompra) {
                mostrarAlerta("Error", "El precio de venta debe ser mayor al de compra");
                return;
            }

            // Confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar cambios");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro de guardar los cambios?");

            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            // Crear objeto modificado
            ProductoVO productoModificado = new ProductoVO(
                    productoOriginal.getIdProducto(),
                    nombreField.getText(),
                    precioCompra,
                    precioVenta,
                    stock,
                    categoriaComboBox.getValue()
            );

            // Actualizar en BD
            boolean actualizado = productoDAO.actualizarProducto(productoModificado);

            if (actualizado) {
                mostrarAlerta("Éxito", "Producto actualizado correctamente");
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el producto");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Valores numéricos inválidos");
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error de base de datos: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}