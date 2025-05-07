package com.idar.pdvpapeleria.controllers;

import DAOImp.ProveedorDAOImp;
import VO.ProveedorVO;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import DAO.DatabaseConnection;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminProveedoresViewController implements Initializable {

    private DatabaseConnection db;
    private ProveedorDAOImp proveedorDAO;

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
    private Button agregarViewButton;
    
    @FXML
    private Button editarViewButton;
    
    @FXML
    private Button eliminarViewButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setDB();
            proveedorDAO = new ProveedorDAOImp(db);
            configurarTabla();
            cargarProveedores();
            //agregarViewButton.onAgregarViewButtonClicked();
                    
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Cambia a otra vista y transfiere la conexion con la db al nuevo controlador
     * 
     * @param fxmlPath
     * @param botoncito
     * @throws IOException 
     */
    @FXML
    private void switchToView(String fxmlPath, Button botoncito) throws IOException {
        System.out.println(fxmlPath);
        File fxmlFile = new File(fxmlPath);
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();

    // Obtener el controlador y pasar la conexión
        Object controller = loader.getController();
        
        //Aca checa a cual controlador le va a pasar la conexión
        try {
            if (controller instanceof AdminAgregarProveedorViewController) {
                ((AdminAgregarProveedorViewController) controller).setDB(this.db);
            } else if (controller instanceof AdminEditarProveedorViewController) {
                ((AdminEditarProveedorViewController) controller).setDB(this.db);
            } else if (controller instanceof AdminEliminarProveedorViewController) {
                ((AdminEliminarProveedorViewController) controller).setDB(this.db);
            }
        } catch (SQLException e) {
            error("Error al pasar la conexión a la nueva vista: " + e.getMessage());
            e.printStackTrace();
        }

        //Muestra la nueva ventana
        Stage stage = (Stage) botoncito.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
    /**
     * Al hacer click en el boton "Agregar" abre el respectivo FXML
     * 
     * @throws IOException 
     */
    @FXML
    private void onAgregarViewButtonClicked() throws IOException{
        switchToView("src/main/resources/scenes/adminAgregarProveedor.fxml", agregarViewButton);
        System.out.println("Se presionó el boton agregar");  
    }
    
    @FXML
    private void onEditarViewButtonClicked() throws IOException{
        switchToView("src/main/resources/scenes/adminEditarProveedor.fxml", editarViewButton);
        System.out.println("Se presionó el boton editar");
    }
    
    /**
     * Al hacer click en el boton "Eliminar" abre el respectivo FXML
     * 
     * @throws IOException 
     */
    @FXML
    private void onEliminarViewButtonClicked() throws IOException{
        switchToView("src/main/resources/scenes/adminEliminarProveedor.fxml", eliminarViewButton);
        System.out.println("Se presionó el boton eliminar");
    }
    
    /**
     * Configura las columnas de la tabla para que muestren los campos correspondientes
     */
    private void configurarTabla() {
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        columnaServicio.setCellValueFactory(new PropertyValueFactory<>("servicioProveedor"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));
    }

    /**
     * Carga todos los proveedores desde la bd en un arraylist para su lectura en la tabla
     */
    private void cargarProveedores() {
        try {
            List<ProveedorVO> proveedores = proveedorDAO.obtenerTodosProveedores();
            ObservableList<ProveedorVO> datos = FXCollections.observableArrayList(proveedores);
            tablaProveedores.setItems(datos);
        } catch (SQLException e) {
            error(e.getMessage());
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }

    /**
     * Establece la conexion con la bd usando "DataConnection"
     * 
     * @throws SQLException 
     */
    public void setDB() throws SQLException {
        this.db = DatabaseConnection.getInstance();
    }

    /**
     * Muestra un mensaje de error en la UI
     * 
     * @param texto 
     */
    public void error(String texto) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null); 
        alert.setContentText("Error: " + texto);
        alert.show();
    }    

}
