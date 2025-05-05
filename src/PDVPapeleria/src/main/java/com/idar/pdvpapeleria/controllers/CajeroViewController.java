




/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.idar.pdvpapeleria.controllers;

import DAOImp.ProductoDAOImp;
import VO.ProductoVO;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class CajeroViewController implements Initializable {

    private final ProductoDAOImp productoDao = ProductoDAOImp.getInstance();
    private final ObservableList<ProductoVO> products = FXCollections.observableArrayList();
    private final Map<Integer, ProductoVO> productMap = new HashMap<>(); // Para búsquedas rápidas

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TextField idProductField;
    @FXML
    private TableView<ProductoVO> productosTableView;
    @FXML
    private TableColumn<ProductoVO, String> productNameCol;
    @FXML
    private TableColumn<ProductoVO, Integer> productCountCol;
    @FXML
    private TableColumn<ProductoVO, Float> productPriceCol;
    @FXML
    private TableColumn<ProductoVO, Float> productSubtotalCol;
    @FXML
    private Label totalLabel;
    @FXML
    private Button searchButton;
    @FXML
    private Button payButton;
    @FXML
    private Button deleteButton;
    private boolean isSellInAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.isSellInAction = true;
        configureTableColumns();

        Platform.runLater(() -> {
            if (AnchorPane.getScene() != null) {
                setupEventHandlers();
            }
        });
    }

    private void configureTableColumns() {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        productCountCol.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("precioDeVenta"));
        productSubtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Formateador para columnas monetarias
        productPriceCol.setCellFactory(col -> new TableCell<ProductoVO, Float>() {
            @Override
            protected void updateItem(Float price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? "" : String.format("$%.2f", price));
            }
        });

        productSubtotalCol.setCellFactory(col -> new TableCell<ProductoVO, Float>() {
            @Override
            protected void updateItem(Float subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                setText(empty || subtotal == null ? "" : String.format("$%.2f", subtotal));
            }
        });
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

        AnchorPane.getScene().setOnKeyPressed(this::handleKeyEvents);

        // Listener para actualizar el total cuando cambia la lista
        products.addListener((javafx.collections.ListChangeListener.Change<? extends ProductoVO> c) -> {
            updateTotal();
        });
    }

    private void updateTotal() {
        int total = 0;
        for (ProductoVO p : products) {
            total += p.getPrecioDeCompra();
        }
        totalLabel.setText(String.format("Total: $%d", total));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void onSearchButtonClicked() {
        if(!this.isSellInAction) return;
        String idProducto = idProductField.getText().trim();

        if (idProducto.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo vacío", "Por favor ingresa un ID de producto.");
            return;
        }

        try {
            int id = Integer.parseInt(idProducto);
            ProductoVO existingProduct = productMap.get(id);

            if (existingProduct != null) {
                existingProduct.setCantidad(existingProduct.getCantidad() + 1);
                productosTableView.refresh();
            } else {
                ProductoVO newProduct = productoDao.getProducto(id);
                if (newProduct != null) {
                    newProduct.setCantidad(1);
                    products.add(newProduct);
                    productMap.put(id, newProduct);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "No encontrado", "No se encontró ningún producto con ese ID.");
                    return;
                }
            }
            idProductField.clear();
            idProductField.requestFocus();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ID inválido", "El ID debe ser un número entero.");
        }
    }
    
    private void onInitButtonClicked(){
        if(this.isSellInAction)return;
        
        products.clear();
        configureTableColumns();
        showAlert(Alert.AlertType.INFORMATION, "Venta iniciada", "Se inicio una venta");
    }

    private void onCancelButtonClicked() {
        if (products.isEmpty() || !this.isSellInAction) {
            return;
        }

        try {
            products.clear();
            productMap.clear();
            showAlert(Alert.AlertType.INFORMATION, "Cancelación de pago realizado", "La cancelación ha sido procesado correctamente.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error la cancelación de pago", "Ocurrió un error al cancelar el pago: " + e.getMessage());
        }
    }

    private void onPayButtonClicked() {
        if (products.isEmpty()) {
            return;
        }

        try {
            products.forEach(producto -> {
                productoDao.sellProducto(producto.getIdProducto(), producto.getCantidad());
            });

            products.clear();
            productMap.clear();
            showAlert(Alert.AlertType.INFORMATION, "Pago realizado", "El pago ha sido procesado correctamente.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error en el pago", "Ocurrió un error al procesar el pago: " + e.getMessage());
        }
    }

    private void onDeleteButtonClicked() {
        String idProducto = idProductField.getText().trim();

        if (idProducto.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo vacío", "Por favor ingresa un ID de producto.");
            return;
        }

        try {
            int id = Integer.parseInt(idProducto);
            ProductoVO removedProduct = productMap.remove(id);

            if (removedProduct != null) {
                products.remove(removedProduct);
                showAlert(Alert.AlertType.INFORMATION, "Producto eliminado", "El producto ha sido eliminado de la lista.");
                idProductField.clear();
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No encontrado", "El producto no se encuentra en la lista.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ID inválido", "El ID debe ser un número entero.");
        }
    }

    private void handleKeyEvents(KeyEvent event) {
        switch (event.getCode()) {
            case F5 ->
                onSearchButtonClicked();
            case F6 ->
                onDeleteButtonClicked();
            case F7 ->
                onPayButtonClicked();
            case F8 ->
                onInitButtonClicked();
            case F9 ->
                onCancelButtonClicked();
            case ESCAPE ->
                idProductField.clear();
        }
    }
}
