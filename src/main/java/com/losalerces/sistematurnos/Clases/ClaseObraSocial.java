package com.losalerces.sistematurnos.Clases;

public class ClaseObraSocial {
    private int idObraSocial;
    private String nombre;


    public ClaseObraSocial(int idObraSocial, String nombre) {
        this.idObraSocial = idObraSocial;
        this.nombre = nombre;

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
}