package com.losalerces.sistematurnos.Clases;

public class ClaseDoctor {
    private int idDoctor;
    private String nombre;
    private String apellido;
    private String especialidad;

    public ClaseDoctor(int idDoctor, String nombre, String apellido, String especialidad) {
        this.idDoctor = idDoctor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
    }

    public int idDoctor() {
        return idDoctor;
    }

    public ClaseDoctor setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
        return this;
    }

    public String nombre() {
        return nombre;
    }

    public ClaseDoctor setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String apellido() {
        return apellido;
    }

    public ClaseDoctor setApellido(String apellido) {
        this.apellido = apellido;
        return this;
    }


    public String especialidad() {
        return especialidad;
    }

    public ClaseDoctor setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
        return this;
    }

    public String getEspecialidad() {
        return especialidad;
    }



    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdDoctor() {
        return idDoctor;
    }
}
