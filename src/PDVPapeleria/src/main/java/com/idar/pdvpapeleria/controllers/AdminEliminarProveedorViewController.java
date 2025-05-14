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
import javafx.scene.control.*;
import javax.swing.JOptionPane;
import DAO.DatabaseConnection;
import Vista.AlertaPDV;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author goatt
 */
public class AdminEliminarProveedorViewController implements Initializable {

    AlertaPDV alerta = new AlertaPDV();
    List<ProveedorVO> proveedores = new ArrayList<ProveedorVO>();
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {   
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
        //Agregar el controlador de editar
        try {
            if (controller instanceof AdminAgregarProveedorViewController) {
                ((AdminAgregarProveedorViewController) controller).setDB(this.db);
            } else if (controller instanceof AdminEliminarProveedorViewController) {
                ((AdminEliminarProveedorViewController) controller).setDB(this.db);
            }
        } catch (SQLException e) {
            alerta.mostrarExcepcion("Error","Error al pasar la conexión a la nueva vista", e);
            e.printStackTrace();
        }

        Stage stage = (Stage) botoncito.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
    /**
     * Se saca el indice seleccionado de la tabla, este junto al arraylist "provedores" son
     * utilizados para el metodo "eliminarProveedor" del objeto "proveedorDAO".
     * Adivina que es lo que hace ese metodo...
     * 
     * Accion que ocurre al presionar el boton "Eliminar Proveedor"
     */
    @FXML
    private void eliminarProveedor(){
        int indice = tablaProveedores.getSelectionModel().getSelectedIndex();
        //System.out.println("Indice Seleccionado: " + indice);
        
        if(indice > -1){
            boolean resultado = alerta.mostrarConfirmacion("Eliminar", 
                    "¡Seguro que desea eliminar el siguiente proveedor?");
            if(resultado){
                try{
                proveedorDAO.eliminarProveedor(proveedores, indice);
                cargarProveedores();
                alerta.mostrarExito("Exito","El proveedor fue eliminado");
                } catch (SQLException e){
                    alerta.mostrarExcepcion("Error", "No se pudo eliminar el proveedor", e);
                }
            } else {
                alerta.mostrarExito("Exito", "Operación cancelada exitosamente");
            }
        } else {
            alerta.mostrarError("Error","Selecciona a un proveedor de la tabla");
        }
    }
    
    /**
     * Al hacer click en el boton "Agregar" abre el respectivo FXML
     * 
     * @throws IOException 
     */
    @FXML
    private void onAgregarViewButtonClicked() throws IOException{
        switchToView("src/main/resources/scenes/adminAgregarProveedor.fxml", 
                agregarViewButton);
        //System.out.println("Se presionó el boton agregar");
    }
    
    /**
     * Al hacer click en el boton "Editar" abre el respectivo FXML
     * 
     * @throws IOException 
     */
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
        switchToView("src/main/resources/scenes/adminEliminarProveedor.fxml", 
                eliminarViewButton);
        //System.out.println("Se presionó el boton eliminar");
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
            proveedores = proveedorDAO.obtenerTodosProveedores();
            ObservableList<ProveedorVO> datos = FXCollections.observableArrayList(proveedores);
            tablaProveedores.setItems(datos);
        } catch (SQLException e) {
            alerta.mostrarExcepcion("Error","Error al cargar proveedores", e);
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }
    
    /**
     * Establece la conexion con la bd transferida del controlador anterior en su metodo
     * "switchToView"
     * 
     * Aca se inicializa todo basicamente
     * 
     * @param db
     * @throws SQLException 
     */
    public void setDB(DatabaseConnection db) throws SQLException {
        this.db = db;
        this.proveedorDAO = new ProveedorDAOImp(db);
        configurarTabla();
        cargarProveedores();
    }

}