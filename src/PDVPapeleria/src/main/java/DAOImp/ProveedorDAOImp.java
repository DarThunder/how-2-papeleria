package DAOImp;

import DAO.DatabaseConnection;
import DAO.ProveedorDAO;
import VO.ProductoVO;
import VO.ProveedorVO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImp implements ProveedorDAO {

    private final DatabaseConnection db;

    public ProveedorDAOImp(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public ProveedorVO obtenerProveedorDeProducto(int idProducto) throws SQLException {
        String sql = "SELECT p.* FROM proveedor p JOIN provee pr ON p.idProveedor = pr.idProveedor "
                + "WHERE pr.idProducto = ? AND p.isDeleted = false";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProveedorVO(
                            rs.getInt("idProveedor"),
                            rs.getString("nombre"),
                            rs.getString("servicio"),
                            rs.getString("telefono")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<ProveedorVO> obtenerTodosProveedores() throws SQLException {
        List<ProveedorVO> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedor WHERE isDeleted = false";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProveedorVO p = new ProveedorVO(
                        rs.getInt("idProveedor"),
                        rs.getString("nombre"),
                        rs.getString("servicio"),
                        rs.getString("telefono")
                );
                proveedores.add(p);
            }
        }
        return proveedores;
    }

    @Override
    public ProveedorVO obtenerProveedorPorId(int id) throws SQLException {
        String query = "SELECT * FROM proveedor WHERE idProveedor = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ProveedorVO(
                        rs.getInt("idProveedor"),
                        rs.getString("nombre"),
                        rs.getString("servicio"),
                        rs.getString("telefono")
                );
            }
        }
        return null;
    }

    @Override
    public boolean agregarProveedor(ProveedorVO proveedor) throws SQLException {
        String sql = "{ CALL agregarProveedor(?, ?, ?) }";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proveedor.getNombreProveedor());
            stmt.setString(2, proveedor.getServicioProveedor());
            stmt.setString(3, proveedor.getTelefonoProveedor());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public void eliminarProveedor(int idProveedor) throws SQLException {
    String sql = "{ CALL eliminarProveedor(?)}";
    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setInt(1, idProveedor);
        ps.executeUpdate();
    }
}

    @Override
    public boolean editarProveedor(int id, String nombre, String servicio, String telefono) throws SQLException {
        String sql = "{ CALL editarProveedor(?, ?, ?, ?) }";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, nombre);
            stmt.setString(3, servicio);
            stmt.setString(4, telefono);

            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Método para cargar los nombres de los proveedores
     * @return Lista de nombres
     * @throws SQLException 
     */
    @Override
    public List<String> obtenerNombresProveedores() throws SQLException {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT nombre FROM proveedor WHERE isDeleted = false ORDER BY nombre";
        
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        }
        return nombres;
    }
    
    /**
     * Método para cargar los productos por proveedor 
     * @param nombreProveedor El nombre del proveedor
     * @return La lista de productos del proveedor 
     * @throws SQLException 
     */
    @Override
     public List<ProductoVO> obtenerProductosPorProveedor(String nombreProveedor) throws SQLException {
        List<ProductoVO> productos = new ArrayList<>();
        
        try (Connection conn = db.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL ObtenerProductosPorProveedor(?)}")) {
            
            cstmt.setString(1, nombreProveedor);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                ProductoVO pp = new ProductoVO(
                    rs.getInt("idProducto"),
                    rs.getString("nombre_producto"),
                    rs.getInt("precioDeCompra"),
                    rs.getInt("precioDeVenta"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                );
                productos.add(pp);
            }
        }
        return productos;
    }
}
