package DAOImp;

import DAO.DatabaseConnection;
import DAO.ProductoDAO;
import VO.ProductoVO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación de la interfaz {@link ProductoDAO} que proporciona operaciones
 * CRUD y de negocio sobre productos en la base de datos.
 */
public class ProductoDAOImp implements ProductoDAO {

    /**
     * Instancia única de la clase (patrón Singleton).
     */
    private static ProductoDAOImp instance;

    /**
     * Constructor privado para evitar la creación directa de instancias.
     */
    private ProductoDAOImp() {
    }

    /**
     * Obtiene la instancia única de {@code ProductoDAOImp}.
     *
     * @return instancia única de {@code ProductoDAOImp}.
     */
    public static synchronized ProductoDAOImp getInstance() {
        if (instance == null) {
            instance = new ProductoDAOImp();
        }
        return instance;
    }

    /**
     * Reduce el stock de un producto en la cantidad indicada, si hay stock suficiente.
     *
     * @param idProducto ID del producto.
     * @param cantidad cantidad a reducir.
     * @return {@code true} si la operación fue exitosa, {@code false} en caso contrario.
     */
    @Override
    public boolean reducirStock(int idProducto, int cantidad){
        try {
            String query = "UPDATE producto SET stock = stock - ? WHERE idProducto = ? AND stock >= ?";
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cantidad);
                stmt.setInt(2, idProducto);
                stmt.setInt(3, cantidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Recupera todos los productos no eliminados junto con el nombre del proveedor asociado.
     *
     * @return {@code ResultSet} con los productos encontrados.
     * @throws SQLException si ocurre un error durante la consulta.
     */
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

    /**
     * Obtiene un producto específico por su ID.
     *
     * @param id ID del producto a buscar.
     * @return Objeto {@code ProductoVO} si se encuentra, {@code null} si no existe o hay error.
     */
    @Override
    public ProductoVO getProductoById(int id) {
        try {
            String query = "SELECT idProducto, nombre, precioDeCompra, precioDeVenta, stock, descripcion, categoria "
                    + "FROM producto WHERE idProducto = ? AND isDeleted = FALSE";
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
        } catch (SQLException e) {
            System.out.println("Error al obtener el producto: " + e.getMessage());
            return null;
        }
    }

    /**
     * Agrega un nuevo producto y lo asocia con un proveedor.
     *
     * @param producto objeto {@code ProductoVO} con los datos del producto.
     * @param idProveedor ID del proveedor a asociar.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
     */
    @Override
    public boolean agregarProductoConProveedor(ProductoVO producto, int idProveedor){
        try {
            String sql = "{ CALL agregarProductoConProveedor(?, ?, ?, ?, ?, ?, ?) }";
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, producto.getNombre());
                stmt.setInt(2, producto.getPrecioDeCompra());
                stmt.setInt(3, producto.getPrecioDeVenta());
                stmt.setInt(4, producto.getStock());
                stmt.setString(5, producto.getDescripcion());
                stmt.setString(6, producto.getCategoria());
                stmt.setInt(7, idProveedor);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Actualiza un producto existente y su relación con el proveedor.
     *
     * @param producto objeto {@code ProductoVO} con los datos actualizados.
     * @param idProveedor ID del nuevo proveedor.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    @Override
    public boolean actualizarProductoConProveedor(ProductoVO producto, int idProveedor) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Actualizar producto
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

            // Actualizar relación con proveedor
            String sqlProvee = "UPDATE provee SET idProveedor = ? WHERE idProducto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlProvee)) {
                stmt.setInt(1, idProveedor);
                stmt.setInt(2, producto.getIdProducto());
                int filas = stmt.executeUpdate();

                // Si no existe relación, insertarla
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
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Marca un producto como eliminado lógicamente.
     *
     * @param idProducto ID del producto a eliminar.
     * @return {@code true} si se marcó como eliminado, {@code false} si falló.
     * @throws SQLException si ocurre un error al ejecutar la operación.
     */
    @Override
    public boolean eliminarProducto(int idProducto) throws SQLException {
        String query = "UPDATE producto SET isDeleted = TRUE WHERE idProducto = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idProducto);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Busca productos según un criterio que coincida con el nombre o la categoría.
     *
     * @param criterio texto de búsqueda.
     * @return {@code ResultSet} con los productos que coincidan.
     */
    @Override
    public ResultSet buscarProductos(String criterio){
        try {
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
        } catch (SQLException ex) {
            return null;
        }
    }

    /**
     * Método no implementado para actualizar un producto sin proveedor.
     *
     * @param producto objeto {@code ProductoVO} a actualizar.
     * @return sin implementación.
     * @throws SQLException siempre que se invoque.
     */
    @Override
    public boolean actualizarProducto(ProductoVO producto) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}