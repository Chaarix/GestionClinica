package com.losalerces.sistematurnos.Controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class controladorTurnos {

    @FXML
    private DatePicker dpFecha;

    @FXML
    private ComboBox<String> cmbDoctor;

    @FXML
    private ComboBox<String> cmbHorario;

    @FXML
    private ComboBox<String> cmbPaciente;

    @FXML
    private Label lblEspecialidad;

    @FXML
    private Label lblObraSocial;

    @FXML
    private Label lblFechaAgenda;

    @FXML
    private TableView<?> tablaTurnos;

    @FXML
    private TableColumn<?, ?> colHora;

    @FXML
    private TableColumn<?, ?> colPaciente;

    @FXML
    private TableColumn<?, ?> colDoctor;

    @FXML
    private TableColumn<?, ?> colEspecialidad;

    @FXML
    private TableColumn<?, ?> colObraSocial;

    @FXML
    private TableColumn<?, ?> colEstado;

    @FXML
    private TableColumn<?, ?> colAcciones;


    @FXML
    public void initialize() {

        dpFecha.setValue(LocalDate.now());

        actualizarFechaAgenda();

        cargarDoctores();

        cargarPacientes();

        System.out.println("Vista Turnos cargada");
    }


    private void cargarDoctores() {

        // TEMPORAL
        // Después esto viene de la BD

        cmbDoctor.getItems().addAll(
                "Dr. López",
                "Dra. Pérez",
                "Dra. Fernández"
        );
    }


    private void cargarPacientes() {

        // TEMPORAL
        // Después esto viene de la BD

        cmbPaciente.getItems().addAll(
                "Ana Ruiz",
                "Carlos Méndez",
                "Lucía Martínez"
        );
    }


    @FXML
    private void cargarHorarios() {

        cmbHorario.getItems().clear();

        cmbHorario.getItems().addAll(
                "08:00",
                "08:30",
                "09:00",
                "09:30",
                "10:00",
                "10:30",
                "11:00",
                "11:30"
        );

        actualizarDatosDoctor();
    }


    private void actualizarDatosDoctor() {

        String doctor =
                cmbDoctor.getValue();

        if (doctor == null) {
            return;
        }

        switch (doctor) {

            case "Dr. López" -> {
                lblEspecialidad.setText("Clínica Médica");
                lblObraSocial.setText("PAMI / OSDE");
            }

            case "Dra. Pérez" -> {
                lblEspecialidad.setText("Cardiología");
                lblObraSocial.setText("PAMI");
            }

            case "Dra. Fernández" -> {
                lblEspecialidad.setText("Pediatría");
                lblObraSocial.setText("OSDE");
            }
        }
    }


    @FXML
    private void guardarTurno() {

        if (dpFecha.getValue() == null ||
                cmbDoctor.getValue() == null ||
                cmbHorario.getValue() == null ||
                cmbPaciente.getValue() == null) {

            mostrarAlerta(
                    "Datos incompletos",
                    "Completá todos los campos del turno."
            );

            return;
        }

        System.out.println("Guardando turno...");

        System.out.println(
                dpFecha.getValue()
                        + " | "
                        + cmbHorario.getValue()
                        + " | "
                        + cmbPaciente.getValue()
                        + " | "
                        + cmbDoctor.getValue()
        );

        // Después acá insertamos en SQLite
    }


    @FXML
    private void limpiarFormulario() {

        dpFecha.setValue(LocalDate.now());

        cmbDoctor.setValue(null);

        cmbHorario.getItems().clear();

        cmbPaciente.setValue(null);

        lblEspecialidad.setText("-");

        lblObraSocial.setText("-");
    }


    @FXML
    private void actualizarTurnos() {

        System.out.println("Actualizar turnos");

        // Después hacemos SELECT desde SQLite
    }


    @FXML
    private void diaAnterior() {

        dpFecha.setValue(
                dpFecha.getValue().minusDays(1)
        );

        actualizarFechaAgenda();
    }


    @FXML
    private void diaSiguiente() {

        dpFecha.setValue(
                dpFecha.getValue().plusDays(1)
        );

        actualizarFechaAgenda();
    }


    @FXML
    private void irHoy() {

        dpFecha.setValue(
                LocalDate.now()
        );

        actualizarFechaAgenda();
    }


    private void actualizarFechaAgenda() {

        if (dpFecha.getValue() != null) {

            lblFechaAgenda.setText(
                    dpFecha.getValue().toString()
            );
        }
    }


    private void mostrarAlerta(
            String titulo,
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alerta.setTitle(titulo);

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}