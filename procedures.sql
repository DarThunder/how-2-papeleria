-- Eliminar procedimiento almacenado si existe
DROP PROCEDURE IF EXISTS agregarEmpleado;

-- Procedimiento almacenado para agregar un nuevo empleado
DELIMITER $$

CREATE PROCEDURE agregarEmpleado(
    IN p_nombre VARCHAR(100), 
    IN p_contraseña VARCHAR(255), 
    IN p_codigoSeguridad VARCHAR(10), 
    IN p_rol ENUM('Dueño', 'Administrador', 'Cajero')
)
BEGIN
    INSERT INTO Empleado (nombre, contraseña, rol, codigoSeguridad, estado)
    VALUES (p_nombre, p_contraseña, p_rol, p_codigoSeguridad, 'Activo');
END $$

DELIMITER ;

-- Procedimiento almacenado para agregar un nuevo proveedor
DELIMITER $$

CREATE PROCEDURE agregarProveedor(
	in p_nombre VARCHAR(100),
    in p_servicio VARCHAR(100),
    in p_telefono VARCHAR(10)
)
BEGIN
	INSERT INTO proveedor (nombre, servicio, telefono)
	VALUES (p_nombre, p_servicio, p_telefono);
END $$

DELIMITER $$

-- Procedimiento almacenado para eliminar un proveedor
DELIMITER $$

CREATE PROCEDURE eliminarProveedor(
	in p_id INT
)
BEGIN
	DELETE FROM proveedor WHERE idProveedor = p_id;
END $$

DELIMITER $$
