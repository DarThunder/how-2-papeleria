/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import VO.EmpleadoVO;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Dylan
 */
public interface EmpleadoDAO {

    boolean createUser(EmpleadoVO empleado);

    boolean existeNombreUsuario(String username);

    boolean existeEmpleadoPorId(int id);

    boolean existeCodigoSeguridad(String codigoSeguridad);

    String getRole(String username) throws SQLException;

    boolean isValidCredentials(String username, String password) throws SQLException;

    boolean verificarCodigoSeguridad(String username, String codigoSeguridad);

    boolean cambiarContraseña(String nuevaContraseña, String codigoSeguridad);

    boolean eliminarEmpleado(int id);

    List<EmpleadoVO> obtenerTodosEmpleados() throws SQLException;
    
    boolean actualizarNombreEmpleado(int idEmpleado, String nuevoNombre);
    
    boolean actualizarCodigoSeguridadEmpleado(int idEmpleado, String nuevoCodigoSeguridad);

    boolean actualizarRolEmpleado(int idEmpleado, String nuevoRol);
    
    boolean actualizarEstadoEmpleado(int idEmpleado, String nuevoEstado);
    
    EmpleadoVO obtenerEmpleadoPorUsuarioYCodigo(String username, String codigoSeguridad);

}
