package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClaseDoctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOImpl {

    public List<ClaseDoctor> listarTodos() throws SQLException {
        List<ClaseDoctor> doctores = new ArrayList<>();
        String sql = "SELECT id_doctor, nombre, apellido, especialidad FROM doctores";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                doctores.add(new ClaseDoctor(
                        rs.getInt("id_doctor"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("especialidad")
                ));
            }
        }
        return doctores;
    }

    public void insertar(ClaseDoctor doctor, List<Integer> obrasSeleccionadas) throws SQLException {
        String sql = "INSERT INTO doctores (nombre, apellido, especialidad) VALUES (?, ?, ?)";
        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, doctor.nombre());
            stmt.setString(2, doctor.apellido());
            stmt.setString(3, doctor.especialidad());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    doctor.setIdDoctor(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void actualizar(ClaseDoctor doctor, List<Integer> obrasSeleccionadas) throws SQLException {
        String sql = "UPDATE doctores SET nombre = ?, apellido = ?, especialidad = ? WHERE id_doctor = ?";
        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.nombre());
            stmt.setString(2, doctor.apellido());
            stmt.setString(3, doctor.especialidad());
            stmt.setInt(4, doctor.idDoctor());
            stmt.executeUpdate();
        }
    }

    public void eliminar(int idDoctor) throws SQLException {
        String sql = "DELETE FROM doctores WHERE id_doctor = ?";
        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDoctor);
            stmt.executeUpdate();
        }
    }

    public ClaseDoctor buscarPorId(int idDoctor) {
        // CORREGIDO: Se agregó 'especialidad' a la consulta SQL
        String sql = "SELECT id_doctor, nombre, apellido, especialidad FROM doctores WHERE id_doctor = ?";

        try (Connection conexion = BaseDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idDoctor);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ClaseDoctor(
                            rs.getInt("id_doctor"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("especialidad")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar doctor por ID: " + e.getMessage());
        }

        return null; // Retorna null si no encuentra el doctor
    }

    public String obtenerNombresObrasSociales(int i) {
        return "";
    }

    public List<Integer> listarIdsObrasSocialesPorDoctor(int id) {
        return List.of();
    }
}