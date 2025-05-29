package Vista;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 * Clase utilitaria para mostrar distintos tipos de alertas en la interfaz gráfica
 * del sistema de punto de venta utilizando JavaFX.
 * 
 * Proporciona métodos estáticos para mostrar mensajes de error, éxito, excepción,
 * confirmación y contenido extenso como historiales.
 * 
 * @author laura
 */
public class AlertaPDV {

    /**
     * Muestra una alerta de error con un mensaje específico.
     * 
     * @param titulo  El título de la alerta.
     * @param mensaje El contenido del mensaje de error.
     */
    public static void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de información indicando una operación exitosa.
     * 
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje informativo a mostrar.
     */
    public static void mostrarExito(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de error que incluye la información de una excepción.
     * 
     * @param titulo  El título de la alerta.
     * @param mensaje Mensaje personalizado para el encabezado.
     * @param ex      La excepción que se desea mostrar.
     */
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
     * Muestra una alerta de confirmación con opciones "Sí" y "No".
     * 
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje de la pregunta o confirmación.
     * @return {@code true} si el usuario selecciona "Sí", {@code false} si selecciona "No".
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

    /**
     * Muestra una alerta de información con un área de texto para visualizar contenido extenso.
     * Ideal para mostrar historiales o reportes.
     * 
     * @param titulo    El título de la alerta.
     * @param contenido El texto completo que se desea mostrar.
     */
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
