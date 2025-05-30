package DAOImp;

/**
 *
 * @author Jazmin
 */
import DAO.DatabaseConnection;
import DAO.VentaDAO;
import VO.HistorialVentaVO;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación de la interfaz VentaDAO que maneja las operaciones de acceso a
 * datos relacionadas con ventas en la base de datos.
 *
 * Esta clase implementa el patrón Singleton para garantizar una única instancia
 * y utiliza stored procedures y consultas SQL para interactuar con la base de
 * datos.
 *
 * @author DarThunder
 * @version 1.0
 * @since 1.0
 */
public class VentaDAOImp implements VentaDAO {

    /**
     * Instancia única de la clase (patrón Singleton)
     */
    private static VentaDAOImp instance;

    /**
     * Constructor privado para implementar el patrón Singleton. Previene la
     * creación de instancias desde fuera de la clase.
     */
    private VentaDAOImp() {
    }

    /**
     * Obtiene la única instancia de VentaDAOImp (patrón Singleton). Este método
     * es thread-safe utilizando sincronización.
     *
     * @return la instancia única de VentaDAOImp
     */
    public static synchronized VentaDAOImp getInstance() {
        if (instance == null) {
            instance = new VentaDAOImp();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     *
     * Utiliza un stored procedure llamado 'generarVenta' para registrar una
     * nueva venta en la base de datos con la fecha y hora actual del sistema.
     *
     * @param idEmpleado el identificador único del empleado que realiza la
     * venta
     * @param total el monto total de la venta
     * @return el folio generado para la venta, o -1 si ocurre un error
     */
    @Override
    public int generarVenta(int idEmpleado, int total) {
        String query = "{CALL generarVenta(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); CallableStatement stmt = conn.prepareCall(query)) {

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

    /**
     * {@inheritDoc}
     *
     * Utiliza un stored procedure llamado 'rollbackVenta' para revertir una
     * venta. Esta operación marca la venta como eliminada en lugar de borrarla
     * físicamente.
     *
     * @param folio el número de folio único de la venta a revertir
     * @return true si la venta se revirtió exitosamente, false en caso
     * contrario
     */
    @Override
    public boolean rollbackVenta(int folio) {
        String query = "{CALL rollbackVenta(?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, folio);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            Logger.getLogger(VentaDAOImp.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Obtiene todas las ventas no eliminadas del sistema, ordenadas por fecha
     * descendente.
     *
     * @return una lista de objetos HistorialVentaVO con todas las ventas
     * activas, o una lista vacía si no hay ventas registradas
     */
    @Override
    public List<HistorialVentaVO> obtenerHistorialVentas() {
        return ejecutarBusquedaVentas("1 = 1", ps -> {
        });
    }

    /**
     * {@inheritDoc}
     *
     * Busca ventas por nombre de empleado utilizando coincidencia parcial
     * (LIKE). También intenta buscar por ID de empleado si el parámetro nombre
     * es numérico.
     *
     * @param nombre el nombre del empleado o su ID como cadena
     * @return una lista de objetos HistorialVentaVO con las ventas del
     * empleado, o una lista vacía si no se encuentran coincidencias
     */
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

    /**
     * {@inheritDoc}
     *
     * Busca una venta específica por su número de folio único.
     *
     * @param folio el número de folio de la venta a buscar
     * @return una lista con el objeto HistorialVentaVO correspondiente al
     * folio, o una lista vacía si no se encuentra la venta
     */
    @Override
    public List<HistorialVentaVO> buscarVentasPorFolio(int folio) {
        return ejecutarBusquedaVentas("v.folio = ?", ps -> ps.setInt(1, folio));
    }

    /**
     * {@inheritDoc}
     *
     * Busca ventas que coincidan exactamente con un monto total específico.
     *
     * @param total el monto total de las ventas a buscar
     * @return una lista de objetos HistorialVentaVO con ventas del monto
     * especificado, o una lista vacía si no se encuentran coincidencias
     */
    @Override
    public List<HistorialVentaVO> buscarVentasPorTotal(int total) {
        return ejecutarBusquedaVentas("v.total = ?", ps -> ps.setInt(1, total));
    }

    /**
     * {@inheritDoc}
     *
     * Busca ventas realizadas en una fecha específica utilizando la función
     * DATE() para comparar solo la parte de fecha ignorando la hora.
     *
     * @param fecha la fecha de las ventas a buscar en formato "YYYY-MM-DD"
     * @return una lista de objetos HistorialVentaVO con las ventas de la fecha
     * especificada, o una lista vacía si no se encuentran ventas en esa fecha
     */
    @Override
    public List<HistorialVentaVO> buscarVentasPorFecha(String fecha) {
        return ejecutarBusquedaVentas("DATE(v.fechaYHora) = ?", ps -> ps.setString(1, fecha));
    }

    /**
     * Método privado utilitario que ejecuta consultas de búsqueda de ventas con
     * diferentes condiciones WHERE y parámetros.
     *
     * Este método centraliza la lógica de consulta para evitar duplicación de
     * código y facilita el mantenimiento. Realiza un JOIN entre las tablas
     * venta y Empleado para obtener información completa de cada venta.
     *
     * @param whereClause la cláusula WHERE específica para la consulta
     * @param setter interfaz funcional para establecer los parámetros de la
     * consulta
     * @return una lista de objetos HistorialVentaVO que cumplen con los
     * criterios, ordenados por fecha descendente, o una lista vacía si hay
     * errores
     */
    private List<HistorialVentaVO> ejecutarBusquedaVentas(String whereClause, ParameterSetter setter) {
        List<HistorialVentaVO> resultados = new ArrayList<>();

        String sql = "SELECT v.folio, v.idEmpleado, v.fechaYHora, v.total, e.nombre AS nombreEmpleado "
                + "FROM venta v JOIN Empleado e ON v.idEmpleado = e.id "
                + "WHERE " + whereClause + " AND v.isDeleted = FALSE "
                + "ORDER BY v.fechaYHora DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

    /**
     * Interfaz funcional que define un contrato para establecer parámetros en
     * un PreparedStatement.
     *
     * Esta interfaz permite pasar diferentes lógicas de configuración de
     * parámetros al método ejecutarBusquedaVentas, promoviendo la reutilización
     * de código.
     */
    @FunctionalInterface
    private interface ParameterSetter {

        /**
         * Establece los parámetros necesarios en un PreparedStatement.
         *
         * @param ps el PreparedStatement donde se establecerán los parámetros
         * @throws SQLException si ocurre un error al establecer los parámetros
         */
        void setParameters(PreparedStatement ps) throws SQLException;
    }
}
