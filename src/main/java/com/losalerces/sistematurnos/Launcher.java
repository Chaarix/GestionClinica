package com.losalerces.sistematurnos;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;


import java.util.List;
import java.util.Scanner;

public class Launcher {
    public static void main(String[] args) {
            ObraSocialDAO obraSocialDAO = new ObraSocialDAO();
            Scanner scanner = new Scanner(System.in); // Intermediario para leer el teclado

            System.out.println("=================================================");
            System.out.println("   SISTEMA DE CARGA DE OBRAS SOCIALES (CONSOLA)  ");
            System.out.println("=================================================");

            while (true) {
                System.out.print("\nIngrese el NOMBRE de la Obra Social (o escriba 'salir' para terminar): ");
                String nombreIngresado = scanner.nextLine().trim();

                // Condición para romper el bucle si el usuario quiere dejar de cargar
                if (nombreIngresado.equalsIgnoreCase("salir")) {
                    break;
                }

                if (nombreIngresado.isEmpty()) {
                    System.out.println("⚠️ El nombre no puede estar vacío. Intente de nuevo.");
                    continue;
                }

                // Creamos el objeto con tu constructor limpio de 2 parámetros
                ClaseObraSocial nuevaOS = new ClaseObraSocial(0, nombreIngresado);

                // Intentamos guardarlo en SQLite usando tu DAO
                boolean exito = obraSocialDAO.insertar(nuevaOS);

                if (exito) {
                    System.out.println("✅ ¡'" + nombreIngresado + "' guardada con éxito en la base de datos!");
                } else {
                    System.out.println("❌ Error: No se pudo guardar la obra social.");
                }
            }

            System.out.println("\n=================================================");
            System.out.println("   REGISTROS ACTUALES EN LA BASE DE DATOS        ");
            System.out.println("=================================================");

            // Listamos todo lo guardado para verificar
            List<ClaseObraSocial> listadoFinal = obraSocialDAO.listarTodos();
            if (listadoFinal.isEmpty()) {
                System.out.println("No hay registros en la tabla.");
            } else {
                for (ClaseObraSocial os : listadoFinal) {
                    System.out.println("ID: " + os.idObraSocial() + " | Nombre: " + os.nombre());
                }
            }

            System.out.println("\nCarga finalizada. ¡Buen trabajo!");
            scanner.close(); // Cerramos el recurso de lectura
        }
    }

