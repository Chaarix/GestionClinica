package com.losalerces.sistematurnos.Clases;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClaseTurno {

    private int idTurno;
    private int idPaciente;
    private int idDoctor;
    private LocalDate fechaTurno;
    private LocalTime horaTurno;

    public ClaseTurno(int idTurno, int idPaciente, int idDoctor, LocalDate fechaTurno, LocalTime horaTurno) {
        this.idTurno = idTurno;
        this.idPaciente = idPaciente;
        this.idDoctor = idDoctor;
        this.fechaTurno = fechaTurno;
        this.horaTurno = horaTurno;
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

    public LocalDate fechaTurno() {
        return fechaTurno;
    }

    public ClaseTurno setFechaTurno(LocalDate fechaTurno) {
        this.fechaTurno = fechaTurno;
        return this;
    }

    public LocalTime horaTurno() {
        return horaTurno;
    }

    public ClaseTurno setHoraTurno(LocalTime horaTurno) {
        this.horaTurno = horaTurno;
        return this;
    }
}