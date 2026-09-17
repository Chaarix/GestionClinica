package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClasePaciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public boolean guardar(ClasePaciente paciente) {

        String sql = """
                INSERT INTO pacientes
                (nombre, apellido, fecha_nacimiento, telefono, email, id_obra_social, dni)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paciente.nombre());
            pstmt.setString(2, paciente.apellido());
            pstmt.setString(3, paciente.fechaNacimiento());
            pstmt.setString(4, paciente.telefono());
            pstmt.setString(5, paciente.email());
            pstmt.setInt(6, paciente.idObraSocial());
            pstmt.setString(7, paciente.dni());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
            return false;
        }
    }

    public boolean agregar(ClasePaciente paciente) {
        return guardar(paciente);
    }

    public List<ClasePaciente> listar() {

        List<ClasePaciente> lista = new ArrayList<>();

        String sql = """
                SELECT id_paciente,
                       nombre,
                       apellido,
                       fecha_nacimiento,
                       telefono,
                       email,
                       id_obra_social,
                       dni
                FROM pacientes
                """;

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

        String sql = """
                SELECT id_paciente,
                       nombre,
                       apellido,
                       fecha_nacimiento,
                       telefono,
                       email,
                       id_obra_social,
                       dni
                FROM pacientes
                WHERE id_paciente = ?
                """;

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

        String sql = """
                UPDATE pacientes
                SET nombre = ?,
                    apellido = ?,
                    fecha_nacimiento = ?,
                    telefono = ?,
                    email = ?,
                    id_obra_social = ?,
                    dni = ?
                WHERE id_paciente = ?
                """;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paciente.nombre());
            pstmt.setString(2, paciente.apellido());
            pstmt.setString(3, paciente.fechaNacimiento());
            pstmt.setString(4, paciente.telefono());
            pstmt.setString(5, paciente.email());
            pstmt.setInt(6, paciente.idObraSocial());
            pstmt.setString(7, paciente.dni());
            pstmt.setInt(8, paciente.idPaciente());

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
                rs.getInt("id_obra_social"),
                rs.getString("dni")
        );
    }

    public List<ClasePaciente> listarPacientesPamiConTurnosCumplidos() {

        List<ClasePaciente> lista = new ArrayList<>();

        String sql = """
                SELECT DISTINCT
                       p.id_paciente,
                       p.nombre,
                       p.apellido,
                       p.fecha_nacimiento,
                       p.telefono,
                       p.email,
                       p.id_obra_social,
                       p.dni
                FROM pacientes p
                JOIN obra_social os
                  ON p.id_obra_social = os.id_obra_social
                JOIN turnos t
                  ON p.id_paciente = t.id_paciente
                WHERE UPPER(os.nombre) LIKE '%PAMI%'
                  AND t.fecha_turno <= DATE('now')
                """;

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                lista.add(
                        new ClasePaciente(
                                rs.getInt("id_paciente"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("fecha_nacimiento"),
                                rs.getString("telefono"),
                                rs.getString("email"),
                                rs.getInt("id_obra_social"),
                                rs.getString("dni")
                        )
                );
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al listar pacientes PAMI con turnos cumplidos: "
                            + e.getMessage()
            );
        }

        return lista;
    }

    // Verifica si el DNI ya existe al guardar un paciente nuevo
    public boolean existeDni(String dni) {

        String sql = "SELECT 1 FROM pacientes WHERE dni = ? LIMIT 1";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar DNI: " + e.getMessage());
            return false;
        }
    }

    // Verifica si el DNI pertenece a OTRO paciente al modificar
    public boolean existeDniEnOtroPaciente(String dni, int idPaciente) {

        String sql = """
                SELECT 1
                FROM pacientes
                WHERE dni = ?
                  AND id_paciente <> ?
                LIMIT 1
                """;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);
            pstmt.setInt(2, idPaciente);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al verificar DNI en otro paciente: "
                            + e.getMessage()
            );
            return false;
        }
    }
}