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

    private static DatabaseConnection instance;
    private final Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/pdvpapeleria";
    private final String username = "root"; // cambiele aqui a su usuario no sean cojudos
    private final String password = ""; // Aca tambien cambienle la contraseña

    private DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        System.out.println("Conexion establecida exitosamente.");
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
