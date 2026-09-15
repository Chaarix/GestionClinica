package com.losalerces.sistematurnos.Clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaseTurno {

    private int idTurno;
    private int idPaciente;
    private int idDoctor;
    private LocalDate fechaTurno;
    private LocalTime horaTurno;

}