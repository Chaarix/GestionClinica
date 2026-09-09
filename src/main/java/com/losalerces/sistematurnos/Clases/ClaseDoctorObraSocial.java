package com.losalerces.sistematurnos.Clases;

public class ClaseDoctorObraSocial {

    private int idDoctorObraSocial;
    private int idDoctor;
    private int idObraSocial;

    public ClaseDoctorObraSocial(int idDoctorObraSocial, int idDoctor, int idObraSocial) {
        this.idDoctorObraSocial = idDoctorObraSocial;
        this.idDoctor = idDoctor;
        this.idObraSocial = idObraSocial;
    }

    public int idDoctorObraSocial() {
        return idDoctorObraSocial;
    }

    public ClaseDoctorObraSocial setIdDoctorObraSocial(int idDoctorObraSocial) {
        this.idDoctorObraSocial = idDoctorObraSocial;
        return this;
    }

    public int idDoctor() {
        return idDoctor;
    }

    public ClaseDoctorObraSocial setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
        return this;
    }

    public int idObraSocial() {
        return idObraSocial;
    }

    public ClaseDoctorObraSocial setIdObraSocial(int idObraSocial) {
        this.idObraSocial = idObraSocial;
        return this;
    }
}
