/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author laura
 */
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {

    private final Connection connection;

    public ProductoDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Obtiene todos los productos no eliminados de la base de datos.
     *
     * @return Un ResultSet con los productos.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public ResultSet getProductos() throws SQLException {
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, categoria "
                + "FROM producto "
                + "WHERE is_deleted = FALSE "
                + "ORDER BY nombre";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }
}
