/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VO;

import javafx.beans.property.*;

/**
 * Clase que representa un Producto como Value Object (VO) con propiedades observables para JavaFX.
 * Contiene información sobre productos incluyendo precios, stock, categoría y cantidad.
 * 
 * @author Alvaro
 */
public class ProductoVO {

  private final IntegerProperty idProducto;
  private final StringProperty nombre;
  private final IntegerProperty precioDeCompra;
  private final IntegerProperty precioDeVenta;
  private final IntegerProperty stock;
  private final StringProperty descripcion;
  private final StringProperty categoria;
  private IntegerProperty cantidad;
  
  /**
   * Constructor con 6 parámetros (sin descripción).
   * 
   * @param id Identificador único del producto
   * @param nombre Nombre del producto
   * @param precioCompra Precio al que se compra el producto
   * @param precioVenta Precio al que se vende el producto
   * @param stock Cantidad disponible en inventario
   * @param categoria Categoría a la que pertenece el producto
   */
  public ProductoVO(int id, String nombre, int precioCompra, int precioVenta, 
                   int stock, String categoria) {
      this(id, nombre, precioCompra, precioVenta, stock, "", categoria); // Descripción vacía
  }

  /**
   * Constructor completo con 7 parámetros.
   * 
   * @param id Identificador único del producto
   * @param nombre Nombre del producto
   * @param precioCompra Precio al que se compra el producto
   * @param precioVenta Precio al que se vende el producto
   * @param stock Cantidad disponible en inventario
   * @param descripcion Descripción detallada del producto
   * @param categoria Categoría a la que pertenece el producto
   */
  public ProductoVO(int id, String nombre, int precioCompra, int precioVenta, 
                   int stock, String descripcion, String categoria) {
    this.idProducto = new SimpleIntegerProperty(id);
    this.nombre = new SimpleStringProperty(nombre);
    this.precioDeCompra = new SimpleIntegerProperty(precioCompra);
    this.precioDeVenta = new SimpleIntegerProperty(precioVenta);
    this.stock = new SimpleIntegerProperty(stock);
    this.descripcion = new SimpleStringProperty(descripcion);
    this.categoria = new SimpleStringProperty(categoria);
    this.cantidad = new SimpleIntegerProperty(1);
  }

  /**
   * Obtiene el ID del producto.
   * @return ID del producto
   */
  public int getIdProducto() {
    return idProducto.get();
  }

  /**
   * Obtiene la propiedad observable del ID.
   * @return Propiedad IntegerProperty del ID
   */
  public IntegerProperty idProductoProperty() {
    return idProducto;
  }

  /**
   * Obtiene el nombre del producto.
   * @return Nombre del producto
   */
  public String getNombre() {
    return nombre.get();
  }

  /**
   * Obtiene la propiedad observable del nombre.
   * @return Propiedad StringProperty del nombre
   */
  public StringProperty nombreProperty() {
    return nombre;
  }

  /**
   * Obtiene el precio de compra del producto.
   * @return Precio de compra
   */
  public int getPrecioDeCompra() {
    return precioDeCompra.get();
  }

  /**
   * Obtiene la propiedad observable del precio de compra.
   * @return Propiedad IntegerProperty del precio de compra
   */
  public IntegerProperty precioDeCompraProperty() {
    return precioDeCompra;
  }

  /**
   * Obtiene el precio de venta del producto.
   * @return Precio de venta
   */
  public int getPrecioDeVenta() {
    return precioDeVenta.get();
  }

  /**
   * Obtiene la propiedad observable del precio de venta.
   * @return Propiedad IntegerProperty del precio de venta
   */
  public IntegerProperty precioDeVentaProperty() {
    return precioDeVenta;
  }

  /**
   * Obtiene el stock disponible del producto.
   * @return Cantidad en stock
   */
  public int getStock() {
    return stock.get();
  }

  /**
   * Obtiene la propiedad observable del stock.
   * @return Propiedad IntegerProperty del stock
   */
  public IntegerProperty stockProperty() {
    return stock;
  }

  /**
   * Obtiene la descripción del producto.
   * @return Descripción del producto
   */
  public String getDescripcion() {
    return descripcion.get();
  }
  
  /**
   * Obtiene la propiedad observable de la descripción.
   * @return Propiedad StringProperty de la descripción
   */
  public StringProperty descripcionProperty() {
    return descripcion;
  }
  
  /**
   * Obtiene la categoría del producto.
   * @return Categoría del producto
   */
  public String getCategoria() {
    return categoria.get();
  }

  /**
   * Obtiene la propiedad observable de la categoría.
   * @return Propiedad StringProperty de la categoría
   */
  public StringProperty categoriaProperty() {
    return categoria;
  }

  /**
   * Obtiene la cantidad seleccionada del producto.
   * @return Cantidad seleccionada
   */
  public int getCantidad() {
    return cantidad.get();
  }

  /**
   * Obtiene la propiedad observable de la cantidad.
   * @return Propiedad IntegerProperty de la cantidad
   */
  public IntegerProperty cantidadProperty() {
    return cantidad;
  }

  /**
   * Establece la cantidad seleccionada del producto.
   * @param cantidad Nueva cantidad a establecer
   */
  public void setCantidad(int cantidad) {
    this.cantidad.set(cantidad);
  }
  
  /**
   * Calcula el subtotal (precio de venta * cantidad).
   * @return Subtotal calculado
   */
  public int getSubtotal() {
    return cantidad.get() * precioDeVenta.get();
  }

  /**
   * Obtiene la propiedad observable del subtotal.
   * @return Propiedad IntegerProperty del subtotal
   */
  public IntegerProperty subtotalProperty() {
    return new SimpleIntegerProperty(getSubtotal());
  }
  
  /**
   * Representación en String del producto.
   * @return String con formato "Nombre xCantidad ($Precio)"
   */
  @Override
  public String toString() {
    return String.format("%s x%d ($%d)", getNombre(), getCantidad(), getPrecioDeVenta());
  }
}