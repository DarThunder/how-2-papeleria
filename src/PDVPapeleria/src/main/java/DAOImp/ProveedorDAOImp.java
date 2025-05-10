package DAOImp;

import VO.ProveedorVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import DAO.DatabaseConnection;
import DAO.ProveedorDAO;

public class ProveedorDAOImp implements ProveedorDAO{
    private final DatabaseConnection db;

    public ProveedorDAOImp(DatabaseConnection db) {
        this.db = db;
    }
    
    /**
     * Obtiene todos los proveedores registrados en la db y los mete a un arraylist
     * 
     * @return
     * @throws SQLException 
     */
    @Override
    public List<ProveedorVO> obtenerTodosProveedores() throws SQLException {
        List<ProveedorVO> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedor";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
        conn = db.getConnection();
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();
            
            while (rs.next()) {
                ProveedorVO p = new ProveedorVO(
                    rs.getInt("idProveedor"),
                    rs.getString("nombre"),
                    rs.getString("servicio"),
                    rs.getString("telefono")
                );
                proveedores.add(p);
            } 
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        
        return proveedores;
    }

    /**
     * Agrega un proveedor usando procedure "agregarProveedor" desde la BD
     * 
     * @param proveedor
     * @return
     * @throws SQLException 
     */
    @Override
    public boolean agregarProveedor(ProveedorVO proveedor) throws SQLException {
        String sql = "{ CALL agregarProveedor(?, ?, ?) }";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, proveedor.getNombreProveedor());
            stmt.setString(2, proveedor.getServicioProveedor());
            stmt.setString(3, proveedor.getTelefonoProveedor());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Elimina un proveedor usando la id de este mediante un procedure llamado "eliminarProveedor"
     * 
     * @param proveedores
     * @param indiceSeleccionado
     * @return 
     */
    @Override
    public boolean eliminarProveedor(List<ProveedorVO> proveedores, 
            int indiceSeleccionado){
        
        String sql = "{ CALL eliminarProveedor(?) }";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, proveedores.get(indiceSeleccionado).getIdProveedor());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo eliminar al proveedor: " + e.getMessage());
            return false;
        }
    }

    
    @Override
    public boolean editarProveedor(int id, String nombre, String servicio, 
            String telefono) throws SQLException{
        
        String sql = "{ CALL editarProveedor(?, ?, ?, ?) }";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setString(2, nombre);
            stmt.setString(3, servicio);
            stmt.setString(4, telefono);
            
            return stmt.executeUpdate() > 0;
        }
    }
}