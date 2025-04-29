-- Eliminar procedimiento almacenado si existe
DROP PROCEDURE IF EXISTS agregarEmpleado;

-- Procedimiento almacenado para agregar un nuevo empleado
DELIMITER $$

CREATE PROCEDURE agregarEmpleado(
    IN p_nombre VARCHAR(100), 
    IN p_contraseña VARCHAR(255), 
    IN p_codigoSeguridad VARCHAR(10), 
    IN p_rol ENUM('admin', 'user')
)
BEGIN
    INSERT INTO empleado (nombre, contraseña, rol, codigoSeguridad, estado)
    VALUES (p_nombre, p_contraseña, p_rol, p_codigoSeguridad, 'active');
END $$

DELIMITER ;
