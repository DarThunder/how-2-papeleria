/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOImp;

import DAO.DatabaseConnection;
import DAO.EmpleadoDAO;
import DAO.ProductoDAO;
import VO.ProductoVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author TheAl
 */
public class ProductoDAOImp implements ProductoDAO {
  private static ProductoDAOImp instance;
   
  public static ProductoDAOImp getInstance() {
        if (instance == null) {
            instance = new ProductoDAOImp();
        }
        return instance;
    }
  
    /**
     * Obtiene todos los productos no eliminados de la base de datos.
     *
     * @return Un ResultSet con los productos.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    @Override
    public ResultSet getProductos() throws SQLException {
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, categoria "
                + "FROM producto "
                + "WHERE isDeleted = FALSE "
                + "ORDER BY nombre";
        PreparedStatement statement = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
        return statement.executeQuery();
    }
  
}
