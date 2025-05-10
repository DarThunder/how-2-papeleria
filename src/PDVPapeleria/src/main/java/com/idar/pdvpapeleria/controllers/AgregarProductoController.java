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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class AgregarProductoController implements Initializable {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField precioCompraField;
    @FXML
    private TextField precioVentaField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField categoriaField;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private ProductoDAO productoDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productoDAO = ProductoDAOImp.getInstance();
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

    @FXML
    private void guardarProducto() {
        try {
            // Validaciones
            if (camposInvalidos()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios y deben ser válidos");
                return;
            }

            ProductoVO nuevoProducto = new ProductoVO(
                    0, // ID temporal
                    nombreField.getText(),
                    Integer.parseInt(precioCompraField.getText()),
                    Integer.parseInt(precioVentaField.getText()),
                    Integer.parseInt(stockField.getText()),
                    categoriaField.getText()
            );

            if (productoDAO.agregarProducto(nuevoProducto)) {
                mostrarAlerta("Éxito", "Producto agregado correctamente");
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo agregar el producto");
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error de base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean camposInvalidos() {
        return nombreField.getText().isEmpty()
                || precioCompraField.getText().isEmpty()
                || precioVentaField.getText().isEmpty()
                || stockField.getText().isEmpty()
                || categoriaField.getText().isEmpty();
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
