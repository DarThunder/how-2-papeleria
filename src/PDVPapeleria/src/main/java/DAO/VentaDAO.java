package DAO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import VO.HistorialVentaVO;
import java.util.List;

/**
 *
 * @author dard
 */
public interface VentaDAO {
    int generarVenta(int idEmpleado, int total);
    boolean rollbackVenta(int folio);
    List<HistorialVentaVO> obtenerHistorialVentas();
    
    // Nuevos métodos para búsquedas
    List<HistorialVentaVO> buscarVentasPorEmpleado(String nombre);
    List<HistorialVentaVO> buscarVentasPorFolio(int folio);
    List<HistorialVentaVO> buscarVentasPorTotal(int total);
    List<HistorialVentaVO> buscarVentasPorFecha(String fecha);
}
