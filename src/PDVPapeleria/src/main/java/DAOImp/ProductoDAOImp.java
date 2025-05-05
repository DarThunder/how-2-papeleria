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
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, categoria FROM producto WHERE isDeleted = FALSE ORDER BY nombre";
        PreparedStatement statement = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
        return statement.executeQuery();
    }

    @Override
    public ProductoVO getProducto(int id) {
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, categoria FROM producto WHERE isDeleted = FALSE AND stock > 0 AND idProducto = ?";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        ProductoVO producto = new ProductoVO(resultSet.getInt("idProducto"), resultSet.getString("nombre"), resultSet.getInt("precioDeCompra"), resultSet.getInt("precioDeVenta"), resultSet.getInt("stock"), resultSet.getString("categoria"));
                        return producto;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el producto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean setProducto(ProductoVO product) {
        String query = "{ CALL agregarProducto(?, ?, ?, ?, ?) }";

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, product.getNombre());
                statement.setInt(2, product.getPrecioDeCompra());
                statement.setInt(3, product.getPrecioDeVenta());
                statement.setInt(4, product.getStock());
                statement.setString(5, product.getCategoria());
                statement.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("Error al crear producto: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la conexión: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean sellProducto(int id, int cantidad) {
        String query = "UPDATE producto SET stock = stock - ? WHERE idProducto = ?";

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, cantidad);
                statement.setInt(2, id);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al vender el producto: " + e.getMessage());
        }

        return false;
    }

}
