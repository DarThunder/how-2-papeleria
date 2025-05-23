/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 *
 * @author laura
 */
public class AlertaPDV {

    public static void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void mostrarExito(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void mostrarExcepcion(String titulo, String mensaje, Exception ex) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(mensaje);

        String contenido = ex.getMessage();
        if (contenido == null || contenido.isBlank()) {
            contenido = ex.toString();
        }

        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje de confirmacion de "Si" o "No"
     *
     * @param titulo
     * @param mensaje
     * @return
     */
    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");
        alert.getButtonTypes().setAll(botonSi, botonNo);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == botonSi;
    }

    public static void mostrarHistorial(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
}
