package VO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author goatt
 */
public class HistorialProductoVO {
    private int idProducto;
    private String nombreAntes, descripcionAntes, categoriaAntes;
    private int precioCompraAntes, precioVentaAntes, stockAntes;
    private String nombreDesp, descripcionDesp, categoriaDesp;
    private int precioCompraDesp, precioVentaDesp, stockDesp;
    private String fechaHora;
    
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