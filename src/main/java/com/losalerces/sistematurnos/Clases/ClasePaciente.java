package com.losalerces.sistematurnos.Clases;

public class ClasePaciente {

    private int idPaciente;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String telefono;
    private String email;
    private int idObraSocial;
    private String dni;

    public ClasePaciente(int idPaciente,
                         String nombre,
                         String apellido,
                         String fechaNacimiento,
                         String telefono,
                         String email,
                         int idObraSocial,
                         String dni) {

        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
        this.idObraSocial = idObraSocial;
        this.dni = dni;
    }

    public ClasePaciente() {
    }

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

    public String fechaNacimiento() {
        return fechaNacimiento;
    }

    public ClasePaciente setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        return this;
    }

    public String telefono() {
        return telefono;
    }

    public ClasePaciente setTelefono(String telefono) {
        this.telefono = telefono;
        return this;
    }

    public String email() {
        return email;
    }

    public ClasePaciente setEmail(String email) {
        this.email = email;
        return this;
    }

    public int idObraSocial() {
        return idObraSocial;
    }

    public ClasePaciente setIdObraSocial(int idObraSocial) {
        this.idObraSocial = idObraSocial;
        return this;
    }

    public String dni() {
        return dni;
    }

    public ClasePaciente setDni(String dni) {
        this.dni = dni;
        return this;
    }
}