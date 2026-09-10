package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.Clases.ClaseDoctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOImpl extends DoctorDAO {

    // Reemplaza esto con tu clase de conexión real (ej: Conexion.getConexion())
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/turnos_db", "usuario", "password");
    }

    @Override
    public List<ClaseDoctor> listarTodos() throws SQLException {
        List<ClaseDoctor> doctores = new ArrayList<>();
        String sql = "SELECT id_doctor, nombre, apellido, dni, telefono, correo, especialidad FROM doctores";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                doctores.add(new ClaseDoctor(
                        rs.getInt("id_doctor"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("especialidad")
                ));
            }
        }
        return doctores;
    }

    @Override
    public void insertar(ClaseDoctor doctor) throws SQLException {
        String sql = "INSERT INTO doctores (nombre, apellido, dni, telefono, correo, especialidad) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, doctor.nombre());
            stmt.setString(2, doctor.apellido());
            stmt.setString(3, doctor.dni());
            stmt.setString(4, doctor.telefono());
            stmt.setString(5, doctor.correo());
            stmt.setString(6, doctor.especialidad());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    doctor.setIdDoctor(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(ClaseDoctor doctor) throws SQLException {
        String sql = "UPDATE doctores SET nombre = ?, apellido = ?, dni = ?, telefono = ?, correo = ?, especialidad = ? WHERE id_doctor = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.nombre());
            stmt.setString(2, doctor.apellido());
            stmt.setString(3, doctor.dni());
            stmt.setString(4, doctor.telefono());
            stmt.setString(5, doctor.correo());
            stmt.setString(6, doctor.especialidad());
            stmt.setInt(7, doctor.idDoctor());
            stmt.executeUpdate();
        }
    }

   @Override
    public void eliminar(int idDoctor) throws SQLException {
        String sql = "DELETE FROM doctores WHERE id_doctor = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDoctor);
            stmt.executeUpdate();
        }
    }

}
