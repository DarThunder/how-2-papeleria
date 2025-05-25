package com.idar.pdvpapeleria.controllers;

import DAOImp.ProductoDAOImp;
import DAOImp.VentaDAOImp;
import VO.EmpleadoVO;
import VO.ProductoVO;
import Vista.AlertaPDV;
import com.idar.pdvpapeleria.utils.PDFGenerator;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CajeroViewController implements Initializable {

    // DAOs
    private ProductoDAOImp productoDao;
    private VentaDAOImp ventaDao;
    
    // Datos del cajero
    private EmpleadoVO cajeroActual;
    
    // Modelos de datos
    private final ObservableList<ProductoVO> products = FXCollections.observableArrayList();
    private final Map<Integer, ProductoVO> productMap = new HashMap<>();
    private boolean isSellInAction;
    
    // Componentes FXML
    @FXML private TableView<ProductoVO> productosTableView;
    @FXML private TableColumn<ProductoVO, String> productNameCol;
    @FXML private TableColumn<ProductoVO, Integer> productCountCol;
    @FXML private TableColumn<ProductoVO, Integer> productPriceCol;
    @FXML private TableColumn<ProductoVO, Integer> productSubtotalCol;
    @FXML private Label totalLabel;
    @FXML private Label estadoVentaLabel;
    @FXML private Label nombreCajeroLabel;
    @FXML private VBox productoInfoBox;
    @FXML private Button cerrarCajaB; 

    // Método para establecer el cajero actual
    public void setCajeroActual(EmpleadoVO cajero) {
        this.cajeroActual = cajero;
        updateCajeroInfo();
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización de DAOs
        productoDao = ProductoDAOImp.getInstance();
        ventaDao = VentaDAOImp.getInstance();
        
        // Configuración inicial
        isSellInAction = false;
        configureTableColumns();
        updateUIState();
        updateCajeroInfo();

        
        // Configurar listener para selección de productos
        productosTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> showProductDetails(newSelection));
        
        // Configurar atajos de teclado
        Platform.runLater(() -> {
            if (productosTableView.getScene() != null) {
                productosTableView.getScene().setOnKeyPressed(this::handleKeyEvents);
            }
        });
    }
    
    private void updateCajeroInfo() {
        if (nombreCajeroLabel != null && cajeroActual != null) {
            nombreCajeroLabel.setText("Cajero: " + cajeroActual.getNombre());
        }
    }

    private void configureTableColumns() {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        productCountCol.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("precioDeVenta"));
        productSubtotalCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getSubtotal()).asObject());
        
        productosTableView.setItems(products);
    }

    private void updateTotal() {
        int total = products.stream()
            .mapToInt(ProductoVO::getSubtotal)
            .sum();
        totalLabel.setText(String.format("Total: $%,d", total));
    }

    private void updateUIState() {
        String estado = isSellInAction ? "Venta en curso" : "Venta no iniciada";
        String color = isSellInAction ? "#4CAF50" : "#F44336";
        
        estadoVentaLabel.setText("Estado: " + estado);
        estadoVentaLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    private void showProductDetails(ProductoVO producto) {
        productoInfoBox.getChildren().clear();
        
        if (producto != null) {
            productoInfoBox.getChildren().addAll(
                new Label("Nombre: " + producto.getNombre()),
                new Label("Precio: $" + producto.getPrecioDeVenta()),
                new Label("En carrito: " + producto.getCantidad()),
                new Label("Subtotal: $" + producto.getSubtotal()),
                new Label("Stock disponible: " + producto.getStock())
            );
        } else {
            productoInfoBox.getChildren().add(
                new Label("Seleccione un producto")
            );
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onInitButtonClicked(ActionEvent event) {
        if (!isSellInAction) {
            isSellInAction = true;
            products.clear();
            productMap.clear();
            updateTotal();
            updateUIState();
            showAlert(Alert.AlertType.INFORMATION, "Venta iniciada", "Se inició una nueva venta");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Venta en curso", "Ya hay una venta en proceso");
        }
    }

    @FXML
    private void onCancelButtonClicked(ActionEvent event) {
        if (isSellInAction) {
            isSellInAction = false;
            products.clear();
            productMap.clear();
            updateTotal();
            updateUIState();
            showAlert(Alert.AlertType.INFORMATION, "Venta cancelada", "La venta ha sido cancelada");
        }
    }

    @FXML
    private void onAddButtonClicked(ActionEvent event) {
        if (!isSellInAction) {
            showAlert(Alert.AlertType.WARNING, "Venta no iniciada", "Debe iniciar una venta primero");
            return;
        }
        showProductPopup(false);
    }

    @FXML
    private void onRemoveButtonClicked(ActionEvent event) {
        if (!isSellInAction) {
            showAlert(Alert.AlertType.WARNING, "Venta no iniciada", "Debe iniciar una venta primero");
            return;
        }
        showProductPopup(true);
    }

    private void showProductPopup(boolean isRemoveOperation) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(isRemoveOperation ? "Quitar producto" : "Agregar producto");

        // Crear componentes
        Label idLabel = new Label("ID del producto:");
        TextField idField = new TextField();
        idField.setPromptText("Ingrese el ID");

        Label cantidadLabel = new Label("Cantidad:");
        TextField cantidadField = new TextField();
        cantidadField.setPromptText("Ingrese la cantidad");

        Button aceptarBtn = new Button("Aceptar");
        aceptarBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        aceptarBtn.setOnAction(e -> {
            handleProductOperation(idField.getText(), cantidadField.getText(), isRemoveOperation);
            popup.close();
        });

        // Configurar layout
        VBox layout = new VBox(10, idLabel, idField, cantidadLabel, cantidadField, aceptarBtn);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #f5f5f5;");

        popup.setScene(new Scene(layout));
        popup.showAndWait();
    }

    private void handleProductOperation(String idProducto, String cantidad, boolean isRemoveOperation) {
        try {
            int id = Integer.parseInt(idProducto.trim());
            int cant = cantidad.isEmpty() ? 1 : Integer.parseInt(cantidad.trim());
            
            if (isRemoveOperation) {
                removeProduct(id);
            } else {
                addProduct(id, cant);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID y cantidad deben ser números válidos");
        }
    }

    private void addProduct(int id, int cantidad) {
        try {
            ProductoVO existingProduct = productMap.get(id);
            
            if (existingProduct != null) {
                existingProduct.setCantidad(existingProduct.getCantidad() + cantidad);
            } else {
                ProductoVO newProduct = productoDao.getProductoById(id);
                if (newProduct != null) {
                    newProduct.setCantidad(cantidad);
                    products.add(newProduct);
                    productMap.put(id, newProduct);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se encontró el producto con ID: " + id);
                    return;
                }
            }
            
            productosTableView.refresh();
            updateTotal();
            showProductDetails(productosTableView.getSelectionModel().getSelectedItem());
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al agregar el producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeProduct(int id) {
        ProductoVO product = productMap.get(id);
        if (product != null) {
            products.remove(product);
            productMap.remove(id);
            productosTableView.refresh();
            updateTotal();
            showProductDetails(productosTableView.getSelectionModel().getSelectedItem());
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "El producto no está en la venta actual");
        }
    }

    @FXML
    private void onPayButtonClicked(ActionEvent event) {
        if (!isSellInAction || products.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "No hay productos en la venta actual");
            return;
        }

        try {
            int total = products.stream()
                .mapToInt(ProductoVO::getSubtotal)
                .sum();

            int idEmpleado = cajeroActual != null ? cajeroActual.getId() : 1;
            int folio = ventaDao.generarVenta(idEmpleado, total);

            if (folio == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo generar la venta");
                return;
            }

            // Procesar cada producto
            for (ProductoVO producto : products) {
                if (!productoDao.reducirStock(producto.getIdProducto(), producto.getCantidad())) {
                    ventaDao.rollbackVenta(folio);
                    showAlert(Alert.AlertType.ERROR, "Error", "No hay suficiente stock para " + producto.getNombre());
                    return;
                }
            }

            // Generar PDF del ticket
            String nombreCajero = cajeroActual != null ? cajeroActual.getNombre() : "Sistema";
            PDFGenerator.generarTicketVenta(products, folio, total, nombreCajero);

            // Mostrar confirmación
            showAlert(Alert.AlertType.INFORMATION, "Venta completada", 
                String.format("Venta #%d completada\nTotal: $%,d\nTicket generado", folio, total));

            // Limpiar y finalizar
            products.clear();
            productMap.clear();
            isSellInAction = false;
            updateTotal();
            updateUIState();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al procesar el pago: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleKeyEvents(KeyEvent event) {
        switch (event.getCode()) {
            case F4 -> onInitButtonClicked(new ActionEvent());
            case F5 -> onCancelButtonClicked(new ActionEvent());
            case F6 -> onAddButtonClicked(new ActionEvent());
            case F7 -> onRemoveButtonClicked(new ActionEvent());
            case F8 -> onPayButtonClicked(new ActionEvent());
        }
    }
    
    @FXML
    /**
     * Método para cerrar la caja por medio de una confirmación
     */
    private void cerrarCaja(){
        boolean opcion = AlertaPDV.mostrarConfirmacion("Cerrar caja","¿Quiéres cerrar la caja y terminar tu sesión?" );
        if(opcion){
            try {
                regresarAlogin();
                //MomichisYam estuvo aqui!!!
            } catch (IOException ex) {
                AlertaPDV.mostrarExcepcion("Error", "No se pudo cambiar al login", ex);
            }
        }
    }
    
    @FXML
    /**
     * Método para regresar al login
     */
    public void regresarAlogin() throws MalformedURLException, IOException {
        File fxmlFile = new File("src/main/resources/scenes/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        Stage stage = (Stage) cerrarCajaB.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}