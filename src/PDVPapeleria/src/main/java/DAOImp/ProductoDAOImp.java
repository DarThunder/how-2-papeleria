package DAOImp;

import DAO.DatabaseConnection;
import DAO.ProductoDAO;
import VO.ProductoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAOImp implements ProductoDAO {

    private static ProductoDAOImp instance;

    public static ProductoDAOImp getInstance() {
        if (instance == null) {
            instance = new ProductoDAOImp();
        }
        return instance;
    }

    @Override
    public ResultSet getProductos() throws SQLException {
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, descripcion, categoria FROM producto WHERE isDeleted = FALSE ORDER BY nombre";
        PreparedStatement statement = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
        return statement.executeQuery();
    }
    


    @Override
    public ProductoVO getProductoById(int id) throws SQLException {
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, categoria FROM producto WHERE idProducto = ? AND isDeleted = FALSE";
        try (PreparedStatement statement = DatabaseConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProductoVO(
                            resultSet.getInt("idProducto"),
                            resultSet.getString("nombre"),
                            resultSet.getInt("precioDeCompra"),
                            resultSet.getInt("precioDeVenta"),
                            resultSet.getInt("stock"),
                            resultSet.getString("categoria")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean agregarProducto(ProductoVO producto) throws SQLException {
        String query = "INSERT INTO producto (nombre, precioDeCompra, precioDeVenta, stock, descripcion, categoria) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, producto.getNombre());
            stmt.setInt(2, producto.getPrecioDeCompra());
            stmt.setInt(3, producto.getPrecioDeVenta());
            stmt.setInt(4, producto.getStock());
            stmt.setString(5, producto.getCategoria());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizarProducto(ProductoVO producto) throws SQLException {
        String query = "UPDATE producto SET nombre = ?, precioDeCompra = ?, precioDeVenta = ?, stock = ?, descripcion = ?, categoria = ? WHERE idProducto = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, producto.getNombre());
            stmt.setInt(2, producto.getPrecioDeCompra());
            stmt.setInt(3, producto.getPrecioDeVenta());
            stmt.setInt(4, producto.getStock());
            stmt.setString(5, producto.getCategoria());
            stmt.setInt(6, producto.getIdProducto());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminarProducto(int idProducto) throws SQLException {
        String query = "UPDATE producto SET isDeleted = TRUE WHERE idProducto = ?";
        try (PreparedStatement statement = DatabaseConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, idProducto);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet buscarProductos(String criterio) throws SQLException {
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, categoria FROM producto WHERE isDeleted = FALSE AND (nombre LIKE ? OR categoria LIKE ?)";
        PreparedStatement statement = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
        String searchPattern = "%" + criterio + "%";
        statement.setString(1, searchPattern);
        statement.setString(2, searchPattern);
        return statement.executeQuery();
    }

    @Override
    public boolean reducirStock(int idProducto, int cantidad) throws SQLException {
        String query = "UPDATE producto SET stock = stock - ? WHERE idProducto = ? AND stock >= ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, cantidad); // Validar que haya suficiente stock
            return stmt.executeUpdate() > 0;
        }
    }

    // Métodos adicionales específicos de la implementación (si son necesarios)
    public ProductoVO getProducto(int id) {
        // Este método puede ser eliminado ya que tenemos getProductoById
        try {
            return getProductoById(id);
        } catch (SQLException e) {
            System.out.println("Error al obtener el producto: " + e.getMessage());
            return null;
        }
    }
}