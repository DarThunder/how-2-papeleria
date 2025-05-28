package DAO;

import VO.ProductoVO;
import java.sql.ResultSet;
import java.sql.SQLException;


public interface ProductoDAO {

    ResultSet getProductos() throws SQLException;

    ProductoVO getProductoById(int id);

    boolean agregarProductoConProveedor(ProductoVO producto, int idProveedor) throws SQLException;

    boolean actualizarProducto(ProductoVO producto) throws SQLException;

    boolean actualizarProductoConProveedor(ProductoVO producto, int idProveedor) throws SQLException;

    boolean eliminarProducto(int idProducto) throws SQLException;

    ResultSet buscarProductos(String criterio) throws SQLException;

    boolean reducirStock(int idProducto, int cantidad) throws SQLException;
}
