package com.losalerces.sistematurnos.DAO;

import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClaseDoctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOImpl {

    // =========================================
    // LISTAR TODOS LOS DOCTORES
    // =========================================

    public List<ClaseDoctor> listarTodos() throws SQLException {

        List<ClaseDoctor> doctores = new ArrayList<>();

        String sql = """
                SELECT id_doctor,
                       nombre,
                       apellido,
                       especialidad
                FROM doctores
                ORDER BY apellido, nombre
                """;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                doctores.add(
                        new ClaseDoctor(
                                rs.getInt("id_doctor"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("especialidad")
                        )
                );
            }
        }

        return doctores;
    }


    // =========================================
    // INSERTAR DOCTOR + OBRAS SOCIALES
    // =========================================

    public void insertar(
            ClaseDoctor doctor,
            List<Integer> idsObrasSociales
    ) throws SQLException {

        String sqlDoctor = """
                INSERT INTO doctores
                (nombre, apellido, especialidad)
                VALUES (?, ?, ?)
                """;

        Connection conn = BaseDatos.getConnection();

        try {

            conn.setAutoCommit(false);

            try (PreparedStatement stmt =
                         conn.prepareStatement(
                                 sqlDoctor,
                                 Statement.RETURN_GENERATED_KEYS
                         )) {

                stmt.setString(
                        1,
                        doctor.nombre()
                );

                stmt.setString(
                        2,
                        doctor.apellido()
                );

                stmt.setString(
                        3,
                        doctor.especialidad()
                );

                stmt.executeUpdate();


                try (ResultSet generatedKeys =
                             stmt.getGeneratedKeys()) {

                    if (generatedKeys.next()) {

                        doctor.setIdDoctor(
                                generatedKeys.getInt(1)
                        );

                    } else {

                        // Respaldo para SQLite
                        try (Statement ultimoId =
                                     conn.createStatement();

                             ResultSet rs =
                                     ultimoId.executeQuery(
                                             "SELECT last_insert_rowid()"
                                     )) {

                            if (rs.next()) {

                                doctor.setIdDoctor(
                                        rs.getInt(1)
                                );
                            }
                        }
                    }
                }
            }


            guardarObrasSociales(
                    conn,
                    doctor.idDoctor(),
                    idsObrasSociales
            );


            conn.commit();

        } catch (SQLException e) {

            conn.rollback();

            throw e;

        } finally {

            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }

            conn.close();
        }
    }


    // =========================================
    // ACTUALIZAR DOCTOR + OBRAS SOCIALES
    // =========================================

    public void actualizar(
            ClaseDoctor doctor,
            List<Integer> idsObrasSociales
    ) throws SQLException {

        String sqlDoctor = """
                UPDATE doctores
                SET nombre = ?,
                    apellido = ?,
                    especialidad = ?
                WHERE id_doctor = ?
                """;

        Connection conn = BaseDatos.getConnection();

        try {

            conn.setAutoCommit(false);

            try (PreparedStatement stmt =
                         conn.prepareStatement(sqlDoctor)) {

                stmt.setString(
                        1,
                        doctor.nombre()
                );

                stmt.setString(
                        2,
                        doctor.apellido()
                );

                stmt.setString(
                        3,
                        doctor.especialidad()
                );

                stmt.setInt(
                        4,
                        doctor.idDoctor()
                );

                stmt.executeUpdate();
            }


            // Borramos las asociaciones viejas
            eliminarObrasSocialesDoctor(
                    conn,
                    doctor.idDoctor()
            );


            // Guardamos las nuevas
            guardarObrasSociales(
                    conn,
                    doctor.idDoctor(),
                    idsObrasSociales
            );


            conn.commit();

        } catch (SQLException e) {

            conn.rollback();

            throw e;

        } finally {

            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }

            conn.close();
        }
    }


    // =========================================
    // GUARDAR OBRAS SOCIALES DEL DOCTOR
    // =========================================

    private void guardarObrasSociales(
            Connection conn,
            int idDoctor,
            List<Integer> idsObrasSociales
    ) throws SQLException {

        if (idsObrasSociales == null ||
                idsObrasSociales.isEmpty()) {

            return;
        }

        String sql = """
                INSERT INTO doctor_obra_social
                (id_doctor, id_obra_social)
                VALUES (?, ?)
                """;

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            for (Integer idObraSocial :
                    idsObrasSociales) {

                stmt.setInt(
                        1,
                        idDoctor
                );

                stmt.setInt(
                        2,
                        idObraSocial
                );

                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }


    // =========================================
    // BORRAR OBRAS SOCIALES DEL DOCTOR
    // =========================================

    private void eliminarObrasSocialesDoctor(
            Connection conn,
            int idDoctor
    ) throws SQLException {

        String sql = """
                DELETE FROM doctor_obra_social
                WHERE id_doctor = ?
                """;

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idDoctor
            );

            stmt.executeUpdate();
        }
    }


    // =========================================
    // OBTENER IDS DE OBRAS SOCIALES
    // =========================================

    public List<Integer>
    listarIdsObrasSocialesPorDoctor(
            int idDoctor
    ) throws SQLException {

        List<Integer> ids =
                new ArrayList<>();

        String sql = """
                SELECT id_obra_social
                FROM doctor_obra_social
                WHERE id_doctor = ?
                """;

        try (Connection conn =
                     BaseDatos.getConnection();

             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idDoctor
            );

            try (ResultSet rs =
                         stmt.executeQuery()) {

                while (rs.next()) {

                    ids.add(
                            rs.getInt(
                                    "id_obra_social"
                            )
                    );
                }
            }
        }

        return ids;
    }


    // =========================================
    // OBTENER NOMBRES DE OBRAS SOCIALES
    // =========================================

    public String obtenerNombresObrasSociales(
            int idDoctor
    ) throws SQLException {

        List<String> nombres =
                new ArrayList<>();

        String sql = """
                SELECT os.nombre
                FROM doctor_obra_social dos

                INNER JOIN obra_social os
                    ON os.id_obra_social =
                       dos.id_obra_social

                WHERE dos.id_doctor = ?

                ORDER BY os.nombre
                """;

        try (Connection conn =
                     BaseDatos.getConnection();

             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idDoctor
            );

            try (ResultSet rs =
                         stmt.executeQuery()) {

                while (rs.next()) {

                    nombres.add(
                            rs.getString("nombre")
                    );
                }
            }
        }

        return String.join(
                ", ",
                nombres
        );
    }


    // =========================================
    // ELIMINAR DOCTOR
    // =========================================

    public void eliminar(
            int idDoctor
    ) throws SQLException {

        Connection conn =
                BaseDatos.getConnection();

        try {

            conn.setAutoCommit(false);


            // Primero borrar relaciones
            String sqlRelaciones = """
                    DELETE FROM doctor_obra_social
                    WHERE id_doctor = ?
                    """;

            try (PreparedStatement stmt =
                         conn.prepareStatement(
                                 sqlRelaciones
                         )) {

                stmt.setInt(
                        1,
                        idDoctor
                );

                stmt.executeUpdate();
            }


            // Después borrar doctor
            String sqlDoctor = """
                    DELETE FROM doctores
                    WHERE id_doctor = ?
                    """;

            try (PreparedStatement stmt =
                         conn.prepareStatement(
                                 sqlDoctor
                         )) {

                stmt.setInt(
                        1,
                        idDoctor
                );

                stmt.executeUpdate();
            }


            conn.commit();

        } catch (SQLException e) {

            conn.rollback();

            throw e;

        } finally {

            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }

            conn.close();
        }
    }


    // =========================================
    // BUSCAR DOCTOR POR ID
    // =========================================

    public ClaseDoctor buscarPorId(
            int idDoctor
    ) {

        String sql = """
                SELECT id_doctor,
                       nombre,
                       apellido,
                       especialidad

                FROM doctores

                WHERE id_doctor = ?
                """;

        try (Connection conn =
                     BaseDatos.getConnection();

             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idDoctor
            );

            try (ResultSet rs =
                         stmt.executeQuery()) {

                if (rs.next()) {

                    return new ClaseDoctor(
                            rs.getInt(
                                    "id_doctor"
                            ),
                            rs.getString(
                                    "nombre"
                            ),
                            rs.getString(
                                    "apellido"
                            ),
                            rs.getString(
                                    "especialidad"
                            )
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar doctor por ID: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

        return null;
    }
}