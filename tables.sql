create database pdvpapeleria;

use pdvpapeleria;

-- Tabla de empleados --
CREATE TABLE Empleado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL,
    rol ENUM('Dueño', 'Administrador', 'Cajero') NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    codigoSeguridad VARCHAR(4) UNIQUE NOT NULL,
    estado ENUM('Activo', 'Inactivo') NOT NULL
);

-- Tabla de ventas --
CREATE TABLE venta (
    folio INT AUTO_INCREMENT PRIMARY KEY,
    idEmpleado INT NOT NULL,
    fechaYHora DATETIME NOT NULL,
    total INT NOT NULL,
    FOREIGN KEY (idEmpleado) REFERENCES Empleado(id)
);

-- Tabla de productos --
CREATE TABLE producto (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precioDeCompra INT NOT NULL,
    precioDeVenta INT NOT NULL,
    stock INT NOT NULL,
    descripcion TEXT,
    categoria ENUM(
        'Material de Escritura',
        'Papelería y Cuadernos',
        'Arte y Manualidades',
        'Oficina y Organización',
        'Tecnología y Electrónica'
    ) NOT NULL,
    isDeleted BOOLEAN DEFAULT FALSE,
    deletedAt TIMESTAMP NULL DEFAULT NULL
);

-- Tabla de proveedor --
CREATE TABLE proveedor (
    idProveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(254) NOT NULL,
    servicio VARCHAR(254) NOT NULL,
    telefono VARCHAR(254) NOT NULL
);

-- Tabla de relación proveedor --
CREATE TABLE provee (
    idProducto INT NOT NULL,
    idProveedor INT NOT NULL,
    PRIMARY KEY (idProducto, idProveedor),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto),
    FOREIGN KEY (idProveedor) REFERENCES proveedor(idProveedor)
);
