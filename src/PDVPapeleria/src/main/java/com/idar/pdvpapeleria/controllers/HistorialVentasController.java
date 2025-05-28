package com.idar.pdvpapeleria.controllers;

/**
 *
 * @author jazmin
 */

import DAOImp.VentaDAOImp;
import VO.HistorialVentaVO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HistorialVentasController {

    @FXML private TableView<HistorialVentaVO> historialTableView;
    @FXML private TableColumn<HistorialVentaVO, String> fechaCol;
    @FXML private TableColumn<HistorialVentaVO, String> cajeroCol;
    @FXML private TableColumn<HistorialVentaVO, String> detallesCol;
    @FXML private TableColumn<HistorialVentaVO, Integer> folioCol;
    @FXML private TableColumn<HistorialVentaVO, String> totalCol;
    @FXML private ComboBox<String> parametroBusquedaCombo;
    @FXML private TextField BuscarField;
    @FXML private Button buttonAtras;

    private final VentaDAOImp ventaDao = VentaDAOImp.getInstance();

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarBusqueda();
        cargarHistorial();
        configurarEventos();
    }

    private void configurarColumnas() {
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada"));
        cajeroCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombreEmpleado() + " (ID: " + 
                                   cellData.getValue().getIdEmpleado() + ")"));
        folioCol.setCellValueFactory(new PropertyValueFactory<>("folio"));
        totalCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty("$" + cellData.getValue().getTotal()));
        
        detallesCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDetallesConsolidados()));

        // Ajustar anchos
        detallesCol.setPrefWidth(200);
        fechaCol.setPrefWidth(120);
        cajeroCol.setPrefWidth(100);
        folioCol.setPrefWidth(80);
        totalCol.setPrefWidth(100);
    }

    private void configurarBusqueda() {
        parametroBusquedaCombo.getItems().addAll(
            "Mostrar todos",
            "Buscar por cajero",
            "Buscar por folio",
            "Buscar por total",
            "Buscar por fecha"
        );
        parametroBusquedaCombo.getSelectionModel().selectFirst();
    }

    private void configurarEventos() {
        historialTableView.setRowFactory(tv -> {
            TableRow<HistorialVentaVO> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    Tooltip tooltip = new Tooltip(row.getItem().getDetallesConsolidados());
                    tooltip.setStyle("-fx-font-size: 12px;");
                    row.setTooltip(tooltip);
                }
            });
            return row;
        });

        parametroBusquedaCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            BuscarField.clear();
            if ("Mostrar todos".equals(newVal)) {
                cargarHistorial();
            }
        });
    }

    private void cargarHistorial() {
        try {
            List<HistorialVentaVO> ventas = ventaDao.obtenerHistorialVentas();
            historialTableView.setItems(FXCollections.observableArrayList(ventas));
            historialTableView.refresh();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarVentas() {
        String parametro = parametroBusquedaCombo.getValue();
        String valor = BuscarField.getText().trim();

        try {
            ObservableList<HistorialVentaVO> resultados = FXCollections.observableArrayList();

            if (parametro == null || parametro.equals("Mostrar todos")) {
                cargarHistorial();
                return;
            }

            if (valor.isEmpty()) {
                mostrarAlerta("Advertencia", "Debe ingresar un valor para buscar");
                return;
            }

            switch (parametro) {
                case "Buscar por cajero":
                    resultados.addAll(ventaDao.buscarVentasPorEmpleado(valor));
                    break;
                case "Buscar por folio":
                    try {
                        resultados.addAll(ventaDao.buscarVentasPorFolio(Integer.parseInt(valor)));
                    } catch (NumberFormatException e) {
                        mostrarAlerta("Error", "El folio debe ser un número entero");
                        return;
                    }
                    break;
                case "Buscar por total":
                    try {
                        resultados.addAll(ventaDao.buscarVentasPorTotal(Integer.parseInt(valor)));
                    } catch (NumberFormatException e) {
                        mostrarAlerta("Error", "El total debe ser un número entero");
                        return;
                    }
                    break;
                case "Buscar por fecha":
                    if (!valor.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        mostrarAlerta("Error", "Formato de fecha inválido. Use YYYY-MM-DD");
                        return;
                    }
                    resultados.addAll(ventaDao.buscarVentasPorFecha(valor));
                    break;
            }

            if (resultados.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron resultados");
            } else {
                historialTableView.setItems(resultados);
                historialTableView.refresh();
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void switchToDueñoView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/dueñoView.fxml"));
        Stage stage = (Stage) buttonAtras.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.sizeToScene();
        stage.centerOnScreen();
    }
}