package VO;

import javafx.beans.property.*;

/**
 * Clase que representa un proveedor con sus propiedades observables.
 * Esta clase es útil para aplicaciones JavaFX que requieren enlace de datos (data binding).
 * 
 * Contiene propiedades para el ID, nombre, servicio y teléfono del proveedor.
 * 
 * @author jeshu
 */
public class ProveedorVO {
    private final IntegerProperty idProveedor; 
    private final StringProperty servicioProveedor; 
    private final StringProperty nombreProveedor; 
    private final StringProperty telefonoProveedor; 

    /**
     * Constructor de la clase ProveedorVO.
     * 
     * @param idProveedor ID del proveedor.
     * @param nombreProveedor Nombre del proveedor.
     * @param servicioProveedor Servicio que ofrece el proveedor.
     * @param telefonoProveedor Teléfono del proveedor.
     */
    public ProveedorVO(int idProveedor, String nombreProveedor, 
            String servicioProveedor, String telefonoProveedor){
        this.idProveedor = new SimpleIntegerProperty(idProveedor);
        this.servicioProveedor = new SimpleStringProperty(servicioProveedor); 
        this.nombreProveedor = new SimpleStringProperty(nombreProveedor); 
        this.telefonoProveedor = new SimpleStringProperty(telefonoProveedor);
    }

    /**
     * Devuelve la propiedad observable del ID del proveedor.
     * 
     * @return Propiedad observable del ID del proveedor.
     */
    public IntegerProperty IdProveedorProperty() {
        return idProveedor;
    }

    /**
     * Devuelve la propiedad observable del servicio del proveedor.
     * 
     * @return Propiedad observable del servicio del proveedor.
     */
    public StringProperty ServicioProveedorProperty() {
        return servicioProveedor;
    }

    /**
     * Devuelve la propiedad observable del nombre del proveedor.
     * 
     * @return Propiedad observable del nombre del proveedor.
     */
    public StringProperty NombreProveedorProperty() {
        return nombreProveedor;
    }

    /**
     * Devuelve la propiedad observable del teléfono del proveedor.
     * 
     * @return Propiedad observable del teléfono del proveedor.
     */
    public StringProperty TelefonoProveedorProperty() {
        return telefonoProveedor;
    }

    /**
     * Obtiene el ID del proveedor.
     * 
     * @return ID del proveedor.
     */
    public Integer getIdProveedor() {
        return idProveedor.get();
    }

    /**
     * Obtiene el servicio que ofrece el proveedor.
     * 
     * @return Servicio del proveedor.
     */
    public String getServicioProveedor() {
        return servicioProveedor.get();
    }

    /**
     * Obtiene el nombre del proveedor.
     * 
     * @return Nombre del proveedor.
     */
    public String getNombreProveedor() {
        return nombreProveedor.get();
    }

    /**
     * Obtiene el teléfono del proveedor.
     * 
     * @return Teléfono del proveedor.
     */
    public String getTelefonoProveedor() {
        return telefonoProveedor.get();
    }
}
