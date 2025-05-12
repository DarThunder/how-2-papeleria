package com.idar.pdvpapeleria.controllers;

import DAO.ProductoDAO;
import DAOImp.ProductoDAOImp;
import VO.ProductoVO;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.control.TableRow;
import javafx.geometry.Insets;
import javafx.scene.Parent;

/**
 *
 * @author Alvaro
 */
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
    @FXML
    private Button btnAtras;
    
    private ProductoDAO productoDAO;
    private ObservableList<ProductoVO> listaProductosOriginal;
    private FilteredList<ProductoVO> datosFiltrados;

    @FXML
    public void initialize() throws SQLException {
        configurarColumnas();
        productoDAO = ProductoDAOImp.getInstance();
        cargarProductos();
        configurarBuscador();
        configurarSeleccionFila();
        configurarMenuContextual();
        
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
                        rs.getString("descripcion"),
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
        // Si no hay filtro o está vacío, mostrar todos los productos
        if (filtro == null || filtro.isEmpty()) {
            return true;
        }
        
        // Convertir el filtro a minúsculas para comparación sin distinción de mayúsculas/minúsculas
        String lowerCaseFilter = filtro.toLowerCase();
        
        // Filtrar solo por nombre
        return producto.getNombre().toLowerCase().contains(lowerCaseFilter);
    });
}
    
    private void configurarMenuContextual() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem modificarItem = new MenuItem("Modificar");
        MenuItem eliminarItem = new MenuItem("Eliminar");

        modificarItem.setOnAction(this::modificarProducto);
        eliminarItem.setOnAction(this::eliminarProducto);

        contextMenu.getItems().addAll(modificarItem, eliminarItem);
        productosTableView.setContextMenu(contextMenu);
    }
    
    private void configurarSeleccionFila() {
        productosTableView.setRowFactory(tv -> {
            TableRow<ProductoVO> row = new TableRow<>();
            
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && 
                    event.getClickCount() == 1 && 
                    !row.isEmpty()) {
                    
                    ProductoVO productoSeleccionado = row.getItem();
                    mostrarDetalleProducto(productoSeleccionado);
                }
            });
            return row;
        });
    }
    
    private void mostrarDetalleProducto(ProductoVO producto) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/producto_detalle.fxml"));
        Parent root = loader.load();
        
        // Obtiene el controlador y establece el producto
        ProductoDetalleController controller = loader.getController();
        controller.setProducto(producto);
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Detalles del Producto");
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
        
    } catch (IOException e) {
        e.printStackTrace();
        mostrarAlerta("Error", "No se pudo cargar la ventana de detalles");
    }
}
    
    @FXML
    private void agregarProducto() {
        try {
            File fxmlFile = new File("src/main/resources/scenes/AgregarProductoView.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Agregar Producto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarProductos();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la ventana de agregar producto");
            e.printStackTrace();
        }
    }

    @FXML
    private void modificarProducto(ActionEvent event) {
        ProductoVO seleccionado = productosTableView.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                File fxmlFile = new File("src/main/resources/scenes/ModificarProductoView.fxml");
                FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
                Parent root = loader.load();

                ModificarProductoController controller = loader.getController();
                controller.initData(seleccionado);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modificar Producto");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                cargarProductos();

            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo cargar la ventana de modificación");
                e.printStackTrace();
            }
        } else {
            mostrarAlerta("Advertencia", "Seleccione un producto para modificar");
        }
    }

    @FXML
    private void eliminarProducto(ActionEvent event) {
        ProductoVO seleccionado = productosTableView.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (seleccionado.getStock() < 0) {
                mostrarAlerta("Advertencia", "No se puede eliminar un producto con stock existente");
                return;
            }
            try {
                // Confirmar eliminación
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Confirmar Eliminación");
                confirmacion.setHeaderText(null);
                confirmacion.setContentText("¿Está seguro de eliminar el producto: " + seleccionado.getNombre() + "?");

                if (confirmacion.showAndWait().get() == ButtonType.OK) {
                    boolean eliminado = productoDAO.eliminarProducto(seleccionado.getIdProducto());
                    if (eliminado) {
                        listaProductosOriginal.remove(seleccionado);
                        mostrarAlerta("Éxito", "Producto eliminado correctamente");
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar el producto");
                    }
                }
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al eliminar el producto: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Advertencia", "Seleccione un producto para eliminar");
        }
    }

    @FXML
        private void regresarOpcionesAdmin(ActionEvent event) throws IOException {
            File fxmlFile = new File("src/main/resources/scenes/OpcionesAdministrador.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Parent root = loader.load();
            Stage stage = (Stage) btnAtras.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}