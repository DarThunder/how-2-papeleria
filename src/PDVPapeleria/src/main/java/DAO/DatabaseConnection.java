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
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/pdvpapeleria";
    private final String username = "dard";
    private final String password = "";

    private DatabaseConnection() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Conexión establecida exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (!instance.isValid()) {
            System.err.println("Conexión inválida, intentando reconectar...");
            instance.connect();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isValid() {
        return connection != null; // timeout 2s
    }
}
