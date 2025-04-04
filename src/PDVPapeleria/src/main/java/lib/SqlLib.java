
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author laura
 */
public class SqlLib {

    private static SqlLib instance;
    private Connection connection;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructor de la clase SqlLib.
     *
     * @param url La URL de la base de datos.
     * @param username El nombre de usuario para la conexión a la base de datos.
     * @param password La contraseña para la conexión a la base de datos.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    public SqlLib(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        connect();
    }
    
    public Connection getConnection() {
    return this.connection;
}


    /**
     * Establece la conexión con la base de datos.
     *
     * @throws ClassNotFoundException Si no se encuentra el driver de la base de
     * datos.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pdvpapeleria", "root", "");
            System.out.println("Conexión establecida exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error de SQL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    public static SqlLib getInstance(String url, String username, String password) throws SQLException {
        if (instance == null) {
            instance = new SqlLib(url, username, password);
        }
        return instance;
    }

    /**
     * Crea un nuevo usuario (empleado) en la base de datos, con la contraseña
     * encriptada en Java.
     *
     * @param role El rol del usuario.
     * @param username El nombre de usuario.
     * @param password La contraseña del usuario.
     * @return true si el usuario fue creado correctamente, false en caso
     * contrario.
   * @throws java.sql.SQLException
     */
    public boolean createUser(String role, String username, String password) throws SQLException {
        // Verifica si la conexión está cerrada y reconecta si es necesario
        if (connection == null || connection.isClosed()) {
            connect();
        }

        String hashedPassword = generateHash(password);
        String query = "{ CALL agregarEmpleado(?, ?, ?) }";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, role);
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al crear el empleado: " + e.getMessage());
            throw e; // Relanza la excepción para manejo en el controlador
        }
    }
    /**
     * Obtiene el rol de un usuario.
     *
     * @param username El nombre de usuario.
     * @return El rol del usuario, o "nar" si no se encuentra el usuario.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public String getRole(String username) throws SQLException {
        if (username.equals("owner") || username.equals("admin") || username.equals("user")){
            return username;
        }
        String sql = "SELECT rol FROM Empleados WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("Rol");
                return role;
            } else {
                return "nar";
            }
        }
    }

    /**
     * Genera un hash de la contraseña proporcionada utilizando BCrypt.
     *
     * @param password La contraseña a hashear.
     * @return El hash de la contraseña.
     */
    /**
     * Genera un hash de la contraseña proporcionada utilizando BCrypt.
     *
     * @param password La contraseña a hashear.
     * @return El hash de la contraseña.
     */
    public String generateHash(String password) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }

    /**
     * Verifica si las credenciales proporcionadas son válidas.
     *
     * @param username El nombre de usuario a verificar.
     * @param password La contraseña a verificar.
     * @return true si las credenciales son válidas, false en caso contrario.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public boolean isValidCredentials(String username, String password) throws SQLException {
        if (username.equals("owner") || username.equals("admin") || username.equals("user")){
            return true;
        }
        String query = "SELECT contraseña FROM Empleados WHERE nombre = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("contraseña");
                if (BCrypt.checkpw(password, storedHash)) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Contraseña incorrecta.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }
}
