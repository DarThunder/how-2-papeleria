/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import VO.EmpleadoVO;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz para definir las operaciones de acceso a datos relacionadas con empleados.
 * 
 * <p>Esta interfaz contiene métodos para crear, verificar, actualizar y eliminar empleados,
 * así como para validar credenciales y recuperar información de empleados.</p>
 * 
 * @author dylxn999
 * @date 28/05/2025
 */
public interface EmpleadoDAO {

    /**
     * Crea un nuevo empleado en la base de datos.
     * 
     * @param empleado Objeto EmpleadoVO con los datos del nuevo empleado.
     * @return true si se creó correctamente, false en caso contrario.
     */
    boolean createUser(EmpleadoVO empleado);

    /**
     * Verifica si un nombre de usuario ya existe.
     * 
     * @param username Nombre de usuario a verificar.
     * @return true si existe, false si no.
     */
    boolean existeNombreUsuario(String username);

    /**
     * Verifica si existe un empleado por su ID.
     * 
     * @param id ID del empleado.
     * @return true si existe, false en caso contrario.
     */
    boolean existeEmpleadoPorId(int id);

    /**
     * Verifica si un código de seguridad existe.
     * 
     * @param codigoSeguridad Código de seguridad a verificar.
     * @return true si existe, false si no.
     */
    boolean existeCodigoSeguridad(String codigoSeguridad);

    /**
     * Obtiene el rol de un empleado por su nombre de usuario.
     * 
     * @param username Nombre de usuario.
     * @return Rol del usuario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    String getRole(String username) throws SQLException;

    /**
     * Verifica si las credenciales de usuario son válidas.
     * 
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return true si las credenciales son correctas, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    boolean isValidCredentials(String username, String password) throws SQLException;

    /**
     * Verifica si un código de seguridad pertenece a un usuario.
     * 
     * @param username Nombre de usuario.
     * @param codigoSeguridad Código de seguridad.
     * @return true si coincide, false si no.
     */
    boolean verificarCodigoSeguridad(String username, String codigoSeguridad);

    /**
     * Cambia la contraseña de un usuario basado en su código de seguridad.
     * 
     * @param nuevaContraseña Nueva contraseña a establecer.
     * @param codigoSeguridad Código de seguridad.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    boolean cambiarContraseña(String nuevaContraseña, String codigoSeguridad);

    /**
     * Elimina un empleado por su ID.
     * 
     * @param id ID del empleado.
     * @return true si fue eliminado, false si no se encontró o no se pudo eliminar.
     */
    boolean eliminarEmpleado(int id);

    /**
     * Obtiene una lista de todos los empleados registrados.
     * 
     * @return Lista de objetos EmpleadoVO.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    List<EmpleadoVO> obtenerTodosEmpleados() throws SQLException;

    /**
     * Actualiza el nombre de un empleado.
     * 
     * @param idEmpleado ID del empleado.
     * @param nuevoNombre Nuevo nombre a establecer.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    boolean actualizarNombreEmpleado(int idEmpleado, String nuevoNombre);

    /**
     * Actualiza el código de seguridad de un empleado.
     * 
     * @param idEmpleado ID del empleado.
     * @param nuevoCodigoSeguridad Nuevo código de seguridad.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    boolean actualizarCodigoSeguridadEmpleado(int idEmpleado, String nuevoCodigoSeguridad);

    /**
     * Actualiza el rol de un empleado.
     * 
     * @param idEmpleado ID del empleado.
     * @param nuevoRol Nuevo rol.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    boolean actualizarRolEmpleado(int idEmpleado, String nuevoRol);

    /**
     * Actualiza el estado de un empleado.
     * 
     * @param idEmpleado ID del empleado.
     * @param nuevoEstado Nuevo estado (por ejemplo, activo o inactivo).
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    boolean actualizarEstadoEmpleado(int idEmpleado, String nuevoEstado);

    /**
     * Obtiene un empleado por nombre de usuario y código de seguridad.
     * 
     * @param username Nombre de usuario.
     * @param codigoSeguridad Código de seguridad.
     * @return EmpleadoVO si se encuentra, null si no existe.
     */
    EmpleadoVO obtenerEmpleadoPorUsuarioYCodigo(String username, String codigoSeguridad);
}
