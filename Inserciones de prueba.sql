-- Insertar proveedores
INSERT INTO proveedor (nombre, servicio, telefono) VALUES 
('Papelería Moderna', 'Material de oficina', '5551002000'),
('Distribuidora Escolar', 'Útiles escolares', '5551003000'),
('ArteCreativo', 'Material artístico', '5551004000'),
('TecnoSuministros', 'Equipos electrónicos', '5551005000');

-- Insertar productos
INSERT INTO producto (nombre, precioDeCompra, precioDeVenta, stock, descripcion, categoria) VALUES 
('Lápiz HB', 2, 5, 100, 'Lápiz grafito grado HB', 'Material de Escritura'),
('Cuaderno profesional', 15, 30, 50, 'Cuaderno de 100 hojas rayadas', 'Papelería y Cuadernos'),
('Acuarelas 12 colores', 80, 150, 20, 'Set básico de acuarelas', 'Arte y Manualidades'),
('Mouse inalámbrico', 120, 250, 30, 'Mouse ergonómico inalámbrico', 'Tecnología y Electrónica'),
('Resma papel carta', 40, 80, 40, 'Paquete de 500 hojas', 'Oficina y Organización');

delete from provee;
-- Establecer relaciones PROVEE (idProducto, idProveedor)
INSERT INTO provee (idProducto, idProveedor) VALUES 
(1, 1),  -- Lápiz HB proveído por Papelería Moderna
(2, 2),  -- Cuaderno profesional proveído por Distribuidora Escolar
(3, 3),  -- Acuarelas proveídas por ArteCreativo
(4, 4),  -- Mouse inalámbrico proveído por TecnoSuministros
(5, 4);  -- Resma papel proveída por Papelería Moderna

-- Consulta de verificación
SELECT p.nombre AS producto, pr.nombre AS proveedor
FROM producto p
JOIN provee ON p.idProducto = provee.idProducto
JOIN proveedor pr ON provee.idProveedor = pr.idProveedor
ORDER BY p.nombre;
