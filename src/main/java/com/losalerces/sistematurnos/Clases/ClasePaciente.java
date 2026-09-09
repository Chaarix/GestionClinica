package com.losalerces.sistematurnos.Clases;

public class ClasePaciente {
    public int idPaciente() {
        return idPaciente;
    }

    public ClasePaciente setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
        return this;
    }

    public String nombre() {
        return nombre;
    }

    public ClasePaciente setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String apellido() {
        return apellido;
    }

    public ClasePaciente setApellido(String apellido) {
        this.apellido = apellido;
        return this;
    }

    public String dni() {
        return dni;
    }

    public ClasePaciente setDni(String dni) {
        this.dni = dni;
        return this;
    }

    public String telefono() {
        return telefono;
    }

    public ClasePaciente setTelefono(String telefono) {
        this.telefono = telefono;
        return this;
    }

    public String correo() {
        return correo;
    }

    public ClasePaciente setCorreo(String correo) {
        this.correo = correo;
        return this;
    }

    public String direccion() {
        return direccion;
    }

    public ClasePaciente setDireccion(String direccion) {
        this.direccion = direccion;
        return this;
    }

    private int idPaciente;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String correo;
    private String direccion;

    public ClasePaciente() {
    }

    public ClasePaciente(int idPaciente, String nombre, String apellido,
                    String dni, String telefono, String correo,
                    String direccion) {
        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }
}
