package com.idar.pdvpapeleria.controllers;

import DAO.DatabaseConnection;
import DAOImp.ProveedorDAOImp;
import VO.ProveedorVO;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Vista.AlertaPDV;

public class AdminEliminarProveedorViewController implements Initializable {

    @FXML
    private TableView<ProveedorVO> tablaProveedores;
    @FXML
    private TableColumn<ProveedorVO, Integer> columnaId;
    @FXML
    private TableColumn<ProveedorVO, String> columnaNombre;
    @FXML
    private TableColumn<ProveedorVO, String> columnaTelefono;
    @FXML
    private TableColumn<ProveedorVO, String> columnaServicio;
    @FXML
    private TextField TF_Nombre;
    @FXML
    private TextField TF_Servicio;
    @FXML
    private TextField TF_Telefono;
    @FXML
    private Button agregarViewButton;
    @FXML
    private Button editarViewButton;
    @FXML
    private Button eliminarViewButton;
    @FXML
    private Button eliminarProveedorButton;

    private final AlertaPDV alerta = new AlertaPDV();
    private List<ProveedorVO> proveedores = new ArrayList<>();
    private DatabaseConnection db;
    private ProveedorDAOImp proveedorDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private void configurarTabla() {
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    private void cargarProveedores() {
        try {
            proveedores = proveedorDAO.obtenerTodosProveedores();
            ObservableList<ProveedorVO> datos = FXCollections.observableArrayList(proveedores);
            tablaProveedores.setItems(datos);
        } catch (SQLException e) {
            alerta.mostrarExcepcion("Error", "Error al cargar proveedores", e);
        }
    }

    @FXML
    private void eliminarProveedor() {
        int indice = tablaProveedores.getSelectionModel().getSelectedIndex();

        if (indice > -1) {
            boolean resultado = alerta.mostrarConfirmacion("Eliminar",
                    "¡Seguro que desea eliminar el siguiente proveedor?");
            if (resultado) {
                try {
                    proveedorDAO.eliminarProveedor(proveedores, indice);
                    cargarProveedores();
                    alerta.mostrarExito("Éxito", "El proveedor fue eliminado");
                } catch (SQLException e) {
                    alerta.mostrarExcepcion("Error", "No se pudo eliminar el proveedor", e);
                }
            }
        } else {
            alerta.mostrarError("Error", "Selecciona a un proveedor de la tabla");
        }
    }

    public void setDB(DatabaseConnection db) throws SQLException {
        this.db = db;
        this.proveedorDAO = new ProveedorDAOImp(db);
        configurarTabla();
        cargarProveedores();
    }
}
