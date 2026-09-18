package com.losalerces.sistematurnos.BD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDatos {

    private static final String URL =
            "jdbc:sqlite:src/main/java/com/losalerces/sistematurnos/BD/turnos.bd";

    public static Connection getConnection() throws SQLException {

        Connection conexion = DriverManager.getConnection(URL);

        // Activar claves foráneas en SQLite
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conexion;
    }
}