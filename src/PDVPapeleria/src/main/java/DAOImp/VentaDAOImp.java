/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOImp;

import DAO.DatabaseConnection;
import DAO.VentaDAO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dard
 */
public class VentaDAOImp implements VentaDAO {

    private static VentaDAOImp instance;

    private VentaDAOImp() {
    }

    public static synchronized VentaDAOImp getInstance() {
        if (instance == null) {
            instance = new VentaDAOImp();
        }
        return instance;
    }

    @Override
    public int generarVenta(int idEmpleado, int total) {
    try {
        String query = "{CALL generarVenta(?, ?, ?, ?)}";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, idEmpleado);
            stmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, total);
            stmt.registerOutParameter(4, java.sql.Types.INTEGER);

            stmt.execute();

            return stmt.getInt(4);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return -1;
    }
}

    @Override
    public boolean rollbackVenta(int folio) {
        try {
            String query = "{CALL generarVenta(?, ?, ?)}";
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, folio);
                stmt.execute();
                return true;

            }
        } catch (SQLException e) {
            return false;
        }
    }

}
