package com.idar.pdvpapeleria.controllers;

import DAOImp.ProductoDAOImp;
import VO.ProductoVO;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class CajeroViewController implements Initializable {

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TextField idProductField;
    @FXML
    private TableView<ProductoConCantidad> productosTableView;
    @FXML
    private TableColumn<ProductoConCantidad, String> productNameCol;
    @FXML
    private TableColumn<ProductoConCantidad, Integer> productCountCol;
    @FXML
    private TableColumn<ProductoConCantidad, Integer> productPriceCol;
    @FXML
    private TableColumn<ProductoConCantidad, Integer> productSubtotalCol;
    @FXML
    private Label totalLabel;
    @FXML
    private Button searchButton;
    @FXML
    private Button payButton;
    @FXML
    private Button deleteButton;

    private final ProductoDAOImp productoDao = ProductoDAOImp.getInstance();
    private final ObservableList<ProductoConCantidad> products = FXCollections.observableArrayList();
    private final Map<Integer, ProductoConCantidad> productMap = new HashMap<>();

    public static class ProductoConCantidad {

        private final ProductoVO producto;
        private int cantidad;

        public ProductoConCantidad(ProductoVO producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public String getNombre() {
            return producto.getNombre();
        }

        public int getPrecioDeVenta() {
            return producto.getPrecioDeVenta();
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public int getSubtotal() {
            return cantidad * producto.getPrecioDeVenta();
        }

        public int getIdProducto() {
            return producto.getIdProducto();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        setupEventHandlers();
    }

    private void configureTableColumns() {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        productCountCol.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("precioDeVenta"));
        productSubtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void setupEventHandlers() {
        searchButton.setOnAction(e -> onSearchButtonClicked());
        payButton.setOnAction(e -> onPayButtonClicked());
        deleteButton.setOnAction(e -> onDeleteButtonClicked());

        idProductField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                onSearchButtonClicked();
            }
        });
    }

    @FXML
    private void onSearchButtonClicked() {
        String idProducto = idProductField.getText().trim();

        if (idProducto.isEmpty()) {
            showAlert("Campo vacío", "Por favor ingresa un ID de producto.");
            return;
        }

        try {
            int id = Integer.parseInt(idProducto);
            ProductoConCantidad existingProduct = productMap.get(id);

            if (existingProduct != null) {
                existingProduct.setCantidad(existingProduct.getCantidad() + 1);
            } else {
                ProductoVO producto = productoDao.getProductoById(id);
                if (producto != null) {
                    ProductoConCantidad newProduct = new ProductoConCantidad(producto, 1);
                    products.add(newProduct);
                    productMap.put(id, newProduct);
                } else {
                    showAlert("No encontrado", "No se encontró ningún producto con ese ID.");
                    return;
                }
            }
            productosTableView.refresh();
            updateTotal();
            idProductField.clear();
        } catch (NumberFormatException e) {
            showAlert("ID inválido", "El ID debe ser un número entero.");
        } catch (SQLException e) {
            showAlert("Error", "Error al buscar producto: " + e.getMessage());
        }
    }

    private void updateTotal() {
        int total = products.stream().mapToInt(ProductoConCantidad::getSubtotal).sum();
        totalLabel.setText(String.format("Total: $%d", total));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onPayButtonClicked() {
        if (products.isEmpty()) {
            showAlert("Error", "No hay productos para vender");
            return;
        }

        try {
            for (ProductoConCantidad p : products) {
                productoDao.reducirStock(p.getIdProducto(), p.getCantidad());
            }

            products.clear();
            productMap.clear();
            updateTotal();
            showAlert("Éxito", "Venta realizada correctamente");
        } catch (SQLException e) {
            showAlert("Error", "No se pudo completar la venta: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteButtonClicked() {
        String idProducto = idProductField.getText().trim();

        if (idProducto.isEmpty()) {
            showAlert("Campo vacío", "Por favor ingresa un ID de producto.");
            return;
        }

        try {
            int id = Integer.parseInt(idProducto);
            ProductoConCantidad removed = productMap.remove(id);
            if (removed != null) {
                products.remove(removed);
                updateTotal();
            }
            idProductField.clear();
        } catch (NumberFormatException e) {
            showAlert("ID inválido", "El ID debe ser un número entero.");
        }
    }
}
