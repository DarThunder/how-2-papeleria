package DAO;

import VO.ProductoVO;
import VO.ProveedorVO;
import java.sql.SQLException;
import java.util.List;

public interface ProveedorDAO {

    ProveedorVO obtenerProveedorDeProducto(int idProducto) throws SQLException;

    List<ProveedorVO> obtenerTodosProveedores() throws SQLException;
    
    ProveedorVO obtenerProveedorPorId(int id) throws SQLException;

    boolean agregarProveedor(ProveedorVO proveedor) throws SQLException;

    boolean eliminarProveedor(List<ProveedorVO> proveedores, int indiceSeleccionado) throws SQLException;

    boolean editarProveedor(int id, String nombre, String servicio, String telefono) throws SQLException;
    
    public List<String> obtenerNombresProveedores() throws SQLException; 
    
    public List<ProductoVO> obtenerProductosPorProveedor(String nombreProveedor) throws SQLException; 
}
