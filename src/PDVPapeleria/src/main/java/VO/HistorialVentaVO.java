package VO;

/**
 * Clase que representa el historial de una venta realizada en el sistema.
 * Contiene información como el folio de la venta, el empleado que la realizó,
 * la fecha y hora, el total de la venta, el nombre del empleado y la lista de productos vendidos.
 * 
 * @author Jazmin
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialVentaVO {
    private int folio;
    private int idEmpleado;
    private LocalDateTime fechaYHora;
    private int total;
    private String nombreEmpleado;
    private List<ProductoVO> productos;

    /**
     * Constructor que inicializa los datos principales de una venta.
     *
     * @param folio           Número de folio de la venta.
     * @param idEmpleado      ID del empleado que realizó la venta.
     * @param fechaYHora      Fecha y hora en que se realizó la venta.
     * @param total           Total de la venta.
     * @param nombreEmpleado  Nombre del empleado que realizó la venta.
     */
    public HistorialVentaVO(int folio, int idEmpleado, LocalDateTime fechaYHora, int total, 
                            String nombreEmpleado) {
        this.folio = folio;
        this.idEmpleado = idEmpleado;
        this.fechaYHora = fechaYHora;
        this.total = total;
        this.nombreEmpleado = nombreEmpleado;
    }

    /**
     * Obtiene el folio de la venta.
     *
     * @return El número de folio.
     */
    public int getFolio() {
        return folio;
    }

    /**
     * Obtiene el ID del empleado que realizó la venta.
     *
     * @return ID del empleado.
     */
    public int getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Obtiene la fecha y hora de la venta.
     *
     * @return Fecha y hora en formato LocalDateTime.
     */
    public LocalDateTime getFechaYHora() {
        return fechaYHora;
    }

    /**
     * Obtiene el total de la venta.
     *
     * @return Total en formato entero.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Obtiene el nombre del empleado que realizó la venta.
     *
     * @return Nombre del empleado.
     */
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    /**
     * Obtiene la lista de productos vendidos en esta venta.
     *
     * @return Lista de objetos ProductoVO.
     */
    public List<ProductoVO> getProductos() {
        return productos;
    }

    /**
     * Devuelve la fecha y hora de la venta en formato "dd/MM/yyyy HH:mm".
     *
     * @return Fecha y hora formateadas como cadena.
     */
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaYHora.format(formatter);
    }

    /**
     * Devuelve el total de la venta con formato monetario.
     *
     * @return Total formateado como cadena con símbolo de peso.
     */
    public String getTotalFormateado() {
        return "$" + total;
    }

    /**
     * Devuelve una cadena con los detalles principales de la venta,
     * como folio, nombre del cajero, fecha y total.
     *
     * @return Detalles consolidados de la venta.
     */
    public String getDetallesConsolidados() {
        StringBuilder sb = new StringBuilder();
        sb.append("Folio: ").append(folio).append("\n")
          .append("Cajero: ").append(nombreEmpleado).append(" (ID: ").append(idEmpleado).append(")\n")
          .append("Fecha: ").append(getFechaFormateada()).append("\n")
          .append("Total: $").append(total).append("\n");

        return sb.toString();
    }
}
