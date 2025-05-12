/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOImp;

import DAO.DatabaseConnection;
import DAO.EmpleadoDAO;
import VO.EmpleadoVO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class EmpleadoDAOImp implements EmpleadoDAO {

    private static EmpleadoDAOImp instance;

    public static EmpleadoDAO getInstance() {
        if (instance == null) {
            instance = new EmpleadoDAOImp();
        }
        return instance;
    }

    @Override
    public boolean createUser(EmpleadoVO empleado) {
        if (existeNombreUsuario(empleado.getNombre()) || existeCodigoSeguridad(empleado.getCodigoSeguridad())) {
            return false;
        }
        String hashedPassword = generateHash(empleado.getPassword());
        String query = "{ CALL agregarEmpleado(?, ?, ?, ?) }";

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, empleado.getNombre());
                statement.setString(2, hashedPassword);
                statement.setString(3, empleado.getCodigoSeguridad());
                statement.setString(4, empleado.getRol());
                statement.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("Error al crear empleado: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la conexión: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isValidCredentials(String username, String password) throws SQLException {
        String query = "SELECT contraseña, estado FROM Empleado WHERE nombre = ?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("contraseña");
                String estado = rs.getString("estado");

                if (!"Activo".equalsIgnoreCase(estado)) {
                    return false;
                }
                return BCrypt.checkpw(password, storedHash);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean eliminarEmpleado(int idEmpleado) {
        String sql = "{CALL eliminarEmpleado(?)}";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, idEmpleado);
                stmt.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("Error al llamar al procedimiento: " + e.getMessage());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean existeEmpleadoPorId(int id) {
        try {
            String query = "SELECT COUNT(*) FROM Empleado WHERE id = ?";
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            } catch (SQLException e) {
                System.out.println("Error al verificar ID: " + e.getMessage());
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean existeNombreUsuario(String username) {
        try {
            String query = "SELECT COUNT(*) FROM Empleado WHERE nombre = ?";
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            } catch (SQLException e) {
                System.out.println("Error nombre: " + e.getMessage());
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean existeCodigoSeguridad(String codigoSeguridad) {
        try {
            String query = "SELECT COUNT(*) FROM Empleado WHERE codigoSeguridad = ?";
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, codigoSeguridad);
                ResultSet rs = statement.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            } catch (SQLException e) {
                System.out.println("Error codigo: " + e.getMessage());
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public String getRole(String username) {
        try {
            String query = "SELECT rol FROM Empleado WHERE nombre = ?";
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getString("rol");
                }
            }
            return "nil";
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "nil";
    }

    @Override
    public boolean verificarCodigoSeguridad(String username, String codigoSeguridad) {
        try {
            String query = "SELECT * FROM Empleado WHERE nombre = ? AND codigoSeguridad = ?";
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, username);
                statement.setString(2, codigoSeguridad);
                ResultSet rs = statement.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean cambiarContraseña(String nuevaContraseña, String codigoSeguridad) {
        try {
            String hash = generateHash(nuevaContraseña);
            String query = "UPDATE Empleado SET contraseña = ? WHERE codigoSeguridad = ?";
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, hash);
                statement.setString(2, codigoSeguridad);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Error al cambiar contraseña: " + e.getMessage());
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private String generateHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    @Override
    public List<EmpleadoVO> obtenerTodosEmpleados() throws SQLException {
        List<EmpleadoVO> empleados = new ArrayList<>();
        String query = "SELECT id, nombre, rol, codigoSeguridad, estado FROM Empleado";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (
                PreparedStatement statement = connection.prepareStatement(query); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                EmpleadoVO empleado = new EmpleadoVO();
                empleado.setId(rs.getInt("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setRol(rs.getString("rol"));
                empleado.setCodigoSeguridad(rs.getString("codigoSeguridad"));
                empleado.setEstado(rs.getString("estado"));
                empleados.add(empleado);
            }
        }

        return empleados;
    }
}
