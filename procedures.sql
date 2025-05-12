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
	-- Elimina el proveedor de la tabla proveedor
	UPDATE `proveedor` 
		SET `isDeleted` = '1' 
	WHERE (`idProveedor` = p_id);
END $$

DELIMITER $$

-- Procedimiento almacenado para editar proveedor
DELIMITER $$

CREATE PROCEDURE editarProveedor(
	in p_id INT,
    in p_nombre VARCHAR(100),
    in p_servicio VARCHAR(100),
    in p_telefono VARCHAR(10)
)
BEGIN
	UPDATE proveedor
		SET nombre = p_nombre, 
			servicio = p_servicio, 
			telefono = p_telefono
	WHERE idProveedor = p_id;
END $$

DELIMITER $$


-- Eliminar procedimiento almacenado si existe
DROP PROCEDURE IF EXISTS agregarProductoConProveedor;

DELIMITER $$

CREATE PROCEDURE agregarProductoConProveedor(
    IN p_nombre VARCHAR(255),
    IN p_precioDeCompra INT,
    IN p_precioDeVenta INT,
    IN p_stock INT,
    IN p_descripcion TEXT,
    IN p_categoria ENUM(
        'Material de Escritura',
        'Papelería y Cuadernos',
        'Arte y Manualidades',
        'Oficina y Organización',
        'Tecnología y Electrónica'
    ),
    IN p_idProveedor INT
)
BEGIN
    DECLARE product_id INT;
    
    -- Insertar el producto
    INSERT INTO producto (nombre, precioDeCompra, precioDeVenta, stock, descripcion, categoria)
    VALUES (p_nombre, p_precioDeCompra, p_precioDeVenta, p_stock, p_descripcion, p_categoria);
    
    -- Obtener el ID del producto recién insertado
    SET product_id = LAST_INSERT_ID();
    
    -- Establecer la relación con el proveedor
    INSERT INTO provee (idProducto, idProveedor)
    VALUES (product_id, p_idProveedor);
END $$

DELIMITER ;

DROP PROCEDURE IF EXISTS ObtenerProductosPorProveedor;
-- Procedimiento para obtener los productos de un proveedor por su nombre --

DELIMITER $$

CREATE PROCEDURE ObtenerProductosPorProveedor(IN nombre_proveedor VARCHAR(255))
BEGIN
    SELECT 
        p.idProducto,
        p.nombre AS nombre_producto,
        p.precioDeCompra,
        p.precioDeVenta,
        p.stock,
        p.categoria
    FROM 
        provee pr
    JOIN 
        proveedor prov ON pr.idProveedor = prov.idProveedor
    JOIN 
        producto p ON pr.idProducto = p.idProducto
    WHERE 
        prov.nombre = nombre_proveedor;
END $$

DELIMITER ;
