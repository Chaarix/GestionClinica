package com.losalerces.sistematurnos.Clases;

public class ClaseObraSocial {
    private int idObraSocial;
    private String nombre;
    private String telefono;
    private String direccion;

    public ClaseObraSocial(int idObraSocial, String nombre, String telefono, String direccion) {
        this.idObraSocial = idObraSocial;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int idObraSocial() {
        return idObraSocial;
    }

    public ClaseObraSocial setIdObraSocial(int idObraSocial) {
        this.idObraSocial = idObraSocial;
        return this;
    }

    public String nombre() {
        return nombre;
    }

    public ClaseObraSocial setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String telefono() {
        return telefono;
    }

    public ClaseObraSocial setTelefono(String telefono) {
        this.telefono = telefono;
        return this;
    }

    public String direccion() {
        return direccion;
    }

    public ClaseObraSocial setDireccion(String direccion) {
        this.direccion = direccion;
        return this;
    }
}
