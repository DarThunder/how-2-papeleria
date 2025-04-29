package com.idar.pdvpapeleria.controllers;

import DAO.ProductoDAO;
import VO.ProductoVO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;

public class AdminViewController {

    @FXML
    private TableView<ProductoVO> productosTableView;
    @FXML
    private TableColumn<ProductoVO, Integer> idCol;
    @FXML
    private TableColumn<ProductoVO, String> nombreCol;
    @FXML
    private TableColumn<ProductoVO, Integer> precioCompraCol;
    @FXML
    private TableColumn<ProductoVO, Integer> precioVentaCol;
    @FXML
    private TableColumn<ProductoVO, Integer> stockCol;
    @FXML
    private TableColumn<ProductoVO, String> categoriaCol;
    @FXML
    private TextField buscarField;
    @FXML
    private Button btnBuscar;

    private ProductoDAO productoDAO;
    private ObservableList<ProductoVO> listaProductosOriginal;
    private FilteredList<ProductoVO> datosFiltrados;

    @FXML
    public void initialize() throws SQLException {
        configurarColumnas();
        productoDAO = new ProductoDAO();
        cargarProductos();
        configurarBuscador();
    }

    private void configurarColumnas() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioCompraCol.setCellValueFactory(new PropertyValueFactory<>("precioDeCompra"));
        precioVentaCol.setCellValueFactory(new PropertyValueFactory<>("precioDeVenta"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        categoriaCol.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    private void cargarProductos() {
        try {
            ResultSet rs = productoDAO.getProductos();
            listaProductosOriginal = FXCollections.observableArrayList();

            while (rs.next()) {
                listaProductosOriginal.add(new ProductoVO(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getInt("precioDeCompra"),
                        rs.getInt("precioDeVenta"),
                        rs.getInt("stock"),
                        rs.getString("categoria")
                ));
            }

            productosTableView.setItems(listaProductosOriginal);

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBuscador() {
        datosFiltrados = new FilteredList<>(listaProductosOriginal, p -> true);

        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarPorNombreYCategoria(newValue);
        });

        btnBuscar.setOnAction(event -> filtrarPorNombreYCategoria(buscarField.getText()));

        SortedList<ProductoVO> datosOrdenados = new SortedList<>(datosFiltrados);
        datosOrdenados.comparatorProperty().bind(productosTableView.comparatorProperty());
        productosTableView.setItems(datosOrdenados);
    }

    private void filtrarPorNombreYCategoria(String filtro) {
        datosFiltrados.setPredicate(producto -> {
            if (filtro == null || filtro.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = filtro.toLowerCase();
            return producto.getNombre().toLowerCase().contains(lowerCaseFilter)
                    || (producto.getCategoria() != null && producto.getCategoria().toLowerCase().contains(lowerCaseFilter));
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
