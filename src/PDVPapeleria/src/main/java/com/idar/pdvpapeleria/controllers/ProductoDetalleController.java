/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.idar.pdvpapeleria.controllers;

import VO.ProductoVO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controlador para la vista de detalle de producto.
 * Muestra la información detallada de un producto en la interfaz gráfica,
 * incluyendo precios, stock, categoría y descripción.
 * 
 * @author Alvaro
 */
public class ProductoDetalleController {
    
    @FXML private Label idLabel;
    @FXML private Label nombreLabel;
    @FXML private Label precioCompraLabel;
    @FXML private Label precioVentaLabel;
    @FXML private Label stockLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label descripcionLabel;
    
    /**
     * Establece los datos del producto a mostrar en los componentes de la vista.
     * Actualiza todas las etiquetas con la información correspondiente del producto.
     * 
     * @param producto El objeto ProductoVO que contiene los datos a mostrar
     */
    public void setProducto(ProductoVO producto) {
        idLabel.setText("ID: " + producto.getIdProducto());
        nombreLabel.setText("Nombre: " + producto.getNombre());
        precioCompraLabel.setText("Precio Compra: $" + producto.getPrecioDeCompra());
        precioVentaLabel.setText("Precio Venta: $" + producto.getPrecioDeVenta());
        stockLabel.setText("Stock: " + producto.getStock());
        categoriaLabel.setText("Categoría: " + producto.getCategoria());
        descripcionLabel.setText("Descripción: " + producto.getDescripcion());
    }
}