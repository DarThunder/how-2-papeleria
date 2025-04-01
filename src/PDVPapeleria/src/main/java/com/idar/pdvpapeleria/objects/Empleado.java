package com.idar.pdvpapeleria.objects;

import javafx.beans.property.*;

public class Empleado {
    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty contraseña;
    private StringProperty rol;
    private StringProperty estado;

    public Empleado(int id, String nombre, String contraseña, String rol, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.contraseña = new SimpleStringProperty(contraseña);
        this.rol = new SimpleStringProperty(rol);
        this.estado = new SimpleStringProperty(estado);
    }

    public int getId() { return id.get(); }
    public String getNombre() { return nombre.get(); }
    public String getContraseña() { return contraseña.get(); }
    public String getRol() { return rol.get(); }
    public String getEstado() { return estado.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty contraseñaProperty() { return contraseña; }
    public StringProperty rolProperty() { return rol; }
    public StringProperty estadoProperty() { return estado; }
}
