package com.losalerces.sistematurnos.Clases;

public class ClaseDoctor {
    private int idDoctor;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String correo;
    private String especialidad;

    public ClaseDoctor(int idDoctor, String nombre, String apellido, String dni, String telefono, String correo, String especialidad) {
        this.idDoctor = idDoctor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
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

    public String dni() {
        return dni;
    }

    public ClaseDoctor setDni(String dni) {
        this.dni = dni;
        return this;
    }

    public String telefono() {
        return telefono;
    }

    public ClaseDoctor setTelefono(String telefono) {
        this.telefono = telefono;
        return this;
    }

    public String correo() {
        return correo;
    }

    public ClaseDoctor setCorreo(String correo) {
        this.correo = correo;
        return this;
    }

    public String especialidad() {
        return especialidad;
    }

    public ClaseDoctor setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
        return this;
    }
}
