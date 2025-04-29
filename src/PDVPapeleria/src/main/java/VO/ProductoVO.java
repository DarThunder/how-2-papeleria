/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VO;

import javafx.beans.property.*;

/**
 *
 * @author laura
 */
public class ProductoVO {

    private final IntegerProperty idProducto;
    private final StringProperty nombre;
    private final IntegerProperty precioDeCompra;
    private final IntegerProperty precioDeVenta;
    private final IntegerProperty stock;
    private final StringProperty categoria;

    // Constructor
    public ProductoVO(int id, String nombre, int precioCompra, int precioVenta, int stock, String categoria) {
        this.idProducto = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.precioDeCompra = new SimpleIntegerProperty(precioCompra);
        this.precioDeVenta = new SimpleIntegerProperty(precioVenta);
        this.stock = new SimpleIntegerProperty(stock);
        this.categoria = new SimpleStringProperty(categoria);
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

    public String getCategoria() {
        return categoria.get();
    }

    public StringProperty categoriaProperty() {
        return categoria;
    }

}
