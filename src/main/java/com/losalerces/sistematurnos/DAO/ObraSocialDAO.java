package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.BD.BaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObraSocialDAO {


    // 1. Método para insertar una obra social nueva (Solo Nombre)
    public boolean insertar(ClaseObraSocial obraSocial) {
        String sql = "INSERT INTO obra_social (nombre) VALUES (?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, obraSocial.nombre());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar obra social: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 2. Método para listar todas las obras sociales
    public List<ClaseObraSocial> listarTodos() {
        List<ClaseObraSocial> lista = new ArrayList<>();
        String sql = "SELECT * FROM obra_social";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Pasamos strings vacíos para el teléfono y dirección ya que no se usan en la BD
                ClaseObraSocial obraSocial = new ClaseObraSocial(
                        rs.getInt("id_obra_social"),
                        rs.getString("nombre")
                );
                lista.add(obraSocial);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar obras sociales: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // 3. Método para modificar el nombre de una obra social existente
    public boolean modificar(ClaseObraSocial obraSocial) {
        String sql = "UPDATE obra_social SET nombre = ? WHERE id_obra_social = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, obraSocial.nombre());
            pstmt.setInt(2, obraSocial.idObraSocial());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar obra social: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 4. Método para eliminar una obra social por su ID
    public boolean eliminar(int idObraSocial) {
        String sql = "DELETE FROM obra_social WHERE id_obra_social = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idObraSocial);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar obra social: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}