package com.losalerces.sistematurnos.Clases;

public class ClaseDoctor {

    private int idDoctor;

    private String nombre;

    private String apellido;

    private String especialidad;


    public ClaseDoctor(
            int idDoctor,
            String nombre,
            String apellido,
            String especialidad
    ) {

        this.idDoctor =
                idDoctor;

        this.nombre =
                nombre;

        this.apellido =
                apellido;

        this.especialidad =
                especialidad;
    }


    public int idDoctor() {

        return idDoctor;
    }


    public String nombre() {

        return nombre;
    }


    public String apellido() {

        return apellido;
    }


    public String especialidad() {

        return especialidad;
    }


    public void setIdDoctor(
            int idDoctor
    ) {

        this.idDoctor =
                idDoctor;
    }


    public void setNombre(
            String nombre
    ) {

        this.nombre =
                nombre;
    }


    public void setApellido(
            String apellido
    ) {

        this.apellido =
                apellido;
    }


    public void setEspecialidad(
            String especialidad
    ) {

        this.especialidad =
                especialidad;
    }
}