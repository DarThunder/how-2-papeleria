/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.idar.pdvpapeleria.controllers;

import DAOImp.ProductoDAOImp;
import DAOImp.VentaDAOImp;
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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CajeroViewController implements Initializable {

    private final ProductoDAOImp productoDao = ProductoDAOImp.getInstance();
    private final VentaDAOImp ventaDao = VentaDAOImp.getInstance();
    private final ObservableList<ProductoVO> products = FXCollections.observableArrayList();
    private final Map<Integer, ProductoVO> productMap = new HashMap<>(); // Para búsquedas rápidas

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TableView<ProductoVO> productosTableView;
    @FXML
    private TableColumn<ProductoVO, String> productNameCol;
    @FXML
    private TableColumn<ProductoVO, Integer> productCountCol;
    @FXML
    private TableColumn<ProductoVO, Integer> productPriceCol;
    @FXML
    private TableColumn<ProductoVO, Integer> productSubtotalCol;
    @FXML
    private Label totalLabel;
    private boolean isSellInAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.isSellInAction = false;
        configureTableColumns();

        Platform.runLater(() -> {
            if (AnchorPane.getScene() != null) {
                AnchorPane.getScene().setOnKeyPressed(this::handleKeyEvents);
            }
        });
    }

    private void configureTableColumns() {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        productCountCol.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("precioDeVenta"));
        productSubtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void updateTotal() {
        int total = 0;
        for (ProductoVO p : products) {
            System.out.println(p.getCantidad());
            total += (p.getPrecioDeVenta() * p.getCantidad());
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

    private void addItem(String idProducto, String count) {
        if (idProducto.isEmpty() || count.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo vacío", "Por favor ingresa un ID de producto.");
            return;
        }

        try {
            int id = Integer.parseInt(idProducto);
            int cnt = Integer.parseInt(count);
            ProductoVO existingProduct = productMap.get(id);

            if (existingProduct != null) {
                existingProduct.setCantidad(existingProduct.getCantidad() + cnt);
                productosTableView.refresh();
            } else {
                ProductoVO newProduct = productoDao.getProductoById(id);
                if (newProduct != null) {
                    newProduct.setCantidad(cnt);
                    products.add(newProduct);
                    productMap.put(id, newProduct);
                    productosTableView.setItems(products);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "No encontrado", "No se encontró ningún producto con ese ID.");
                    return;
                }
            }
            updateTotal();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ID inválido", "El ID debe ser un número entero.");
        }
    }

    private void removeItem(String idProducto, String count) {
        if (idProducto.isEmpty() || count.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo vacío", "Por favor ingresa un ID de producto.");
            return;
        }

        try {
            int id = Integer.parseInt(idProducto);
            ProductoVO removedProduct = productMap.remove(id);

            if (removedProduct != null) {
                products.remove(removedProduct);
                updateTotal();
                showAlert(Alert.AlertType.INFORMATION, "Producto eliminado", "El producto ha sido eliminado de la lista.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No encontrado", "El producto no se encuentra en la lista.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ID inválido", "El ID debe ser un número entero.");
        }
    }

    private void onInitButtonClicked() {
        if (this.isSellInAction) {
            return;
        }

        this.isSellInAction = true;
        products.clear();
        configureTableColumns();
        showAlert(Alert.AlertType.INFORMATION, "Venta iniciada", "Se inicio una venta");
    }

    private void onCancelButtonClicked() {
        if (!this.isSellInAction) {
            return;
        }
        if (products.isEmpty()) {
            this.isSellInAction = false;
            showAlert(Alert.AlertType.INFORMATION, "Cancelación de pago realizado", "La cancelación ha sido procesado correctamente.");
            return;
        }

        try {
            this.isSellInAction = false;
            products.clear();
            productMap.clear();
            updateTotal();
            showAlert(Alert.AlertType.INFORMATION, "Cancelación de pago realizado", "La cancelación ha sido procesado correctamente.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error la cancelación de pago", "Ocurrió un error al cancelar el pago: " + e.getMessage());
        }
    }

    private void onSearchButtonClicked(boolean deleteMode) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Ingresar datos");

        Label idLabel = new Label("ID:");
        TextField idField = new TextField();

        Label cantidadLabel = new Label("Cantidad:");
        TextField cantidadField = new TextField();

        Button aceptarBtn = new Button("Aceptar");
        aceptarBtn.setOnAction(e -> {
            if (!this.isSellInAction) {
                return;
            }
            String id = idField.getText().trim();
            String cantidad = cantidadField.getText().trim();
            if (!deleteMode) {
                addItem(id, cantidad);
            } else {
                removeItem(id, cantidad);
            }
            popup.close();
        });

        VBox layout = new VBox(10, idLabel, idField, cantidadLabel, cantidadField, aceptarBtn);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #f0f0f0;");
        popup.setScene(new Scene(layout));
        popup.showAndWait();
    }

    private void onPayButtonClicked() {
        if (products.isEmpty() || !this.isSellInAction) {
            return;
        }

        int total = products.stream()
        .mapToInt(p -> p.getPrecioDeVenta() * p.getCantidad())
        .sum();

        int folio = ventaDao.generarVenta(1, total);
        if (folio == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo generar la venta.");
            return;
        }

        for (ProductoVO producto : products) {
            if (!productoDao.reducirStock(producto.getIdProducto(), producto.getCantidad())) {
                ventaDao.rollbackVenta(folio);
                showAlert(Alert.AlertType.ERROR, "Error en el pago", "Ocurrió un error al procesar el pago. Se hizo rollback.");
                return;
            }
        }

        products.clear();
        productMap.clear();
        updateTotal();
        showAlert(Alert.AlertType.INFORMATION, "Pago realizado", "El pago ha sido procesado correctamente.");
    }

    private void handleKeyEvents(KeyEvent event) {
        switch (event.getCode()) {
            case F4 ->
                onInitButtonClicked();
            case F5 ->
                onCancelButtonClicked();
            case F6 ->
                onSearchButtonClicked(false);
            case F7 ->
                onSearchButtonClicked(true);
            case F8 ->
                onPayButtonClicked();
        }
    }
}
