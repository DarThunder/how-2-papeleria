package DAOImp;

import DAO.DatabaseConnection;
import DAO.ProductoDAO;
import VO.ProductoVO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAOImp implements ProductoDAO {

    private static ProductoDAOImp instance;

    private ProductoDAOImp() {
    }

    public static synchronized ProductoDAOImp getInstance() {
        if (instance == null) {
            instance = new ProductoDAOImp();
        }
        return instance;
    }

    @Override
    public boolean reducirStock(int idProducto, int cantidad) throws SQLException {
        String query = "UPDATE producto SET stock = stock - ? WHERE idProducto = ? AND stock >= ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cantidad);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, cantidad);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet getProductos() throws SQLException {
        String query = "SELECT p.*, pr.nombre as nombreProveedor FROM producto p "
                + "LEFT JOIN provee pe ON p.idProducto = pe.idProducto "
                + "LEFT JOIN proveedor pr ON pe.idProveedor = pr.idProveedor "
                + "WHERE p.isDeleted = FALSE ORDER BY p.nombre";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = conn.prepareStatement(query);
        return statement.executeQuery();
    }

    @Override
    public ProductoVO getProductoById(int id) throws SQLException {
        String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, descripcion, categoria "
                + "FROM producto WHERE idProducto = ? AND isDeleted = FALSE";

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductoVO(
                            rs.getInt("idProducto"),
                            rs.getString("nombre"),
                            rs.getInt("precioDeCompra"),
                            rs.getInt("precioDeVenta"),
                            rs.getInt("stock"),
                            rs.getString("descripcion"),
                            rs.getString("categoria")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean agregarProductoConProveedor(ProductoVO producto, int idProveedor) throws SQLException {
        String sql = "{ CALL agregarProductoConProveedor(?, ?, ?, ?, ?, ?, ?) }";

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setInt(2, producto.getPrecioDeCompra());
            stmt.setInt(3, producto.getPrecioDeVenta());
            stmt.setInt(4, producto.getStock());
            stmt.setString(5, producto.getDescripcion());
            stmt.setString(6, producto.getCategoria());
            stmt.setInt(7, idProveedor);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizarProductoConProveedor(ProductoVO producto, int idProveedor) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar producto
            String sqlProducto = "UPDATE producto SET nombre = ?, precioDeCompra = ?, precioDeVenta = ?, "
                    + "stock = ?, descripcion = ?, categoria = ? WHERE idProducto = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlProducto)) {
                stmt.setString(1, producto.getNombre());
                stmt.setInt(2, producto.getPrecioDeCompra());
                stmt.setInt(3, producto.getPrecioDeVenta());
                stmt.setInt(4, producto.getStock());
                stmt.setString(5, producto.getDescripcion());
                stmt.setString(6, producto.getCategoria());
                stmt.setInt(7, producto.getIdProducto());

                int filas = stmt.executeUpdate();
                if (filas == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Actualizar relación con proveedor
            String sqlProvee = "UPDATE provee SET idProveedor = ? WHERE idProducto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlProvee)) {
                stmt.setInt(1, idProveedor);
                stmt.setInt(2, producto.getIdProducto());

                int filas = stmt.executeUpdate();

                // Si no existe relación, crear una nueva
                if (filas == 0) {
                    String sqlInsert = "INSERT INTO provee (idProducto, idProveedor) VALUES (?, ?)";
                    try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                        stmtInsert.setInt(1, producto.getIdProducto());
                        stmtInsert.setInt(2, idProveedor);
                        filas = stmtInsert.executeUpdate();
                    }
                }

                if (filas == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public boolean eliminarProducto(int idProducto) throws SQLException {
        String query = "UPDATE producto SET isDeleted = TRUE WHERE idProducto = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProducto);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet buscarProductos(String criterio) throws SQLException {
        String query = "SELECT p.*, pr.nombre as nombreProveedor FROM producto p "
                + "LEFT JOIN provee pe ON p.idProducto = pe.idProducto "
                + "LEFT JOIN proveedor pr ON pe.idProveedor = pr.idProveedor "
                + "WHERE p.isDeleted = FALSE AND (p.nombre LIKE ? OR p.categoria LIKE ?)";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        String pattern = "%" + criterio + "%";
        stmt.setString(1, pattern);
        stmt.setString(2, pattern);
        return stmt.executeQuery();
    }

    @Override
    public boolean actualizarProducto(ProductoVO producto) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
