package VO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Clase que representa un historial de cambios realizados sobre un producto.
 * Almacena los valores anteriores y posteriores de cada campo, así como la fecha y hora del cambio.
 * 
 * @author goatt
 */
public class HistorialProductoVO {

    /** ID del producto al que pertenece el historial */
    private int idProducto;

    /** Nombre del producto antes del cambio */
    private String nombreAntes;

    /** Descripción del producto antes del cambio */
    private String descripcionAntes;

    /** Categoría del producto antes del cambio */
    private String categoriaAntes;

    /** Precio de compra antes del cambio */
    private int precioCompraAntes;

    /** Precio de venta antes del cambio */
    private int precioVentaAntes;

    /** Stock disponible antes del cambio */
    private int stockAntes;

    /** Nombre del producto después del cambio */
    private String nombreDesp;

    /** Descripción del producto después del cambio */
    private String descripcionDesp;

    /** Categoría del producto después del cambio */
    private String categoriaDesp;

    /** Precio de compra después del cambio */
    private int precioCompraDesp;

    /** Precio de venta después del cambio */
    private int precioVentaDesp;

    /** Stock disponible después del cambio */
    private int stockDesp;

    /** Fecha y hora en que se realizó el cambio */
    private String fechaHora;

    /**
     * Constructor para inicializar un historial de producto con sus valores anteriores y posteriores.
     * 
     * @param id ID del producto
     * @param nombreA Nombre antes del cambio
     * @param precioCompraA Precio de compra antes del cambio
     * @param precioVentaA Precio de venta antes del cambio
     * @param stockA Stock antes del cambio
     * @param descripcionA Descripción antes del cambio
     * @param categoriaA Categoría antes del cambio
     * @param nombreD Nombre después del cambio
     * @param precioCompraD Precio de compra después del cambio
     * @param precioVentaD Precio de venta después del cambio
     * @param stockD Stock después del cambio
     * @param descripcionD Descripción después del cambio
     * @param categoriaD Categoría después del cambio
     * @param fechaHorita Fecha y hora del cambio
     */
    public HistorialProductoVO(int id, String nombreA, int precioCompraA, int precioVentaA, 
            int stockA, String descripcionA, String categoriaA,
            String nombreD, int precioCompraD, int precioVentaD, 
            int stockD, String descripcionD, String categoriaD, String fechaHorita){
        
        this.idProducto = id;
        this.nombreAntes = nombreA;
        this.precioCompraAntes = precioCompraA;
        this.precioVentaAntes = precioVentaA;
        this.stockAntes = stockA;
        this.descripcionAntes = descripcionA;
        this.categoriaAntes = categoriaA;
        this.nombreDesp = nombreD;
        this.precioCompraDesp = precioCompraD;
        this.precioVentaDesp = precioVentaD;
        this.stockDesp = stockD;
        this.descripcionDesp = descripcionD;
        this.categoriaDesp = categoriaD;
        this.fechaHora = fechaHorita;
    }

    /**
     * Representación textual del historial de cambios del producto.
     * 
     * @return Cadena con los valores antes y después del cambio, junto con la fecha y hora.
     */
    @Override
    public String toString() {
        return "Cambio en producto ID: " + idProducto +
                "\nAntes => Nombre: " + nombreAntes + 
                ", Precio Compra: " + precioCompraAntes + 
                ", Precio Venta: " + precioVentaAntes +
                ", Stock: " + stockAntes + 
                ", Descripción: " + descripcionAntes + 
                ", Categoría: " + categoriaAntes +
                "\nDespués => Nombre: " + nombreDesp + 
                ", Precio Compra: " + precioCompraDesp + 
                ", Precio Venta: " + precioVentaDesp +
                ", Stock: " + stockDesp + 
                ", Descripción: " + descripcionDesp + 
                ", Categoría: " + categoriaDesp +
                "\nFecha y Hora: " + fechaHora + "\n";
    }
}
