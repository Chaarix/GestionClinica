package com.losalerces.sistematurnos.Clases;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClaseTurno {

    private int idTurno;
    private int idPaciente;
    private int idDoctor;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private String motivo;

    public ClaseTurno(int idTurno, int idPaciente, int idDoctor, LocalDate fecha, LocalTime hora, String estado, String motivo) {
        this.idTurno = idTurno;
        this.idPaciente = idPaciente;
        this.idDoctor = idDoctor;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.motivo = motivo;
    }

    public int idTurno() {
        return idTurno;
    }

    public ClaseTurno setIdTurno(int idTurno) {
        this.idTurno = idTurno;
        return this;
    }

    public int idPaciente() {
        return idPaciente;
    }

    public ClaseTurno setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
        return this;
    }

    public int idDoctor() {
        return idDoctor;
    }

    public ClaseTurno setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
        return this;
    }

    public LocalDate fecha() {
        return fecha;
    }

    public ClaseTurno setFecha(LocalDate fecha) {
        this.fecha = fecha;
        return this;
    }

    public LocalTime hora() {
        return hora;
    }

    public ClaseTurno setHora(LocalTime hora) {
        this.hora = hora;
        return this;
    }

    public String estado() {
        return estado;
    }

    public ClaseTurno setEstado(String estado) {
        this.estado = estado;
        return this;
    }

    public String motivo() {
        return motivo;
    }

    public ClaseTurno setMotivo(String motivo) {
        this.motivo = motivo;
        return this;
    }
}
