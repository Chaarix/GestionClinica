package com.losalerces.sistematurnos.BD;

import java.sql.Connection;
import java.sql.DriverManager;

public class BaseDatos {
    private static final String URL = "jdbc:sqlite:src/main/java/com/losalerces/sistematurnos/BD/turnos.bd";

    public static Connection getConnection() {
        try {
            // Asegura que Java cargue el driver de SQLite en memoria antes de conectar
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            throw new RuntimeException("Error conexión SQLite: " + e.getMessage(), e);
        }
    }
}
