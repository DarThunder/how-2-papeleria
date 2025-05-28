package VO;

/**
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

    public HistorialVentaVO(int folio, int idEmpleado, LocalDateTime fechaYHora, int total, 
                          String nombreEmpleado, List<ProductoVO> productos) {
        this.folio = folio;
        this.idEmpleado = idEmpleado;
        this.fechaYHora = fechaYHora;
        this.total = total;
        this.nombreEmpleado = nombreEmpleado;
        this.productos = productos;
    }

    public int getFolio() {
        return folio;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public LocalDateTime getFechaYHora() {
        return fechaYHora;
    }

    public int getTotal() {
        return total;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public List<ProductoVO> getProductos() {
        return productos;
    }

    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaYHora.format(formatter);
    }

    public String getTotalFormateado() {
        return "$" + total;
    }

    public String getDetallesConsolidados() {
        StringBuilder sb = new StringBuilder();
        sb.append("Folio: ").append(folio).append("\n")
          .append("Cajero: ").append(nombreEmpleado).append(" (ID: ").append(idEmpleado).append(")\n")
          .append("Fecha: ").append(getFechaFormateada()).append("\n")
          .append("Total: $").append(total).append("\n");

        return sb.toString();
    }
}