package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClaseTurno;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

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
            ps.setString(3, turno.fechaTurno() != null ? turno.fechaTurno().toString() : "");
            ps.setString(4, turno.horaTurno() != null ? turno.horaTurno().toString() : "");

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al agregar turno: " + e.getMessage());
            return false;
        }
    }

    public List<ClaseTurno> listar() {
        List<ClaseTurno> turnos = new ArrayList<>();
        String sql = "SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno FROM turnos";

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                turnos.add(mappearTurno(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar turnos: " + e.getMessage());
        }

        return turnos;
    }

    public ClaseTurno buscarPorId(int idTurno) {
        String sql = "SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno FROM turnos WHERE id_turno = ?";

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idTurno);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mappearTurno(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar turno: " + e.getMessage());
        }

        return null;
    }

    public List<ClaseTurno> listarPorDoctor(int idDoctor) {
        List<ClaseTurno> turnos = new ArrayList<>();
        String sql = "SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno FROM turnos WHERE id_doctor = ?";

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idDoctor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mappearTurno(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar turnos por doctor: " + e.getMessage());
        }

        return turnos;
    }

    public List<ClaseTurno> listarPorPaciente(int idPaciente) {
        List<ClaseTurno> turnos = new ArrayList<>();
        String sql = "SELECT id_turno, id_paciente, id_doctor, fecha_turno, hora_turno FROM turnos WHERE id_paciente = ?";

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mappearTurno(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar turnos por paciente: " + e.getMessage());
        }

        return turnos;
    }

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
            ps.setString(3, turno.fechaTurno() != null ? turno.fechaTurno().toString() : "");
            ps.setString(4, turno.horaTurno() != null ? turno.horaTurno().toString() : "");
            ps.setInt(5, turno.idTurno());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar turno: " + e.getMessage());
            return false;
        }
    }

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

    // Método auxiliar seguro para evitar errores de parseo con SQLite
    private ClaseTurno mappearTurno(ResultSet rs) throws SQLException {
        String fechaStr = rs.getString("fecha_turno");
        String horaStr = rs.getString("hora_turno");

        LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty()) ? LocalDate.parse(fechaStr) : null;
        LocalTime hora = (horaStr != null && !horaStr.isEmpty()) ? LocalTime.parse(horaStr) : null;

        return new ClaseTurno(
                rs.getInt("id_turno"),
                rs.getInt("id_paciente"),
                rs.getInt("id_doctor"),
                fecha,
                hora
        );
    }
}