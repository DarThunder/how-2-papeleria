package com.idar.pdvpapeleria.controllers;

import DAOImp.VentaDAOImp;
import VO.HistorialVentaVO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para la interfaz de historial de ventas en el sistema de punto de venta.
 * <p>
 * Se encarga de mostrar en una tabla el listado de ventas realizadas, así como permitir
 * al usuario buscar ventas por distintos criterios: cajero, folio, total y fecha.
 * Además, permite navegar de regreso a la vista principal del dueño.
 * </p>
 * 
 * <p>
 * Utiliza objetos de tipo {@link HistorialVentaVO} y el acceso a la base de datos
 * se realiza mediante {@link VentaDAOImp}.
 * </p>
 * 
 * @author jazmin
 */
public class HistorialVentasController {

    /** Tabla principal que contiene el historial de ventas. */
    @FXML private TableView<HistorialVentaVO> historialTableView;

    /** Columna que muestra la fecha en que se realizó cada venta. */
    @FXML private TableColumn<HistorialVentaVO, String> fechaCol;

    /** Columna que muestra el nombre del cajero y su ID asociado a cada venta. */
    @FXML private TableColumn<HistorialVentaVO, String> cajeroCol;

    /** Columna que muestra los productos vendidos en cada transacción. */
    @FXML private TableColumn<HistorialVentaVO, String> detallesCol;

    /** Columna que muestra el número de folio (ID único) de cada venta. */
    @FXML private TableColumn<HistorialVentaVO, Integer> folioCol;

    /** Columna que muestra el total monetario de cada venta. */
    @FXML private TableColumn<HistorialVentaVO, String> totalCol;

    /** Desplegable que permite al usuario elegir el tipo de búsqueda a realizar. */
    @FXML private ComboBox<String> parametroBusquedaCombo;

    /** Campo de texto para introducir el valor a buscar según el parámetro seleccionado. */
    @FXML private TextField BuscarField;

    /** Botón que permite regresar a la vista principal del dueño. */
    @FXML private Button buttonAtras;

    /** Instancia del DAO para acceder al historial de ventas desde la base de datos. */
    private final VentaDAOImp ventaDao = VentaDAOImp.getInstance();

    /**
     * Inicializa el controlador una vez que el archivo FXML ha sido cargado.
     * <p>
     * Este método configura las columnas de la tabla, los parámetros de búsqueda,
     * carga los datos del historial y establece eventos de interacción.
     * </p>
     */
    @FXML
    public void initialize() {
        configurarColumnas();
        configurarBusqueda();
        cargarHistorial();
        configurarEventos();
    }

    /**
     * Configura los datos que serán mostrados en cada columna de la tabla.
     * Utiliza propiedades del objeto {@code HistorialVentaVO} y formatea algunos campos.
     */
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

        // Ajustes visuales
        detallesCol.setPrefWidth(200);
        fechaCol.setPrefWidth(120);
        cajeroCol.setPrefWidth(100);
        folioCol.setPrefWidth(80);
        totalCol.setPrefWidth(100);
    }

    /**
     * Configura los tipos de búsqueda que el usuario puede seleccionar desde el ComboBox.
     * Incluye búsqueda por cajero, folio, total, fecha y una opción para mostrar todas las ventas.
     */
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

    /**
     * Agrega eventos de interacción a los componentes de la vista, como mostrar
     * un tooltip con los detalles al pasar el mouse sobre una fila de la tabla.
     */
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

    /**
     * Obtiene el historial completo de ventas desde la base de datos y lo muestra en la tabla.
     * Si ocurre un error, se muestra una alerta informativa al usuario.
     */
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

    /**
     * Ejecuta una búsqueda filtrada según el parámetro y el valor ingresado por el usuario.
     * <p>
     * Soporta los siguientes tipos de búsqueda:
     * <ul>
     *   <li>Por cajero (nombre o ID)</li>
     *   <li>Por folio (entero)</li>
     *   <li>Por total (entero)</li>
     *   <li>Por fecha (formato YYYY-MM-DD)</li>
     * </ul>
     * </p>
     * Muestra alertas en caso de errores de formato o resultados vacíos.
     */
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

    /**
     * Muestra una ventana de alerta informativa con el título y mensaje indicados.
     *
     * @param titulo  Título de la alerta.
     * @param mensaje Contenido del mensaje a mostrar.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cambia la vista actual a la escena del dueño del sistema.
     * <p>
     * Este método se ejecuta cuando el usuario hace clic en el botón "Atrás".
     * </p>
     *
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @FXML
    private void switchToDueñoView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/dueñoView.fxml"));
        Stage stage = (Stage) buttonAtras.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.sizeToScene();
        stage.centerOnScreen();
    }
}
