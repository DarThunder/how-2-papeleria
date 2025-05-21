package DAO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dard
 */
public interface VentaDAO {
    int generarVenta(int idEmpleado, int total);
    boolean rollbackVenta(int folio);
}
