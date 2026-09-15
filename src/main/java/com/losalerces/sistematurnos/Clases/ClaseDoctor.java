package com.losalerces.sistematurnos.Clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaseDoctor {
    private int idDoctor;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String correo;
    private String especialidad;

}

