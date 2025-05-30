package com.idar.pdvpapeleria.controllers;

import DAO.ProductoDAO;
import DAOImp.ProductoDAOImp;
import VO.HistorialProductoVO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ObservableValue;

/**
 * Controlador para la vista de administración de productos. Permite gestionar
 * productos, incluyendo operaciones CRUD, búsqueda y filtrado.
 */
public class AdminViewController {

    /**
     * Umbral para considerar stock bajo
     */
    private static final int STOCK_BAJO_UMBRAL = 10;

    /**
     * Estilo CSS para resaltar productos con stock bajo
     */
    private static final String ESTILO_STOCK_BAJO = "-fx-background-color: #ffcccc; -fx-font-weight: bold;";

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
    @FXML
    private ComboBox<String> cbParametrosBusqueda;
    @FXML
    private Button btnLimpiar;

    private ProductoDAO productoDAO;
    private ObservableList<ProductoVO> listaProductosOriginal;
    private FilteredList<ProductoVO> datosFiltrados;
    private final Map<String, String> parametrosBusqueda = new HashMap<>();
    private String ultimoParametroBusqueda = "";
    private String ultimoTextoBusqueda = "";

    private static List<HistorialProductoVO> historialProductos = new ArrayList<>();
    ModificarProductoController historial = new ModificarProductoController();

    /**
     * Inicializa el controlador después de que su elemento raíz haya sido
     * procesado.
     *
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    @FXML
    public void initialize() throws SQLException {
        productoDAO = ProductoDAOImp.getInstance();
        configurarParametrosBusqueda();
        configurarComponentes();
        configurarColumnas();
        cargarProductos();
        configurarBuscador();
        configurarSeleccionFila();
        configurarMenuContextual();
        historialProductos = historial.getHistorial();
    }

    /**
     * Configura los parámetros de búsqueda disponibles.
     */
    private void configurarParametrosBusqueda() {
        parametrosBusqueda.put("Nombre", "nombre");
        parametrosBusqueda.put("ID", "idProducto");
        parametrosBusqueda.put("Precio Compra", "precioDeCompra");
        parametrosBusqueda.put("Precio Venta", "precioDeVenta");
        parametrosBusqueda.put("Stock", "stock");
        parametrosBusqueda.put("Categoría", "categoria");
    }

    /**
     * Configura los componentes iniciales de la interfaz.
     */
    private void configurarComponentes() {
        cbParametrosBusqueda.getItems().addAll(parametrosBusqueda.keySet());
        cbParametrosBusqueda.getSelectionModel().selectFirst();
        btnLimpiar.setOnAction(event -> limpiarBusqueda());
    }

    /**
     * Configura las columnas de la tabla de productos.
     */
    private void configurarColumnas() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioCompraCol.setCellValueFactory(new PropertyValueFactory<>("precioDeCompra"));
        precioVentaCol.setCellValueFactory(new PropertyValueFactory<>("precioDeVenta"));

        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setCellFactory(column -> new TableCell<ProductoVO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    setTooltip(null);
                } else {
                    setText(item.toString());

                    if (item <= STOCK_BAJO_UMBRAL) {
                        setStyle(ESTILO_STOCK_BAJO);
                        setTooltip(new Tooltip("¡Stock bajo! Por favor reabastecer"));
                    } else {
                        setStyle("");
                        setTooltip(null);
                    }
                }
            }
        });

        categoriaCol.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    /**
     * Carga los productos desde la base de datos y los muestra en la tabla.
     */
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

            if (datosFiltrados != null) {
                datosFiltrados = new FilteredList<>(listaProductosOriginal, p -> true);
                SortedList<ProductoVO> datosOrdenados = new SortedList<>(datosFiltrados);
                datosOrdenados.comparatorProperty().bind(productosTableView.comparatorProperty());
                productosTableView.setItems(datosOrdenados);
            } else {
                productosTableView.setItems(listaProductosOriginal);
            }

        } catch (SQLException e) {
            AlertaPDV.mostrarError("Error", "No se pudieron cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura el sistema de búsqueda y filtrado de productos.
     */
    private void configurarBuscador() {
        if (listaProductosOriginal == null) {
            listaProductosOriginal = FXCollections.observableArrayList();
        }

        datosFiltrados = new FilteredList<>(listaProductosOriginal, p -> true);

        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            String parametro = cbParametrosBusqueda.getValue();
            String texto = newValue.trim().toLowerCase();

            datosFiltrados.setPredicate(producto -> {
                if (texto.isEmpty()) {
                    return true;
                }

                try {
                    switch (parametro) {
                        case "Nombre":
                            return producto.getNombre().toLowerCase().contains(texto);
                        case "ID":
                            return String.valueOf(producto.getIdProducto()).contains(texto);
                        case "Categoría":
                            return producto.getCategoria().toLowerCase().contains(texto);
                        case "Precio Compra":
                            return String.valueOf(producto.getPrecioDeCompra()).contains(texto);
                        case "Precio Venta":
                            return String.valueOf(producto.getPrecioDeVenta()).contains(texto);
                        case "Stock":
                            return String.valueOf(producto.getStock()).contains(texto);
                        default:
                            return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            });
        });

        cbParametrosBusqueda.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    String texto = buscarField.getText().trim();
                    if (!texto.isEmpty()) {
                        buscarField.textProperty().set(texto);
                    }
                });

        btnBuscar.setOnAction(event -> {
            String texto = buscarField.getText().trim();
            if (!texto.isEmpty()) {
                buscarField.textProperty().set(texto);
            }
        });

        btnLimpiar.setOnAction(event -> {
            buscarField.clear();
            cbParametrosBusqueda.getSelectionModel().clearSelection();
            datosFiltrados.setPredicate(p -> true);
        });

        SortedList<ProductoVO> datosOrdenados = new SortedList<>(datosFiltrados);
        datosOrdenados.comparatorProperty().bind(productosTableView.comparatorProperty());
        productosTableView.setItems(datosOrdenados);
    }

    /**
     * Filtra productos por campos numéricos.
     *
     * @param texto Texto de búsqueda
     * @param valor Valor numérico a comparar
     * @return true si el valor coincide con el criterio de búsqueda
     */
    private boolean filtrarPorNumero(String texto, int valor) {
        try {
            texto = texto.trim();

            if (texto.startsWith(">")) {
                int valorBuscado = Integer.parseInt(texto.substring(1).trim());
                return valor > valorBuscado;
            } else if (texto.startsWith("<")) {
                int valorBuscado = Integer.parseInt(texto.substring(1).trim());
                return valor < valorBuscado;
            } else {
                int valorBuscado = Integer.parseInt(texto);
                return valor == valorBuscado;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Aplica un filtro a la lista de productos.
     *
     * @param parametroSeleccionado Parámetro por el que filtrar
     * @param textoBusqueda Texto de búsqueda
     */
    private void aplicarFiltro(String parametroSeleccionado, String textoBusqueda) {
        if (parametroSeleccionado == null || listaProductosOriginal == null) {
            productosTableView.setItems(listaProductosOriginal);
            return;
        }

        String propiedad = parametrosBusqueda.get(parametroSeleccionado);
        datosFiltrados.setPredicate(producto -> {
            try {
                switch (propiedad) {
                    case "nombre":
                        return producto.getNombre().toLowerCase().startsWith(textoBusqueda.toLowerCase());
                    case "categoria":
                        return producto.getCategoria() != null
                                && producto.getCategoria().toLowerCase().startsWith(textoBusqueda.toLowerCase());
                    case "idProducto":
                        return String.valueOf(producto.getIdProducto()).startsWith(textoBusqueda);
                    case "precioDeCompra":
                        return String.valueOf(producto.getPrecioDeCompra()).startsWith(textoBusqueda);
                    case "precioDeVenta":
                        return String.valueOf(producto.getPrecioDeVenta()).startsWith(textoBusqueda);
                    case "stock":
                        return String.valueOf(producto.getStock()).startsWith(textoBusqueda);
                    default:
                        return true;
                }
            } catch (Exception e) {
                AlertaPDV.mostrarError("Advertencia", "Ha ocurrido un error al buscar");
                e.printStackTrace();
                return false;
            }
        });
    }

    /**
     * Limpia los filtros de búsqueda y muestra todos los productos.
     */
    @FXML
    private void limpiarBusqueda() {
        buscarField.clear();
        datosFiltrados.setPredicate(producto -> true);
        productosTableView.refresh();
        ultimoParametroBusqueda = "";
        ultimoTextoBusqueda = "";
    }

    /**
     * Configura el menú contextual para la tabla de productos.
     */
    private void configurarMenuContextual() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem modificarItem = new MenuItem("Modificar");
        MenuItem eliminarItem = new MenuItem("Eliminar");

        modificarItem.setOnAction(this::modificarProducto);
        eliminarItem.setOnAction(this::eliminarProducto);

        contextMenu.getItems().addAll(modificarItem, eliminarItem);
        productosTableView.setContextMenu(contextMenu);
    }

    /**
     * Configura el comportamiento de selección de filas en la tabla.
     */
    private void configurarSeleccionFila() {
        productosTableView.setRowFactory(tv -> {
            TableRow<ProductoVO> row = new TableRow<ProductoVO>() {
                @Override
                protected void updateItem(ProductoVO item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setStyle("");
                    } else {
                        if (item.getStock() <= STOCK_BAJO_UMBRAL) {
                            setStyle(ESTILO_STOCK_BAJO);
                        } else {
                            setStyle("");
                        }
                    }
                }
            };

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

    /**
     * Muestra un diálogo con los detalles de un producto.
     *
     * @param producto Producto a mostrar
     */
    private void mostrarDetalleProducto(ProductoVO producto) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalles del Producto");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #c4def6;");

        Label header = new Label("Detalle del Producto");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: white;");
        StackPane headerPane = new StackPane(header);
        headerPane.setStyle("-fx-background-color: #07575b; -fx-padding: 10;");

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
        Label stockLabel = new Label(producto.getStock() + "");
        if (producto.getStock() <= STOCK_BAJO_UMBRAL) {
            stockLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
        grid.add(stockLabel, 1, 4);

        grid.add(new Label("Categoría:"), 0, 5);
        grid.add(new Label(producto.getCategoria()), 1, 5);

        grid.add(new Label("Descripción:"), 0, 6);
        Label descLabel = new Label(producto.getDescripcion());
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);
        grid.add(descLabel, 1, 6);

        content.getChildren().addAll(headerPane, grid);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setMinWidth(400);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialog.getDialogPane());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        dialog.setOnShown(e -> fadeIn.play());
        dialog.showAndWait();
    }

    /**
     * Muestra el historial de cambios realizados en los productos.
     */
    @FXML
    public void mostrarHistorialCambios() {
        if (historialProductos.isEmpty()) {
            AlertaPDV.mostrarError("Historial vacío", "No hay cambios registrados aún.");
        } else {
            StringBuilder historialTexto = new StringBuilder();
            for (HistorialProductoVO cambio : historialProductos) {
                historialTexto.append(cambio.toString()).append("\n--------------------------\n");
            }
            AlertaPDV.mostrarHistorial("Historial de Cambios", historialTexto.toString());
        }
    }

    /**
     * Abre la ventana para agregar un nuevo producto.
     */
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
            resetearBuscador();

        } catch (IOException e) {
            AlertaPDV.mostrarError("Advertencia", "Error al cargar la ventana de agregar producto");
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana para modificar un producto seleccionado.
     *
     * @param event Evento que disparó la acción
     */
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
                resetearBuscador();

            } catch (IOException e) {
                AlertaPDV.mostrarError("Error", "No se pudo cargar la ventana de modificación");
                e.printStackTrace();
            }
        } else {
            AlertaPDV.mostrarError("Advertencia", "Seleccione un producto para modificar");
        }
    }

    /**
     * Elimina el producto seleccionado después de confirmación.
     *
     * @param event Evento que disparó la acción
     */
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
                    "Este producto tiene " + seleccionado.getStock() + " unidades en stock.\n"
                    + "¿Está seguro que desea eliminarlo de todos modos?"
            );

            if (!confirmar) {
                return;
            }
        }

        if (AlertaPDV.mostrarConfirmacion("Confirmar Eliminación",
                "¿Está seguro de eliminar el producto: " + seleccionado.getNombre() + "?")) {

            try {
                boolean eliminado = productoDAO.eliminarProducto(seleccionado.getIdProducto());
                if (eliminado) {
                    cargarProductos();
                    resetearBuscador();
                    AlertaPDV.mostrarExito("Éxito", "Producto eliminado correctamente");
                } else {
                    AlertaPDV.mostrarError("Error", "No se pudo eliminar el producto");
                }
            } catch (SQLException e) {
                AlertaPDV.mostrarExcepcion("Error", "Error al eliminar el producto", e);
            }
        }
    }

    /**
     * Resetea el buscador a su estado inicial.
     */
    private void resetearBuscador() {
        buscarField.clear();
        datosFiltrados.setPredicate(producto -> true);
        productosTableView.refresh();
        ultimoParametroBusqueda = "";
        ultimoTextoBusqueda = "";
    }

    /**
     * Regresa a la ventana de opciones de administrador.
     *
     * @param event Evento que disparó la acción
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    private void regresarOpcionesAdmin(ActionEvent event) throws IOException {
        File fxmlFile = new File("src/main/resources/scenes/OpcionesAdministrador.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) btnAtras.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Ordena los productos por ID ascendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorIdAsc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        idCol.setSortType(TableColumn.SortType.ASCENDING);
        productosTableView.getSortOrder().add(idCol);
    }

    /**
     * Ordena los productos por ID descendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorIdDesc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        idCol.setSortType(TableColumn.SortType.DESCENDING);
        productosTableView.getSortOrder().add(idCol);
    }

    /**
     * Ordena los productos por nombre ascendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorNombreAsc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        nombreCol.setSortType(TableColumn.SortType.ASCENDING);
        productosTableView.getSortOrder().add(nombreCol);
    }

    /**
     * Ordena los productos por nombre descendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorNombreDesc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        nombreCol.setSortType(TableColumn.SortType.DESCENDING);
        productosTableView.getSortOrder().add(nombreCol);
    }

    /**
     * Ordena los productos por precio de venta ascendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorPrecioVentaAsc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        precioVentaCol.setSortType(TableColumn.SortType.ASCENDING);
        productosTableView.getSortOrder().add(precioVentaCol);
    }

    /**
     * Ordena los productos por precio de venta descendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorPrecioVentaDesc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        precioVentaCol.setSortType(TableColumn.SortType.DESCENDING);
        productosTableView.getSortOrder().add(precioVentaCol);
    }

    /**
     * Ordena los productos por stock ascendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorStockAsc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        stockCol.setSortType(TableColumn.SortType.ASCENDING);
        productosTableView.getSortOrder().add(stockCol);
    }

    /**
     * Ordena los productos por stock descendente.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void ordenarPorStockDesc(ActionEvent event) {
        productosTableView.getSortOrder().clear();
        stockCol.setSortType(TableColumn.SortType.DESCENDING);
        productosTableView.getSortOrder().add(stockCol);
    }

    /**
     * Quita cualquier ordenación aplicada a la tabla.
     *
     * @param event Evento que disparó la acción
     */
    @FXML
    private void quitarOrden(ActionEvent event) {
        productosTableView.getSortOrder().clear();
    }
}
