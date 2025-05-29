/**
 * Implementación del DAO para la gestión de empleados, metodos implementados que se emplean en EmpleadoDao.
 * Contiene métodos para crear, eliminar, actualizar y consultar empleados en la base de datos.
 * Utiliza procedimientos almacenados y encriptación de contraseñas con BCrypt.
 * 
 * @author Jazmin
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

    /**
     * Obtiene la instancia única del DAO.
     * 
     * @return instancia de EmpleadoDAOImp
     */
    public static EmpleadoDAO getInstance() {
        if (instance == null) {
            instance = new EmpleadoDAOImp();
        }
        return instance;
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * 
     * @param empleado Objeto EmpleadoVO con los datos del nuevo empleado
     * @return true si se creó correctamente, false si ya existe el nombre de usuario o código
     */
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

    /**
     * Verifica si las credenciales son válidas.
     * 
     * @param username nombre del usuario
     * @param password contraseña sin encriptar
     * @return true si son válidas y el usuario está activo, false en caso contrario
     */
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

    /**
     * Elimina un empleado por ID usando procedimiento almacenado.
     * 
     * @param idEmpleado ID del empleado
     * @return true si fue eliminado correctamente
     */
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

    /**
     * Verifica si existe un empleado por su ID.
     * 
     * @param id ID del empleado
     * @return true si existe
     */
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

    /**
     * Verifica si existe un nombre de usuario.
     * 
     * @param username nombre de usuario
     * @return true si existe
     */
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

    /**
     * Verifica si existe un código de seguridad.
     * 
     * @param codigoSeguridad código de seguridad
     * @return true si existe
     */
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

    /**
     * Obtiene el rol de un usuario.
     * 
     * @param username nombre del usuario
     * @return rol del usuario o "nil" si no se encuentra
     */
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

    /**
     * Verifica que el nombre y el código de seguridad coincidan.
     * 
     * @param username nombre del usuario
     * @param codigoSeguridad código de seguridad
     * @return true si coinciden
     */
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

    /**
     * Cambia la contraseña del usuario usando su código de seguridad.
     * 
     * @param nuevaContraseña nueva contraseña sin encriptar
     * @param codigoSeguridad código de seguridad del usuario
     * @return true si se actualizó correctamente
     */
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

    /**
     * Genera el hash de una contraseña utilizando BCrypt.
     * 
     * @param password contraseña sin encriptar
     * @return contraseña encriptada
     */
    private String generateHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Obtiene una lista con todos los empleados, mostrando su id, nombre, codigo de seguridad, rol y estado en el sistema.
     * 
     * @return lista de EmpleadoVO
     */
    @Override
    public List<EmpleadoVO> obtenerTodosEmpleados() throws SQLException {
        List<EmpleadoVO> empleados = new ArrayList<>();
        String query = "SELECT id, nombre, rol, codigoSeguridad, estado FROM Empleado";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery()) {
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

    /**
     * Actualiza el nombre de un empleado por ID.
     * 
     * @param idEmpleado ID del empleado
     * @param nuevoNombre nuevo nombre
     * @return true si se actualizó correctamente
     */
    @Override
    public boolean actualizarNombreEmpleado(int idEmpleado, String nuevoNombre) {
        String sql = "{CALL actualizarNombreEmpleado(?, ?)}";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, idEmpleado);
                stmt.setString(2, nuevoNombre);
                stmt.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("Error al actualizar nombre: " + e.getMessage());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Actualiza el código de seguridad de un empleado.
     * 
     * @param idEmpleado ID del empleado
     * @param nuevoCodigoSeguridad nuevo código
     * @return true si se actualizó correctamente
     */
    @Override
    public boolean actualizarCodigoSeguridadEmpleado(int idEmpleado, String nuevoCodigoSeguridad) {
        String sql = "{CALL actualizarCodigoSeguridadEmpleado(?, ?)}";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, idEmpleado);
                stmt.setString(2, nuevoCodigoSeguridad);
                stmt.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("Error al actualizar código de seguridad: " + e.getMessage());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Actualiza el rol de un empleado.
     * 
     * @param idEmpleado ID del empleado
     * @param nuevoRol nuevo rol (Dueño, Administrador, Cajero)
     * @return true si se actualizó correctamente
     */
    @Override
    public boolean actualizarRolEmpleado(int idEmpleado, String nuevoRol) {
        String sql = "{CALL actualizarRolEmpleado(?, ?)}";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, idEmpleado);
                stmt.setString(2, nuevoRol);
                stmt.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("Error al actualizar rol: " + e.getMessage());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Actualiza el estado (Activo/Inactivo) de un empleado.
     * 
     * @param idEmpleado ID del empleado
     * @param nuevoEstado nuevo estado
     * @return true si se actualizó correctamente
     */
    @Override
    public boolean actualizarEstadoEmpleado(int idEmpleado, String nuevoEstado) {
        String sql = "{CALL actualizarEstadoEmpleado(?, ?)}";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, idEmpleado);
                stmt.setString(2, nuevoEstado);
                stmt.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("Error al actualizar estado: " + e.getMessage());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Obtiene un empleado dado su nombre y código de seguridad.
     * 
     * @param username nombre del usuario
     * @param codigoSeguridad código de seguridad
     * @return objeto EmpleadoVO si se encuentra, null si no
     */
    @Override
    public EmpleadoVO obtenerEmpleadoPorUsuarioYCodigo(String username, String codigoSeguridad) {
        String sql = "SELECT * FROM Empleado WHERE nombre = ? AND codigoSeguridad = ?";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, codigoSeguridad);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    EmpleadoVO empleado = new EmpleadoVO();
                    empleado.setId(rs.getInt("id"));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setCodigoSeguridad(rs.getString("codigoSeguridad"));
                    empleado.setRol(rs.getString("rol"));
                    empleado.setPassword(rs.getString("contraseña"));
                    empleado.setEstado(rs.getString("estado"));
                    return empleado;
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener empleado: " + e.getMessage());
                Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmpleadoDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
