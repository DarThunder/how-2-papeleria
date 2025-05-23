package VO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author goatt
 */
public class HistorialProveedorVO {
    private int idProveedor;
    private String nombreAntes, servicioAntes, telefonoAntes;
    private String nombreDesp, servicioDesp, telefonoDesp;
    private String fechaHora;
    
    public HistorialProveedorVO(int id, String nombreA, String servicioA, String telefonoA, 
            String nombreD, String servicioD, String telefonoD, String fechaHorita){
        
        this.idProveedor = id;
        this.nombreAntes = nombreA;
        this.servicioAntes = servicioA;
        this.telefonoAntes = telefonoA;
        this.nombreDesp = nombreD;
        this.servicioDesp = servicioD;
        this.telefonoDesp = telefonoD;
        this.fechaHora = fechaHorita;
    }

    @Override
    public String toString() {
        return "Cambio en proveedor ID: " + idProveedor +
                "\nAntes => Nombre: " + nombreAntes + ", Servicio: " + servicioAntes + ", Teléfono: " + telefonoAntes +
                "\nDespués => Nombre: " + nombreDesp + ", Servicio: " + servicioDesp + ", Teléfono: " + telefonoDesp +
                "\nFecha y Hora: " + fechaHora + "\n";
    }
}
