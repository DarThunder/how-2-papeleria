package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que gestiona la conexión a la base de datos utilizando el patrón Singleton.
 * <p>
 * Esta clase se encarga de establecer y proporcionar acceso a una única instancia
 * de conexión a una base de datos MySQL. Es útil para evitar múltiples conexiones
 * innecesarias y mejorar la eficiencia de acceso a los datos.
 * </p>
 * 
 * 
 * @author dylxn999
 */
public class DatabaseConnection {

    /**
     * Instancia única de la clase DatabaseConnection (Singleton).
     */
    private static DatabaseConnection instance;

    /**
     * Objeto Connection activo para interactuar con la base de datos.
     */
    private final Connection connection;

    /**
     * URL de conexión a la base de datos.
     */
    private final String url = "jdbc:mysql://localhost:3306/pdvpapeleria";

    /**
     * Nombre de usuario de la base de datos.
     */
    private final String username = "root";

    /**
     * Contraseña del usuario de la base de datos.
     */
    private final String password = "";

    /**
     * Constructor privado que establece la conexión a la base de datos.
     * Se llama únicamente desde {@link #getInstance()}.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    private DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        System.out.println("Conexión establecida exitosamente.");
    }

    /**
     * Obtiene la única instancia de {@code DatabaseConnection}.
     * Si no existe, se crea una nueva instancia.
     *
     * @return instancia única de {@code DatabaseConnection}.
     * @throws SQLException si ocurre un error al crear la conexión.
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Devuelve una nueva conexión a la base de datos.
     * <p>
     * Aunque la clase sigue el patrón Singleton, este método crea una nueva conexión
     * cada vez que se invoca. Esto puede ser útil en ciertos contextos donde se 
     * requiere múltiples conexiones independientes, pero no es el comportamiento típico
     * de un Singleton.
     * </p>
     *
     * @return una nueva conexión {@link Connection} a la base de datos.
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
