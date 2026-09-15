package com.losalerces.sistematurnos.Clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClasePaciente {

   private int idPaciente;
   private String nombre;
   private String apellido;
   private String fechaNacimiento;
   private String telefono;
   private String email;
   private int idObraSocial;

}
