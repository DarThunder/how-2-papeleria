package VO;

import javafx.beans.property.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author jeshu
 */
public class ProveedorVO {
    private final IntegerProperty idProveedor; 
    private final StringProperty servicioProveedor; 
    private final StringProperty nombreProveedor; 
    private final StringProperty telefonoProveedor; 
    
    public ProveedorVO(int idProveedor, String nombreProveedor, 
            String servicioProveedor, String telefonoProveedor){
        this.idProveedor = new SimpleIntegerProperty(idProveedor);
        this.servicioProveedor = new SimpleStringProperty(servicioProveedor); 
        this.nombreProveedor = new SimpleStringProperty(nombreProveedor); 
        this.telefonoProveedor = new SimpleStringProperty(telefonoProveedor);
    }

    public IntegerProperty IdProveedorProperty() {
        return idProveedor;
    }

    public StringProperty ServicioProveedorProperty() {
        return servicioProveedor;
    }

    public StringProperty NombreProveedorProperty() {
        return nombreProveedor;
    }

    public StringProperty TelefonoProveedorProperty() {
        return telefonoProveedor;
    }

    public Integer getIdProveedor() {
        return idProveedor.get();
    }

    public String getServicioProveedor() {
        return servicioProveedor.get();
    }

    public String getNombreProveedor() {
        return nombreProveedor.get();
    }

    public String getTelefonoProveedor() {
        return telefonoProveedor.get();
    }
}
