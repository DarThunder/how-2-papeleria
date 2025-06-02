package VO;

/**
 * Clase que representa un empleado en el sistema.
 *
 * <p>
 * Contiene atributos como rol, código de seguridad, nombre, contraseña, estado
 * e ID. Se utiliza para transportar datos relacionados con empleados entre las
 * capas de la aplicación.</p>
 *
 * @author dylxn999
 * Fecha: 28/05/2025
 */
public class EmpleadoVO {

    private String rol;
    private String codigoSeguridad;
    private String nombre;
    private String password;
    private String estado;
    private int id;

    /**
     * Constructor vacío.
     */
    public EmpleadoVO() {
    }

    /**
     * Constructor con parámetros para inicializar los atributos principales del
     * empleado.
     *
     * @param rol Rol del empleado.
     * @param codigoSeguridad Código de seguridad del empleado.
     * @param nombre Nombre del empleado.
     * @param password Contraseña del empleado.
     */
    public EmpleadoVO(String rol, String codigoSeguridad, String nombre, String password) {
        this.rol = rol;
        this.codigoSeguridad = codigoSeguridad;
        this.nombre = nombre;
        this.password = password;
    }

    /**
     * Obtiene el rol del empleado.
     *
     * @return Rol del empleado.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del empleado.
     *
     * @param rol Rol a asignar.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Obtiene el código de seguridad del empleado.
     *
     * @return Código de seguridad.
     */
    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    /**
     * Establece el código de seguridad del empleado.
     *
     * @param codigoSeguridad Código de seguridad a asignar.
     */
    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    /**
     * Obtiene el nombre del empleado.
     *
     * @return Nombre del empleado.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del empleado.
     *
     * @param nombre Nombre a asignar.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la contraseña del empleado.
     *
     * @return Contraseña del empleado.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del empleado.
     *
     * @param password Contraseña a asignar.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el estado del empleado.
     *
     * @return Estado actual (ej. "Activo", "Inactivo").
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del empleado.
     *
     * @param estado Estado a asignar.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el ID del empleado.
     *
     * @return ID del empleado.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del empleado.
     *
     * @param id ID a asignar.
     */
    public void setId(int id) {
        this.id = id;
    }
}
