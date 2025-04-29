-- Tabla de empleados --
CREATE TABLE empleado (
	  idEmpleado INT AUTO_INCREMENT PRIMARY KEY, 
    contraseña VARCHAR(255) NOT NULL, 
    nombre VARCHAR(255) NOT NULL, 
    estado VARCHAR(255) NOT NULL,
    rol ENUM('admin', 'user') NOT NULL,
    codigoSeguridad VARCHAR(255) NOT NULL,
	  isDeleted BOOLEAN DEFAULT FALSE,
    deletedAt TIMESTAMP NULL DEFAULT NULL
); 

-- Tabla de ventas --
CREATE TABLE venta (
	  folio INT PRIMARY KEY AUTO_INCREMENT,
    idEmpleado INT NOT NULL,
    fechaYHora DATETIME NOT NULL,
    total INT NOT NULL,
    FOREIGN KEY (idEmpleado) REFERENCES empleado(idEmpleado)
); 

-- Tabla de productos --
CREATE TABLE producto (
    idProducto INT PRIMARY KEY NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    precioDeCompra INT NOT NULL,
    precioDeVenta INT NOT NULL,
    stock INT NOT NULL,
    imagen VARCHAR(255),
    descripcion TEXT,
    categoria VARCHAR(255), 
    isDeleted BOOLEAN DEFAULT FALSE,
    deletedAt TIMESTAMP NULL DEFAULT NULL
);

-- Tabla de relación proveedor --
CREATE TABLE provee (
    idProducto INT NOT NULL,
    idProveedor INT NOT NULL,
    PRIMARY KEY (idProducto, idProveedor),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto),
    FOREIGN KEY (idProveedor) REFERENCES proveedor(idProveedor)
);

-- Tabla de proveedor --
CREATE TABLE proveedor (
    idProveedor INT PRIMARY KEY NOT NULL,
    correo VARCHAR(255) NOT NULL,
    servicio VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    telefono VARCHAR(255) NOT NULL
);
