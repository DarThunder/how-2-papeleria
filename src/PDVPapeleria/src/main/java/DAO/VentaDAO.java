package DAO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import VO.HistorialVentaVO;
import java.util.List;

/**
 *
 *
 * Interfaz que define las operaciones de acceso a datos relacionadas con las ventas.
 * @author dard
 */
public interface VentaDAO {

    /**
     * Genera una nueva venta en el sistema.
     *
     * @param idEmpleado el identificador único del empleado que realiza la
     * venta
     * @param total el monto total de la venta
     * @return el folio generado para la venta, o -1 si ocurre un error
     */
    int generarVenta(int idEmpleado, int total);

    /**
     * Revierte una venta previamente registrada utilizando su folio. Esta
     * operación cancela la venta y puede incluir la restauración del
     * inventario.
     *
     * @param folio el número de folio único de la venta a revertir
     * @return true si la venta se revirtió exitosamente, false en caso
     * contrario
     */
    boolean rollbackVenta(int folio);

    /**
     * Obtiene el historial completo de todas las ventas registradas en el
     * sistema.
     *
     * @return una lista de objetos HistorialVentaVO con todas las ventas, o una
     * lista vacía si no hay ventas registradas
     */
    List<HistorialVentaVO> obtenerHistorialVentas();

    // Nuevos métodos para búsquedas
    /**
     * Busca ventas realizadas por un empleado específico basándose en su
     * nombre. La búsqueda puede ser exacta o parcial dependiendo de la
     * implementación.
     *
     * @param nombre el nombre del empleado (puede ser nombre completo o
     * parcial)
     * @return una lista de objetos HistorialVentaVO con las ventas del
     * empleado, o una lista vacía si no se encuentran coincidencias
     */
    List<HistorialVentaVO> buscarVentasPorEmpleado(String nombre);

    /**
     * Busca una venta específica utilizando su número de folio único.
     *
     * @param folio el número de folio de la venta a buscar
     * @return una lista con el objeto HistorialVentaVO correspondiente al
     * folio, o una lista vacía si no se encuentra la venta
     */
    List<HistorialVentaVO> buscarVentasPorFolio(int folio);

    /**
     * Busca ventas que coincidan con un monto total específico.
     *
     * @param total el monto total de las ventas a buscar
     * @return una lista de objetos HistorialVentaVO con ventas del monto
     * especificado, o una lista vacía si no se encuentran coincidencias
     */
    List<HistorialVentaVO> buscarVentasPorTotal(int total);

    /**
     * Busca ventas realizadas en una fecha específica.
     *
     * @param fecha la fecha de las ventas a buscar (formato: "YYYY-MM-DD" o
     * según implementación)
     * @return una lista de objetos HistorialVentaVO con las ventas de la fecha
     * especificada, o una lista vacía si no se encuentran ventas en esa fecha
     */
    List<HistorialVentaVO> buscarVentasPorFecha(String fecha);
}
