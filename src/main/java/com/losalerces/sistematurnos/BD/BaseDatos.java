package com.losalerces.sistematurnos.BD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDatos {
    private static final String URL = "jdbc:sqlite:src/main/java/com/losalerces/sistematurnos/BD/turnos.bd";

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error al conectar");
            e.printStackTrace();
            return null;
        }
    }
}
