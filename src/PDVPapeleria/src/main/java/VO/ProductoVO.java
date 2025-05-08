/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VO;

import javafx.beans.property.*;

/**
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
  
  //constructor de 6 parametros
  public ProductoVO(int id, String nombre, int precioCompra, int precioVenta, 
                     int stock, String categoria) {
        this(id, nombre, precioCompra, precioVenta, stock, "", categoria); // Descripción vacía
    }

  // Constructor de 7 parametros
  public ProductoVO(int id, String nombre, int precioCompra, int precioVenta, int stock, String descripcion, String categoria) {
    this.idProducto = new SimpleIntegerProperty(id);
    this.nombre = new SimpleStringProperty(nombre);
    this.precioDeCompra = new SimpleIntegerProperty(precioCompra);
    this.precioDeVenta = new SimpleIntegerProperty(precioVenta);
    this.stock = new SimpleIntegerProperty(stock);
    this.descripcion = new SimpleStringProperty(descripcion);
    this.categoria = new SimpleStringProperty(categoria);
    this.cantidad = new SimpleIntegerProperty(1);
  }

  // Getters y propiedades para JavaFX
  public int getIdProducto() {
    return idProducto.get();
  }

  public IntegerProperty idProductoProperty() {
    return idProducto;
  }

  public String getNombre() {
    return nombre.get();
  }

  public StringProperty nombreProperty() {
    return nombre;
  }

  public int getPrecioDeCompra() {
    return precioDeCompra.get();
  }

  public IntegerProperty precioDeCompraProperty() {
    return precioDeCompra;
  }

  public int getPrecioDeVenta() {
    return precioDeVenta.get();
  }

  public IntegerProperty precioDeVentaProperty() {
    return precioDeVenta;
  }

  public int getStock() {
    return stock.get();
  }

  public IntegerProperty stockProperty() {
    return stock;
  }

  public String getDescripcion() {
    return descripcion.get();
  }
  
  public StringProperty descripcionProperty() {
    return descripcion;
  }
  
  public String getCategoria() {
    return categoria.get();
  }

  public StringProperty categoriaProperty() {
    return categoria;
  }

  public int getCantidad() {
    return cantidad.get();
  }

  public IntegerProperty cantidadProperty() {
    return cantidad;
  }

  public void setCantidad(int cantidad) {
    this.cantidad.set(cantidad);
  }
  
  public int getSubtotal() {
        return cantidad.get() * precioDeVenta.get();
    }

    public IntegerProperty subtotalProperty() {
        return new SimpleIntegerProperty(getSubtotal());
    }

}
