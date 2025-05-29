package VO;

/**
 * Clase Value Object que representa el historial de cambios realizados a un proveedor.
 * 
 * Esta clase almacena información sobre las modificaciones realizadas en los datos
 * de un proveedor, manteniendo tanto los valores anteriores como los nuevos valores
 * después de la modificación, junto con la fecha y hora del cambio.
 * 
 * Se utiliza para mantener un registro de auditoría de todas las modificaciones
 * realizadas en el sistema de proveedores, permitiendo rastrear qué cambios
 * se realizaron, cuándo y qué valores tenían antes y después de la modificación.
 * 
 * @author goatt
 * @version 1.0
 * @since 2025
 */
public class HistorialProveedorVO {
    
    /** Identificador único del proveedor que fue modificado */
    private int idProveedor;
    
    /** Nombre del proveedor antes de la modificación */
    private String nombreAntes;
    
    /** Servicio del proveedor antes de la modificación */
    private String servicioAntes;
    
    /** Teléfono del proveedor antes de la modificación */
    private String telefonoAntes;
    
    /** Nombre del proveedor después de la modificación */
    private String nombreDesp;
    
    /** Servicio del proveedor después de la modificación */
    private String servicioDesp;
    
    /** Teléfono del proveedor después de la modificación */
    private String telefonoDesp;
    
    /** Fecha y hora en que se realizó la modificación */
    private String fechaHora;
    
    /**
     * Constructor que inicializa un nuevo registro de historial de cambios.
     * 
     * Crea una instancia del historial con todos los datos necesarios para
     * registrar una modificación completa de un proveedor, incluyendo los
     * valores antes y después del cambio.
     * 
     * @param id el identificador único del proveedor modificado
     * @param nombreA el nombre del proveedor antes de la modificación
     * @param servicioA el servicio del proveedor antes de la modificación
     * @param telefonoA el teléfono del proveedor antes de la modificación
     * @param nombreD el nombre del proveedor después de la modificación
     * @param servicioD el servicio del proveedor después de la modificación
     * @param telefonoD el teléfono del proveedor después de la modificación
     * @param fechaHorita la fecha y hora en que se realizó la modificación
     */
    public HistorialProveedorVO(int id, String nombreA, String servicioA, String telefonoA, 
            String nombreD, String servicioD, String telefonoD, String fechaHorita){
        
        this.idProveedor = id;
        this.nombreAntes = nombreA;
        this.servicioAntes = servicioA;
        this.telefonoAntes = telefonoA;
        this.nombreDesp = nombreD;
        this.servicioDesp = servicioD;
        this.telefonoDesp = telefonoD;
        this.fechaHora = fechaHorita;
    }

    /**
     * Obtiene el identificador del proveedor modificado.
     * 
     * @return el ID único del proveedor que fue modificado
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * Establece el identificador del proveedor modificado.
     * 
     * @param idProveedor el ID único del proveedor que fue modificado
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * Obtiene el nombre del proveedor antes de la modificación.
     * 
     * @return el nombre anterior del proveedor
     */
    public String getNombreAntes() {
        return nombreAntes;
    }

    /**
     * Establece el nombre del proveedor antes de la modificación.
     * 
     * @param nombreAntes el nombre anterior del proveedor
     */
    public void setNombreAntes(String nombreAntes) {
        this.nombreAntes = nombreAntes;
    }

    /**
     * Obtiene el servicio del proveedor antes de la modificación.
     * 
     * @return el servicio anterior del proveedor
     */
    public String getServicioAntes() {
        return servicioAntes;
    }

    /**
     * Establece el servicio del proveedor antes de la modificación.
     * 
     * @param servicioAntes el servicio anterior del proveedor
     */
    public void setServicioAntes(String servicioAntes) {
        this.servicioAntes = servicioAntes;
    }

    /**
     * Obtiene el teléfono del proveedor antes de la modificación.
     * 
     * @return el teléfono anterior del proveedor
     */
    public String getTelefonoAntes() {
        return telefonoAntes;
    }

    /**
     * Establece el teléfono del proveedor antes de la modificación.
     * 
     * @param telefonoAntes el teléfono anterior del proveedor
     */
    public void setTelefonoAntes(String telefonoAntes) {
        this.telefonoAntes = telefonoAntes;
    }

    /**
     * Obtiene el nombre del proveedor después de la modificación.
     * 
     * @return el nombre nuevo del proveedor
     */
    public String getNombreDesp() {
        return nombreDesp;
    }

    /**
     * Establece el nombre del proveedor después de la modificación.
     * 
     * @param nombreDesp el nombre nuevo del proveedor
     */
    public void setNombreDesp(String nombreDesp) {
        this.nombreDesp = nombreDesp;
    }

    /**
     * Obtiene el servicio del proveedor después de la modificación.
     * 
     * @return el servicio nuevo del proveedor
     */
    public String getServicioDesp() {
        return servicioDesp;
    }

    /**
     * Establece el servicio del proveedor después de la modificación.
     * 
     * @param servicioDesp el servicio nuevo del proveedor
     */
    public void setServicioDesp(String servicioDesp) {
        this.servicioDesp = servicioDesp;
    }

    /**
     * Obtiene el teléfono del proveedor después de la modificación.
     * 
     * @return el teléfono nuevo del proveedor
     */
    public String getTelefonoDesp() {
        return telefonoDesp;
    }

    /**
     * Establece el teléfono del proveedor después de la modificación.
     * 
     * @param telefonoDesp el teléfono nuevo del proveedor
     */
    public void setTelefonoDesp(String telefonoDesp) {
        this.telefonoDesp = telefonoDesp;
    }

    /**
     * Obtiene la fecha y hora de la modificación.
     * 
     * @return la fecha y hora en que se realizó el cambio
     */
    public String getFechaHora() {
        return fechaHora;
    }

    /**
     * Establece la fecha y hora de la modificación.
     * 
     * @param fechaHora la fecha y hora en que se realizó el cambio
     */
    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * Genera una representación textual del historial de cambios.
     * 
     * Crea una cadena formateada que muestra de manera clara y legible
     * todos los cambios realizados en el proveedor, incluyendo el ID,
     * los valores antes y después de la modificación, y la fecha/hora
     * del cambio.
     * 
     * El formato incluye:
     * - ID del proveedor modificado
     * - Valores anteriores (nombre, servicio, teléfono)
     * - Valores nuevos (nombre, servicio, teléfono)
     * - Fecha y hora de la modificación
     * 
     * @return una cadena formateada con toda la información del cambio
     */
    @Override
    public String toString() {
        return "Cambio en proveedor ID: " + idProveedor +
                "\nAntes => Nombre: " + nombreAntes + ", Servicio: " + servicioAntes + ", Teléfono: " + telefonoAntes +
                "\nDespués => Nombre: " + nombreDesp + ", Servicio: " + servicioDesp + ", Teléfono: " + telefonoDesp +
                "\nFecha y Hora: " + fechaHora + "\n";
    }
}