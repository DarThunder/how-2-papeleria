package DAOImp;

/**
 *
 * @author Jazmin
 */

import DAO.DatabaseConnection;
import DAO.VentaDAO;
import VO.HistorialVentaVO;
import VO.ProductoVO;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VentaDAOImp implements VentaDAO {

    private static VentaDAOImp instance;

    private VentaDAOImp() {}

    public static synchronized VentaDAOImp getInstance() {
        if (instance == null) {
            instance = new VentaDAOImp();
        }
        return instance;
    }

    @Override
    public int generarVenta(int idEmpleado, int total) {
        String query = "{CALL generarVenta(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, idEmpleado);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, total);
            stmt.registerOutParameter(4, Types.INTEGER);

            stmt.execute();
            return stmt.getInt(4);

        } catch (SQLException e) {
            Logger.getLogger(VentaDAOImp.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }
    }

    @Override
    public boolean rollbackVenta(int folio) {
        String query = "{CALL rollbackVenta(?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, folio);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            Logger.getLogger(VentaDAOImp.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    @Override
    public List<HistorialVentaVO> obtenerHistorialVentas() {
        return ejecutarBusquedaVentas("1 = 1", ps -> {});
    }

    @Override
    public List<HistorialVentaVO> buscarVentasPorEmpleado(String nombre) {
        return ejecutarBusquedaVentas("(e.nombre LIKE ? OR e.id = ?)", ps -> {
            ps.setString(1, "%" + nombre + "%");
            try {
                ps.setInt(2, Integer.parseInt(nombre));
            } catch (NumberFormatException e) {
                ps.setInt(2, -1);
            }
        });
    }

    @Override
    public List<HistorialVentaVO> buscarVentasPorFolio(int folio) {
        return ejecutarBusquedaVentas("v.folio = ?", ps -> ps.setInt(1, folio));
    }

    @Override
    public List<HistorialVentaVO> buscarVentasPorTotal(int total) {
        return ejecutarBusquedaVentas("v.total = ?", ps -> ps.setInt(1, total));
    }

    @Override
    public List<HistorialVentaVO> buscarVentasPorFecha(String fecha) {
        return ejecutarBusquedaVentas("DATE(v.fechaYHora) = ?", ps -> ps.setString(1, fecha));
    }

    private List<HistorialVentaVO> ejecutarBusquedaVentas(String whereClause, ParameterSetter setter) {
        List<HistorialVentaVO> resultados = new ArrayList<>();

        String sql = "SELECT v.folio, v.idEmpleado, v.fechaYHora, v.total, e.nombre AS nombreEmpleado " +
                     "FROM venta v JOIN Empleado e ON v.idEmpleado = e.id " +
                     "WHERE " + whereClause + " AND v.isDeleted = FALSE " +
                     "ORDER BY v.fechaYHora DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setter.setParameters(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int folio = rs.getInt("folio");

                    resultados.add(new HistorialVentaVO(
                    folio,
                    rs.getInt("idEmpleado"),
                    rs.getTimestamp("fechaYHora").toLocalDateTime(),
                    rs.getInt("total"),
                    rs.getString("nombreEmpleado")
                ));

                }
            }
        } catch (SQLException e) {
            Logger.getLogger(VentaDAOImp.class.getName()).log(Level.SEVERE, null, e);
        }
        return resultados;
    }

   
    @FunctionalInterface
    private interface ParameterSetter {
        void setParameters(PreparedStatement ps) throws SQLException;
    }
}