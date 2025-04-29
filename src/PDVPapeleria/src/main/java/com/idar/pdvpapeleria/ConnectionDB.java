package com.idar.pdvpapeleria;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import DAO.DatabaseConnection;

/**
 *
 * @author laura
 */
public class ConnectionDB {

    private static DatabaseConnection db; // Instancia de la conexión a la base de datos

    /**
     * Método principal que inicia la aplicación y establece la conexión con la
     * base de datos.
     *
     * @param args Argumentos de la línea de comandos.
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws SQLException {
        String[] credentials = getDBCredentials();
        if (credentials == null) {
            System.out.println("Error: No se pudieron cargar las credenciales de la base de datos.");
            return;
        }
        try {
            System.out.println("peme");
            db = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            System.out.println("Url: " + credentials[0] + "\nUsuario: " + credentials[1]);
            System.out.println("Error: No se pudo conectar a la base de datos.");
            e.printStackTrace();
            return;
        }
        App.main(args);
    }

    /**
     * Carga las credenciales de la base de datos desde un archivo de
     * propiedades.
     *
     * @return Un array de Strings con la URL, usuario y contraseña de la base
     * de datos.
     */
    private static String[] getDBCredentials() {
        Properties properties = new Properties();
        String[] credentials = new String[3];

        try (FileInputStream fis = new FileInputStream("src/main/java/var/credentials.properties")) {
            properties.load(fis);
            credentials[0] = properties.getProperty("db.url");
            credentials[1] = properties.getProperty("db.user");
            credentials[2] = properties.getProperty("db.password");
        } catch (IOException e) {
            System.out.println("Error: No se pudo leer el archivo de credenciales.");
            e.printStackTrace();
            return null;
        }
        return credentials;
    }
}
