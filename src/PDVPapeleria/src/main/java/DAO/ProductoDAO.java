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

    ProductoVO getProducto(int id);

    ResultSet getProductos() throws SQLException;

    boolean setProducto(ProductoVO product);
    
    boolean sellProducto(int id, int cantidad);

}
