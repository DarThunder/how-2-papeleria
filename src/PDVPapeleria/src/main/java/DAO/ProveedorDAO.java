/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import VO.ProveedorVO;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author goatt
 */
public interface ProveedorDAO {
    
    public List<ProveedorVO> obtenerTodosProveedores() throws SQLException;
    
    public boolean agregarProveedor(ProveedorVO proveedor) throws SQLException;
    
    public boolean eliminarProveedor(List<ProveedorVO> proveedores, int indiceSeleccionado);
    
}
