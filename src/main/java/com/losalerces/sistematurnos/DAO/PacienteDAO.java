package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClasePaciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public boolean guardar(ClasePaciente paciente) {
        String sql = "INSERT INTO pacientes (nombre, apellido, fecha_nacimiento, telefono, email, id_obra_social) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, paciente.nombre());
            pstmt.setString(2, paciente.apellido());
            pstmt.setString(3, paciente.fechaNacimiento()); // O .toString() si manejas LocalDate
            pstmt.setString(4, paciente.telefono());
            pstmt.setString(5, paciente.email());
            pstmt.setInt(6, paciente.idObraSocial());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
            return false;
        }
    }

    // Alias por si prefieres usar "agregar" en tus pruebas
    public boolean agregar(ClasePaciente paciente) {
        return guardar(paciente);
    }

    public List<ClasePaciente> listar() {
        List<ClasePaciente> lista = new ArrayList<>();
        String sql = "SELECT id_paciente, nombre, apellido, fecha_nacimiento, telefono, email, id_obra_social FROM pacientes";
        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mappearPaciente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los pacientes: " + e.getMessage());
        }
        return lista;
    }

    public ClasePaciente buscarPorId(int idPaciente) {
        String sql = "SELECT id_paciente, nombre, apellido, fecha_nacimiento, telefono, email, id_obra_social FROM pacientes WHERE id_paciente = ?";
        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPaciente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mappearPaciente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(ClasePaciente paciente) {
        String sql = "UPDATE pacientes SET nombre = ?, apellido = ?, fecha_nacimiento = ?, telefono = ?, email = ?, id_obra_social = ? WHERE id_paciente = ?";
        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, paciente.nombre());
            pstmt.setString(2, paciente.apellido());
            pstmt.setString(3, paciente.fechaNacimiento());
            pstmt.setString(4, paciente.telefono());
            pstmt.setString(5, paciente.email());
            pstmt.setInt(6, paciente.idObraSocial());
            pstmt.setInt(7, paciente.idPaciente());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idPaciente) {
        String sql = "DELETE FROM pacientes WHERE id_paciente = ?";
        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPaciente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar paciente: " + e.getMessage());
            return false;
        }
    }

    private ClasePaciente mappearPaciente(ResultSet rs) throws SQLException {
        return new ClasePaciente(
                rs.getInt("id_paciente"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("fecha_nacimiento"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getInt("id_obra_social")
        );
    }
}