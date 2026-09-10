package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClaseTurno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    // ==========================================
    // AGREGAR / INSERTAR TURNO
    // ==========================================
    public boolean agregar(ClaseTurno turno) {

        String sql = """
                INSERT INTO turnos
                (id_paciente, id_doctor, fecha_turno, hora_turno)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, turno.idPaciente());
            ps.setInt(2, turno.idDoctor());
            ps.setString(3, turno.fechaTurno().toString()); // O Date.valueOf(turno.fechaTurno())
            ps.setString(4, turno.horaTurno().toString());  // O Time.valueOf(turno.horaTurno())

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al agregar turno: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // LISTAR TODOS LOS TURNOS
    // ==========================================
    public List<ClaseTurno> listar() {
        List<ClaseTurno> turnos = new ArrayList<>();

        String sql = """
                SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno
                FROM turnos
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ClaseTurno turno = new ClaseTurno(
                        rs.getInt("id_turno"),
                        rs.getInt("id_paciente"),
                        rs.getInt("id_doctor"),
                        rs.getDate("fecha_turno").toLocalDate(),
                        rs.getTime("hora_turno").toLocalTime()
                );
                turnos.add(turno);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar turnos: " + e.getMessage());
        }

        return turnos;
    }

    // ==========================================
    // BUSCAR TURNO POR ID
    // ==========================================
    public ClaseTurno buscarPorId(int idTurno) {
        String sql = """
                SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno
                FROM turnos
                WHERE id_turno = ?
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idTurno);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ClaseTurno(
                            rs.getInt("id_turno"),
                            rs.getInt("id_paciente"),
                            rs.getInt("id_doctor"),
                            rs.getDate("fecha_turno").toLocalDate(),
                            rs.getTime("hora_turno").toLocalTime()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar turno: " + e.getMessage());
        }

        return null;
    }

    // ==========================================
    // LISTAR TURNOS POR DOCTOR
    // ==========================================
    public List<ClaseTurno> listarPorDoctor(int idDoctor) {
        List<ClaseTurno> turnos = new ArrayList<>();

        String sql = """
                SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno
                FROM turnos
                WHERE id_doctor = ?
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idDoctor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClaseTurno turno = new ClaseTurno(
                            rs.getInt("id_turno"),
                            rs.getInt("id_paciente"),
                            rs.getInt("id_doctor"),
                            rs.getDate("fecha_turno").toLocalDate(),
                            rs.getTime("hora_turno").toLocalTime()
                    );
                    turnos.add(turno);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar turnos por doctor: " + e.getMessage());
        }

        return turnos;
    }

    // ==========================================
    // LISTAR TURNOS POR PACIENTE
    // ==========================================
    public List<ClaseTurno> listarPorPaciente(int idPaciente) {
        List<ClaseTurno> turnos = new ArrayList<>();

        String sql = """
                SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno
                FROM turnos
                WHERE id_paciente = ?
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClaseTurno turno = new ClaseTurno(
                            rs.getInt("id_turno"),
                            rs.getInt("id_paciente"),
                            rs.getInt("id_doctor"),
                            rs.getDate("fecha_turno").toLocalDate(),
                            rs.getTime("hora_turno").toLocalTime()
                    );
                    turnos.add(turno);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar turnos por paciente: " + e.getMessage());
        }

        return turnos;
    }

    // ==========================================
    // MODIFICAR TURNO
    // ==========================================
    public boolean modificar(ClaseTurno turno) {
        String sql = """
                UPDATE turnos
                SET id_paciente = ?,
                    id_doctor = ?,
                    fecha_turno = ?,
                    hora_turno = ?
                WHERE id_turno = ?
                """;

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, turno.idPaciente());
            ps.setInt(2, turno.idDoctor());
            ps.setString(3, turno.fechaTurno().toString());
            ps.setString(4, turno.horaTurno().toString());
            ps.setInt(5, turno.idTurno()); // Corregido el índice a 5

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar turno: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // ELIMINAR TURNO
    // ==========================================
    public boolean eliminar(int idTurno) {
        String sql = "DELETE FROM turnos WHERE id_turno = ?";

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idTurno);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar turno: " + e.getMessage());
            return false;
        }
    }
}