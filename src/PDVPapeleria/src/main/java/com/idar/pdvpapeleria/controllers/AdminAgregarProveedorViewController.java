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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class AdminAgregarProveedorViewController implements Initializable {

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
    private Button agregarProveedorButton;
    
    @FXML
    private Button resetearCamposButton;
    
    public static final String REGEX_TELEFONO = "^[0-9]{10}$";

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

        Stage stage = (Stage) botoncito.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
    /**
     * Agarra el contenido de los textFields, valida que no esten vacios o incorrectos
     * y si todo esta bien, manda a llamar al metodo "agregarProveedor" del objeto
     * "proveedorDAO"
     * Adivina que hace el metodo ese..
     * 
     * Accion que ocurre al presionar el boton "Crear Nuevo Proveedor"
     */
    @FXML
    private void agregarProveedor(){
        String Nombre = TF_Nombre.getText();
        String Servicio = TF_Servicio.getText();
        String Telefono = TF_Telefono.getText();
        
        if(validarCampos(Nombre, Servicio, Telefono)){
            ProveedorVO p = new ProveedorVO(0, Nombre, Servicio, Telefono);
            try {
                proveedorDAO.agregarProveedor(p);
                cargarProveedores();
                mostrarMensajeExito("Se agregó correctamente al proveedor");
                resetearCampos();
            } catch (SQLException e){
                error(e.getMessage());
                System.err.println("Error al agregar proveedor: " + e.getMessage());
            }   
        }
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
            proveedores = proveedorDAO.obtenerTodosProveedores();
            ObservableList<ProveedorVO> datos = FXCollections.observableArrayList(proveedores);
            tablaProveedores.setItems(datos);
        } catch (SQLException e) {
            error(e.getMessage());
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
    
    /**
     * Muestra un mensaje en la UI
     * 
     * @param mensaje 
     */
    private void mostrarMensajeExito(String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Operación exitosa");
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
    }
    
    /**
     * Valida que el telefono tenga el formato de uno real: 10 digitos y que no 
     * tenga ninguna letra.
     * True si cumple, false si no
     * 
     * @param telefono
     * @return 
     */
    @FXML
    public boolean validarTelefono(String telefono){
        return telefono.matches(REGEX_TELEFONO);
    }
    
    /**
     * Valida los campos
     * 
     * @param Nombre
     * @param Servicio
     * @param Telefono
     * @return 
     */
    @FXML
    public boolean validarCampos(String Nombre, String Servicio, String Telefono){
        boolean nombreVacio = Nombre.isEmpty();
        boolean servicioVacio = Servicio.isEmpty();
        boolean telefonoVacio = Telefono.isEmpty();
        
        if(nombreVacio && servicioVacio && telefonoVacio){
            error("Campos vacios");
            return false;
        }
        if (nombreVacio){
            error("Campo Nombre vacio");
            return false;
        }
        if (servicioVacio){
            error("Campo Servicio vacio");
            return false;
        }
        if (telefonoVacio){
            error("Campo Telefono vacio");
            return false;
        }
        if (!validarTelefono(Telefono)){
            error("El telefono no es valido");
            return false;
        }
        return true;
    }
    
    /**
     * Pone los textFields vacios
     */
    @FXML
    public void resetearCampos(){
        TF_Nombre.setText("");
        TF_Servicio.setText("");
        TF_Telefono.setText("");
    }
}