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
import javafx.animation.FadeTransition;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import Vista.AlertaPDV;

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
            AlertaPDV.mostrarError("Error", "No se pudieron cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBuscador() {
        datosFiltrados = new FilteredList<>(listaProductosOriginal, p -> true);

        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarPorNombre(newValue);
        });

        btnBuscar.setOnAction(event -> filtrarPorNombre(buscarField.getText()));

        SortedList<ProductoVO> datosOrdenados = new SortedList<>(datosFiltrados);
        datosOrdenados.comparatorProperty().bind(productosTableView.comparatorProperty());
        productosTableView.setItems(datosOrdenados);
    }

    private void filtrarPorNombre(String filtro) {
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
                if (event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 1
                        && !row.isEmpty()) {

                    ProductoVO productoSeleccionado = row.getItem();
                    mostrarDetalleProducto(productoSeleccionado);
                }
            });
            return row;
        });
    }

    private void mostrarDetalleProducto(ProductoVO producto) {
    // Crear diálogo
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Detalles del Producto");
    
    // Configurar botón de cierre
    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
    
    // Crear contenido
    VBox content = new VBox(10);
    content.setPadding(new Insets(15));
    content.setStyle("-fx-background-color: #c4def6;");
    
    // Encabezado
    Label header = new Label("Detalle del Producto");
    header.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: white;");
    StackPane headerPane = new StackPane(header);
    headerPane.setStyle("-fx-background-color: #07575b; -fx-padding: 10;");
    
    // Detalles
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(5);
    grid.setPadding(new Insets(10));
    
    grid.add(new Label("ID:"), 0, 0);
    grid.add(new Label(producto.getIdProducto() + ""), 1, 0);
    
    grid.add(new Label("Nombre:"), 0, 1);
    grid.add(new Label(producto.getNombre()), 1, 1);
    
    grid.add(new Label("Precio Compra:"), 0, 2);
    grid.add(new Label("$" + producto.getPrecioDeCompra()), 1, 2);
    
    grid.add(new Label("Precio Venta:"), 0, 3);
    grid.add(new Label("$" + producto.getPrecioDeVenta()), 1, 3);
    
    grid.add(new Label("Stock:"), 0, 4);
    grid.add(new Label(producto.getStock() + ""), 1, 4);
    
    grid.add(new Label("Categoría:"), 0, 5);
    grid.add(new Label(producto.getCategoria()), 1, 5);
    
    grid.add(new Label("Descripción:"), 0, 6);
    Label descLabel = new Label(producto.getDescripcion());
    descLabel.setWrapText(true);
    descLabel.setMaxWidth(300);
    grid.add(descLabel, 1, 6);
    
    content.getChildren().addAll(headerPane, grid);
    
    // Configurar diálogo
    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().setMinWidth(400);
    
    // Configurar animación
    dialog.getDialogPane().setOpacity(0); // Inicia invisible
    
    // Animación de aparición
    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialog.getDialogPane());
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    
    // Animación al mostrar
    dialog.setOnShown(e -> fadeIn.play());
    
    // Mostrar diálogo
    dialog.showAndWait();
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
            AlertaPDV.mostrarError("Advertencia", "Seleccione un producto para modificar");
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
                AlertaPDV.mostrarError("Error", "No se pudo cargar la ventana de modificación");
                e.printStackTrace();
            }
        } else {
            AlertaPDV.mostrarError("Advertencia", "Seleccione un producto para modificar");
        }
    }

    @FXML
    private void eliminarProducto(ActionEvent event) {
        ProductoVO seleccionado = productosTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaPDV.mostrarError("Advertencia", "Seleccione un producto para eliminar");
            return;
        }

        if (seleccionado.getStock() > 0) {
            boolean confirmar = AlertaPDV.mostrarConfirmacion(
                "Producto con stock", 
                "Este producto tiene " + seleccionado.getStock() + " unidades en stock.\n" +
                "¿Está seguro que desea eliminarlo de todos modos?"
            );

            if (!confirmar) {
                return; // El usuario canceló la eliminación
            }
        }

        // Confirmación final de eliminación
        if (AlertaPDV.mostrarConfirmacion("Confirmar Eliminación", 
            "¿Está seguro de eliminar el producto: " + seleccionado.getNombre() + "?")) {

            try {
                boolean eliminado = productoDAO.eliminarProducto(seleccionado.getIdProducto());
                if (eliminado) {
                    listaProductosOriginal.remove(seleccionado);
                    AlertaPDV.mostrarExito("Éxito", "Producto eliminado correctamente");
                } else {
                    AlertaPDV.mostrarError("Error", "No se pudo eliminar el producto");
                }
            } catch (SQLException e) {
                AlertaPDV.mostrarExcepcion("Error", "Error al eliminar el producto", e);
            }
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
