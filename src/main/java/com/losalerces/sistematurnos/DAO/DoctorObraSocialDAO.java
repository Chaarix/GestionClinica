package com.losalerces.sistematurnos.DAO;


import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClaseDoctorObraSocial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorObraSocialDAO {


    public boolean agregar(ClaseDoctorObraSocial relacion) {

        String sql = """
                INSERT INTO DoctorObraSocial
                (idDoctor, idObraSocial)
                VALUES (?, ?)
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, relacion.idDoctor());
            ps.setInt(2, relacion.idObraSocial());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al agregar la relación doctor-obra social: " + e.getMessage());
            return false;
        }
    }


    // ==========================================
    // LISTAR TODAS LAS RELACIONES
    // ==========================================
    public List<ClaseDoctorObraSocial> listar() {

        List<ClaseDoctorObraSocial> lista = new ArrayList<>();

        String sql = """
                SELECT idDoctorObraSocial, idDoctor, idObraSocial
                FROM DoctorObraSocial
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ClaseDoctorObraSocial relacion = new ClaseDoctorObraSocial(
                        rs.getInt("idDoctorObraSocial"),
                        rs.getInt("idDoctor"),
                        rs.getInt("idObraSocial")
                );

                lista.add(relacion);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar las relaciones doctor-obra social: " + e.getMessage());
        }

        return lista;
    }


    // ==========================================
    // BUSCAR RELACIONES POR ID DE DOCTOR
    // (Para ver qué obras sociales acepta un doctor)
    // ==========================================
    public List<ClaseDoctorObraSocial> listarPorDoctor(int idDoctor) {

        List<ClaseDoctorObraSocial> lista = new ArrayList<>();

        String sql = """
                SELECT idDoctorObraSocial, idDoctor, idObraSocial
                FROM DoctorObraSocial
                WHERE idDoctor = ?
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idDoctor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ClaseDoctorObraSocial relacion = new ClaseDoctorObraSocial(
                            rs.getInt("idDoctorObraSocial"),
                            rs.getInt("idDoctor"),
                            rs.getInt("idObraSocial")
                    );

                    lista.add(relacion);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar obras sociales del doctor: " + e.getMessage());
        }

        return lista;
    }


    public List<ClaseDoctorObraSocial> listarPorObraSocial(int idObraSocial) {

        List<ClaseDoctorObraSocial> lista = new ArrayList<>();

        String sql = """
                SELECT idDoctorObraSocial, idDoctor, idObraSocial
                FROM DoctorObraSocial
                WHERE idObraSocial = ?
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idObraSocial);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ClaseDoctorObraSocial relacion = new ClaseDoctorObraSocial(
                            rs.getInt("idDoctorObraSocial"),
                            rs.getInt("idDoctor"),
                            rs.getInt("idObraSocial")
                    );

                    lista.add(relacion);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar doctores de la obra social: " + e.getMessage());
        }

        return lista;
    }

    public boolean eliminar(int idDoctorObraSocial) {

        String sql = "DELETE FROM DoctorObraSocial WHERE idDoctorObraSocial = ?";

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idDoctorObraSocial);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar la relación doctor-obra social: " + e.getMessage());
            return false;
        }
    }
}



