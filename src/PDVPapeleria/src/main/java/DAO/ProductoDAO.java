package DAO;

import VO.ProductoVO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interfaz que define las operaciones (Crear, Leer, Actualizar, Eliminar) 
 * para la gestión de productos en la base de datos, incluyendo operaciones específicas
 * relacionadas con proveedores y gestión de stock.
 * 
 * @author Alvaro, Jose Carlos
 */
public interface ProductoDAO {

    /**
     * Obtiene todos los productos disponibles en la base de datos.
     * 
     * @return ResultSet con el conjunto de resultados de todos los productos
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    ResultSet getProductos() throws SQLException;

    /**
     * Obtiene un producto específico según su ID.
     * 
     * @param id Identificador único del producto a buscar
     * @return Objeto ProductoVO con los datos del producto encontrado, o null si no existe
     */
    ProductoVO getProductoById(int id);

    /**
     * Agrega un nuevo producto a la base de datos asociándolo a un proveedor.
     * 
     * @param producto Objeto ProductoVO con los datos del nuevo producto
     * @param idProveedor Identificador del proveedor asociado al producto
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    boolean agregarProductoConProveedor(ProductoVO producto, int idProveedor) throws SQLException;

    /**
     * Actualiza los datos de un producto existente en la base de datos.
     * 
     * @param producto Objeto ProductoVO con los datos actualizados del producto
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    boolean actualizarProducto(ProductoVO producto) throws SQLException;

    /**
     * Actualiza los datos de un producto existente y su relación con un proveedor.
     * 
     * @param producto Objeto ProductoVO con los datos actualizados del producto
     * @param idProveedor Nuevo identificador del proveedor asociado al producto
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    boolean actualizarProductoConProveedor(ProductoVO producto, int idProveedor) throws SQLException;

    /**
     * Elimina un producto de la base de datos según su ID.
     * 
     * @param idProducto Identificador del producto a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    boolean eliminarProducto(int idProducto) throws SQLException;

    /**
     * Busca productos según un criterio de búsqueda.
     * 
     * @param criterio Texto para buscar coincidencias en nombre, precio u otros campos
     * @return ResultSet con el conjunto de resultados de la búsqueda
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    ResultSet buscarProductos(String criterio) throws SQLException;

    /**
     * Reduce el stock disponible de un producto en la cantidad especificada.
     * 
     * @param idProducto Identificador del producto a actualizar
     * @param cantidad Cantidad a reducir del stock actual
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    boolean reducirStock(int idProducto, int cantidad) throws SQLException;
}