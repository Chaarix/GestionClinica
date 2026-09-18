package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.DAO.TurnoDAO;
import javafx.event.ActionEvent; // IMPORTE CLAVE PARA LOS EVENTOS DE BOTONES
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class controladorTurnos {

    private final TurnoDAO turnoDAO = new TurnoDAO();

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

        // =============================================================
        // 🔒 BLOQUEO DE CALENDARIO: De hoy en adelante
        // =============================================================
        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Si la fecha es anterior a hoy (pasado), se deshabilita y se pinta de gris
                if (date != null && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #aaaaaa;");
                }
            }
        });
        // =============================================================

        actualizarFechaAgenda();
        cargarDoctores();
        cargarPacientes();
        System.out.println("Vista Turnos cargada");
    }

    private void cargarDoctores() {
        cmbDoctor.getItems().addAll(
                "Dr. López",
                "Dra. Pérez",
                "Dra. Fernández"
        );
    }

    private void cargarPacientes() {
        cmbPaciente.getItems().addAll(
                "Ana Ruiz",
                "Carlos Méndez",
                "Lucía Martínez"
        );
    }

    @FXML
    private void cargarHorarios(ActionEvent event) {
        cmbHorario.getItems().clear();
        cmbHorario.getItems().addAll(
                "08:00", "08:30", "09:00", "09:30",
                "10:00", "10:30", "11:00", "11:30"
        );
        actualizarDatosDoctor();
    }

    private void actualizarDatosDoctor() {
        String doctor = cmbDoctor.getValue();
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
    private void guardarTurno(ActionEvent event) {
        if (dpFecha.getValue() == null ||
                cmbDoctor.getValue() == null ||
                cmbHorario.getValue() == null ||
                cmbPaciente.getValue() == null) {

            mostrarAlerta("Datos incompletos", "Completá todos los campos del turno.");
            return;
        }

        // Validación extra de seguridad por si escriben la fecha a mano
        if (dpFecha.getValue().isBefore(LocalDate.now())) {
            mostrarAlerta("Fecha inválida", "No se pueden registrar turnos en días pasados.");
            return;
        }

        System.out.println("Guardando turno...");
        System.out.println(
                dpFecha.getValue() + " | " + cmbHorario.getValue() + " | " + cmbPaciente.getValue() + " | " + cmbDoctor.getValue()
        );
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        dpFecha.setValue(LocalDate.now());
        cmbDoctor.setValue(null);
        cmbHorario.getItems().clear();
        cmbPaciente.setValue(null);
        lblEspecialidad.setText("-");
        lblObraSocial.setText("-");
    }

    @FXML
    private void actualizarTurnos(ActionEvent event) {
        System.out.println("Actualizar turnos");
    }

    // Permite retroceder el día de la agenda, pero frena si intenta ir al pasado
    @FXML
    private void diaAnterior(ActionEvent event) {
        LocalDate fechaActualSeleccionada = dpFecha.getValue();

        if (fechaActualSeleccionada != null && fechaActualSeleccionada.isAfter(LocalDate.now())) {
            dpFecha.setValue(fechaActualSeleccionada.minusDays(1));
            actualizarFechaAgenda();
        } else {
            mostrarAlerta("Atención", "No podés visualizar ni agendar turnos de días pasados.");
        }
    }

    // Permite avanzar libremente hacia cualquier día del futuro
    @FXML
    private void diaSiguiente(ActionEvent event) {
        LocalDate fechaActualSeleccionada = dpFecha.getValue();
        if (fechaActualSeleccionada != null) {
            dpFecha.setValue(fechaActualSeleccionada.plusDays(1));
            actualizarFechaAgenda();
        }
    }

    @FXML
    private void irHoy(ActionEvent event) {
        dpFecha.setValue(LocalDate.now());
        actualizarFechaAgenda();
    }

    private void actualizarFechaAgenda() {
        if (dpFecha.getValue() != null) {
            lblFechaAgenda.setText(dpFecha.getValue().toString());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
