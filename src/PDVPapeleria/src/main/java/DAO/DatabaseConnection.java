
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author laura
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Instancia singleton de la DB
    private static DatabaseConnection instance;

    // Conexión a la DB
    private final Connection connection;

    // Llaves de acceso a la DB (a quien se le ocurrio hardcodearlo????)
    private final String url = "jdbc:mysql://localhost:3306/pdvpapeleria";
    private final String username = "root"; // cambiele aqui a su usuario no sean cojudos
    private final String password = ""; // Aca tambien cambienle la contraseña

    /**
     * Constructor privado que establece la conexión a la base de datos.
     * Implementa el patrón Singleton para garantizar una única instancia de
     * conexión.
     *
     * @throws SQLException si ocurre un error al establecer la conexión con la
     * base de datos
     */
    private DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        System.out.println("Conexion establecida exitosamente.");
    }

    /**
     * Obtiene la única instancia de DatabaseConnection (patrón Singleton). Si
     * no existe una instancia previa, crea una nueva; de lo contrario, retorna
     * la instancia existente.
     *
     * @return la instancia única de DatabaseConnection
     * @throws SQLException si ocurre un error al crear la conexión a la base de
     * datos
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Obtiene una nueva conexión a la base de datos. Nota: Este método crea una
     * nueva conexión cada vez que se llama, lo cual puede no ser la
     * implementación ideal para un patrón Singleton.
     *
     * @return un objeto Connection para interactuar con la base de datos
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
