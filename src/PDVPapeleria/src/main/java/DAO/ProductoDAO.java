/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Alvaro
 */
package DAO;

import VO.ProductoVO;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ProductoDAO {

    ResultSet getProductos() throws SQLException;

    ProductoVO getProductoById(int id) throws SQLException;

    boolean agregarProducto(ProductoVO producto) throws SQLException;

    boolean actualizarProducto(ProductoVO producto) throws SQLException;

    boolean eliminarProducto(int idProducto) throws SQLException;

    ResultSet buscarProductos(String criterio) throws SQLException;

    boolean reducirStock(int idProducto, int cantidad) throws SQLException;
}