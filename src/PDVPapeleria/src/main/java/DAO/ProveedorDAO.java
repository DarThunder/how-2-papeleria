package DAO;

import VO.ProductoVO;
import VO.ProveedorVO;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz DAO (Data Access Object) para la gestión de proveedores.
 * <p>
 * Esta interfaz define los métodos necesarios para realizar operaciones CRUD
 * (Create, Read, Update, Delete) sobre la entidad Proveedor en la base de datos.
 * También incluye métodos específicos para obtener información relacionada con
 * productos y proveedores.
 * </p>
 * 
 * @author goatt
 * @version 1.0
 * @since 2025
 */
public interface ProveedorDAO {

    /**
     * Obtiene el proveedor asociado a un producto específico.
     * 
     * @param idProducto el identificador único del producto
     * @return el objeto ProveedorVO correspondiente al producto, o null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta a la base de datos
     */
    ProveedorVO obtenerProveedorDeProducto(int idProducto) throws SQLException;

    /**
     * Obtiene todos los proveedores registrados en el sistema.
     * 
     * @return una lista de objetos ProveedorVO con todos los proveedores,
     *         o una lista vacía si no hay proveedores registrados
     * @throws SQLException si ocurre un error en la consulta a la base de datos
     */
    List<ProveedorVO> obtenerTodosProveedores() throws SQLException;
    
    /**
     * Obtiene un proveedor específico por su identificador único.
     * 
     * @param id el identificador único del proveedor
     * @return el objeto ProveedorVO correspondiente al ID, o null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta a la base de datos
     */
    ProveedorVO obtenerProveedorPorId(int id) throws SQLException;

    /**
     * Agrega un nuevo proveedor al sistema.
     * 
     * @param proveedor el objeto ProveedorVO con los datos del nuevo proveedor
     * @return true si el proveedor se agregó exitosamente, false en caso contrario
     * @throws SQLException si ocurre un error en la inserción a la base de datos
     */
    boolean agregarProveedor(ProveedorVO proveedor) throws SQLException;

    /**
     * Elimina un proveedor del sistema.
     * 
     * @param idProveedor el identificador único del proveedor a eliminar
     * @throws SQLException si ocurre un error en la eliminación de la base de datos
     */
    public void eliminarProveedor(int idProveedor) throws SQLException;

    /**
     * Edita la información de un proveedor existente.
     * 
     * @param id el identificador único del proveedor a editar
     * @param nombre el nuevo nombre del proveedor
     * @param servicio el nuevo servicio que ofrece el proveedor
     * @param telefono el nuevo número de teléfono del proveedor
     * @return true si la edición se realizó exitosamente, false en caso contrario
     * @throws SQLException si ocurre un error en la actualización de la base de datos
     */
    boolean editarProveedor(int id, String nombre, String servicio, String telefono) throws SQLException;
    
    /**
     * Obtiene una lista con los nombres de todos los proveedores.
     * <p>
     * Este método es útil para poblar listas desplegables o comboboxes
     * en la interfaz de usuario.
     * </p>
     * 
     * @return una lista de cadenas con los nombres de los proveedores,
     *         o una lista vacía si no hay proveedores registrados
     * @throws SQLException si ocurre un error en la consulta a la base de datos
     */
    public List<String> obtenerNombresProveedores() throws SQLException; 
    
    /**
     * Obtiene todos los productos asociados a un proveedor específico.
     * 
     * @param nombreProveedor el nombre del proveedor del cual se quieren obtener los productos
     * @return una lista de objetos ProductoVO asociados al proveedor,
     *         o una lista vacía si el proveedor no tiene productos asociados
     * @throws SQLException si ocurre un error en la consulta a la base de datos
     */
    public List<ProductoVO> obtenerProductosPorProveedor(String nombreProveedor) throws SQLException; 
}