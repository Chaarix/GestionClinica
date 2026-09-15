package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClasePaciente;
import com.losalerces.sistematurnos.Clases.ClaseTurno;
import com.losalerces.sistematurnos.DAO.DoctorDAOImpl;
import com.losalerces.sistematurnos.DAO.PacienteDAO;
import com.losalerces.sistematurnos.DAO.TurnoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class controladorReportePami {

    @FXML
    private TableView<FilaReportePami> tablaReportePami;

    @FXML
    private TableColumn<FilaReportePami, String> colFecha;

    @FXML
    private TableColumn<FilaReportePami, String> colHora;

    @FXML
    private TableColumn<FilaReportePami, String> colPaciente;

    @FXML
    private TableColumn<FilaReportePami, String> colDoctor;

    @FXML
    private TableColumn<FilaReportePami, String> colEstado;

    private final ObservableList<FilaReportePami> listaReporte = FXCollections.observableArrayList();
    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final DoctorDAOImpl doctorDAO = new DoctorDAOImpl();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatosReporte();
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFecha()));
        colHora.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getHora()));
        colPaciente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPaciente()));
        colDoctor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDoctor()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));

        tablaReportePami.setItems(listaReporte);
    }

    @FXML
    private void actualizarReporte() {
        cargarDatosReporte();
    }

    private void cargarDatosReporte() {
        listaReporte.clear();
        System.out.println(">>> Intentando cargar reporte PAMI...");

        List<ClaseTurno> turnosPamiAtendidos = turnoDAO.listarPorObraSocialYEstado("PAMI", "Atendido");

        System.out.println(">>> Turnos encontrados en la BD: " + turnosPamiAtendidos.size());

        for (ClaseTurno t : turnosPamiAtendidos) {
            ClasePaciente pac = pacienteDAO.buscarPorId(t.idPaciente());
            String nombrePac = pac != null ? pac.nombre() + " " + pac.apellido() : "Desconocido";

            // Depuración de doctor
            System.out.println(">>> Procesando turno ID: " + t.idTurno() + " | idDoctor asociado en BD: " + t.idDoctor());

            String nombreDoc = "Desconocido";
            try {
                var doc = doctorDAO.buscarPorId(t.idDoctor());
                if (doc != null) {
                    nombreDoc = doc.nombre() + " " + doc.apellido();
                    System.out.println("    -> Doctor encontrado: " + nombreDoc);
                } else {
                    System.out.println("    -> ALERTA: doctorDAO.buscarPorId(" + t.idDoctor() + ") devolvió NULL.");
                }
            } catch (Exception e) {
                System.out.println("    -> EXCEPCIÓN al buscar doctor: " + e.getMessage());
            }

            listaReporte.add(new FilaReportePami(
                    t.fechaTurno() != null ? t.fechaTurno().toString() : "",
                    t.horaTurno() != null ? t.horaTurno().toString() : "",
                    nombrePac,
                    nombreDoc,
                    t.estado()
            ));
        }
        System.out.println(">>> Filas añadidas a la tabla: " + listaReporte.size());
    }

    public static class FilaReportePami {
        private final String fecha;
        private final String hora;
        private final String paciente;
        private final String doctor;
        private final String estado;

        public FilaReportePami(String fecha, String hora, String paciente, String doctor, String estado) {
            this.fecha = fecha;
            this.hora = hora;
            this.paciente = paciente;
            this.doctor = doctor;
            this.estado = estado;
        }

        public String getFecha() { return fecha; }
        public String getHora() { return hora; }
        public String getPaciente() { return paciente; }
        public String getDoctor() { return doctor; }
        public String getEstado() { return estado; }
    }
}