package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClaseDoctor;
import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.Clases.ClasePaciente;
import com.losalerces.sistematurnos.Clases.ClaseTurno;
import com.losalerces.sistematurnos.DAO.DoctorDAOImpl;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;
import com.losalerces.sistematurnos.DAO.PacienteDAO;
import com.losalerces.sistematurnos.DAO.TurnoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TableView<TurnoFila> tablaTurnos;

    @FXML
    private TableColumn<TurnoFila, String> colHora;

    @FXML
    private TableColumn<TurnoFila, String> colPaciente;

    @FXML
    private TableColumn<TurnoFila, String> colDoctor;

    @FXML
    private TableColumn<TurnoFila, String> colEspecialidad;

    @FXML
    private TableColumn<TurnoFila, String> colObraSocial;

    @FXML
    private TableColumn<TurnoFila, String> colEstado;

    @FXML
    private TableColumn<TurnoFila, Void> colAcciones;

    private final ObservableList<TurnoFila> listaTurnos = FXCollections.observableArrayList();

    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final ObraSocialDAO obraSocialDAO = new ObraSocialDAO();
    private final DoctorDAOImpl doctorDAO = new DoctorDAOImpl(); // Instancia del DAO de doctores provisto

    // Mapas para pacientes
    private final Map<String, Integer> mapaPacientes = new HashMap<>();

    // Mapas para traducir Obras Sociales de ID a Nombre de forma dinámica
    private final Map<Integer, String> mapaObrasSocialesIdANombre = new HashMap<>();

    // Mapas dinámicos para doctores basados en los datos reales de la BD
    private final Map<String, Integer> mapaDoctoresNombreAId = new HashMap<>();
    private final Map<Integer, String> mapaDoctoresIdANombre = new HashMap<>();
    private final Map<String, String> mapaDoctoresEspecialidad = new HashMap<>();

    @FXML
    public void initialize() {
        dpFecha.setValue(LocalDate.now());
        actualizarFechaAgenda();

        // Configuración de seguridad para el DatePicker
        dpFecha.setConverter(new javafx.util.converter.LocalDateStringConverter() {
            @Override
            public LocalDate fromString(String text) {
                try {
                    if (text == null || text.trim().isEmpty()) {
                        return null;
                    }
                    return super.fromString(text);
                } catch (Exception e) {
                    return null;
                }
            }
        });

        configurarTabla();
        cargarMapaObrasSociales();
        cargarDoctoresDesdeBD(); // Carga los doctores reales usando DoctorDAOImpl
        cargarPacientes();
        cargarTurnosDesdeBD();

        // Listener para actualizar los turnos si cambia la fecha
        dpFecha.valueProperty().addListener((obs, viejo, nuevo) -> {
            actualizarFechaAgenda();
            cargarTurnosDesdeBD();
        });
    }

    private void configurarTabla() {
        colHora.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getHoraTexto()));
        colPaciente.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getNombrePaciente()));
        colDoctor.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getNombreDoctor()));
        colEspecialidad.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getEspecialidad()));
        colObraSocial.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getObraSocial()));
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getEstado()));

        configurarAcciones();
        tablaTurnos.setItems(listaTurnos);
    }

    private void cargarMapaObrasSociales() {
        mapaObrasSocialesIdANombre.clear();
        List<ClaseObraSocial> listaBD = obraSocialDAO.listarTodos();
        for (ClaseObraSocial os : listaBD) {
            mapaObrasSocialesIdANombre.put(os.idObraSocial(), os.nombre());
        }
    }

    private void cargarDoctoresDesdeBD() {
        cmbDoctor.getItems().clear();
        mapaDoctoresNombreAId.clear();
        mapaDoctoresIdANombre.clear();
        mapaDoctoresEspecialidad.clear();

        try {
            List<ClaseDoctor> doctoresBD = doctorDAO.listarTodos();
            for (ClaseDoctor d : doctoresBD) {
                String nombreCompleto = "Dr/a. " + d.nombre() + " " + d.apellido(); // O puedes usar solo d.nombre() segun prefieras
                // Nota: Asegúrate de alinear el formato del nombre si en tu BD viene separado o completo.
                // Si 'd.nombre()' ya incluye el título, úsalo directo. Aquí asumimos formato estándar:
                String nombreFormateado = d.nombre() + " " + d.apellido();

                cmbDoctor.getItems().add(nombreFormateado);
                mapaDoctoresNombreAId.put(nombreFormateado, d.idDoctor());
                mapaDoctoresIdANombre.put(d.idDoctor(), nombreFormateado);
                mapaDoctoresEspecialidad.put(nombreFormateado, d.especialidad());
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "No se pudieron cargar los doctores desde la base de datos: " + e.getMessage());
        }
    }

    private void cargarPacientes() {
        cmbPaciente.getItems().clear();
        mapaPacientes.clear();

        List<ClasePaciente> pacientesDB = pacienteDAO.listar();
        for (ClasePaciente p : pacientesDB) {
            String nombreCompleto = p.nombre() + " " + p.apellido();
            cmbPaciente.getItems().add(nombreCompleto);
            mapaPacientes.put(nombreCompleto, p.idPaciente());
        }
    }

    @FXML
    private void cargarHorarios() {
        cmbHorario.getItems().clear();

        cmbHorario.getItems().addAll(
                "08:00", "08:30", "09:00", "09:30",
                "10:00", "10:30", "11:00", "11:30",
                "14:00", "14:30", "15:00", "15:30"
        );

        actualizarDatosDoctor();
    }

    private void actualizarDatosDoctor() {
        String doctorSeleccionado = cmbDoctor.getValue();
        if (doctorSeleccionado == null) {
            return;
        }

        String especialidad = mapaDoctoresEspecialidad.getOrDefault(doctorSeleccionado, "General");
        lblEspecialidad.setText(especialidad);

        // Opcional o genérico para las obras sociales que atiende el doctor
        lblObraSocial.setText("Varias / Particular");
    }

    @FXML
    private void guardarTurno() {
        if (dpFecha.getValue() == null || cmbDoctor.getValue() == null || cmbHorario.getValue() == null || cmbPaciente.getValue() == null) {
            mostrarAlerta("Datos incompletos", "Completá todos los campos del turno.");
            return;
        }

        int idPaciente = mapaPacientes.getOrDefault(cmbPaciente.getValue(), 0);
        int idDoctor = mapaDoctoresNombreAId.getOrDefault(cmbDoctor.getValue(), 0);

        if (idPaciente == 0 || idDoctor == 0) {
            mostrarAlerta("Error", "Debe seleccionar un paciente y un doctor válidos.");
            return;
        }

        ClaseTurno nuevoTurno = new ClaseTurno(
                0,
                idPaciente,
                idDoctor,
                dpFecha.getValue(),
                LocalTime.parse(cmbHorario.getValue())
        );

        boolean exito = turnoDAO.agregar(nuevoTurno);

        if (exito) {
            cargarTurnosDesdeBD();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo guardar el turno en la base de datos.");
        }
    }

    @FXML
    private void actualizarTurnos() {
        cargarTurnosDesdeBD();
    }

    private void cargarTurnosDesdeBD() {
        listaTurnos.clear();
        List<ClaseTurno> turnosDB = turnoDAO.listar();
        LocalDate fechaSeleccionada = dpFecha.getValue();

        for (ClaseTurno t : turnosDB) {
            if (fechaSeleccionada != null && !t.fechaTurno().equals(fechaSeleccionada)) {
                continue;
            }

            ClasePaciente pac = pacienteDAO.buscarPorId(t.idPaciente());
            String nombrePac = pac != null ? pac.nombre() + " " + pac.apellido() : "Desconocido";

            String nombreObraSocial = "Particular";
            if (pac != null) {
                nombreObraSocial = mapaObrasSocialesIdANombre.getOrDefault(pac.idObraSocial(), "Particular");
            }

            String nombreDoc = mapaDoctoresIdANombre.getOrDefault(t.idDoctor(), "Desconocido");
            String espDoc = mapaDoctoresEspecialidad.getOrDefault(nombreDoc, "General");

            listaTurnos.add(new TurnoFila(
                    t.idTurno(),
                    t.idPaciente(),
                    t.idDoctor(),
                    t.horaTurno(),
                    t.fechaTurno(),
                    nombrePac,
                    nombreDoc,
                    espDoc,
                    nombreObraSocial,
                    "Confirmado"
            ));
        }
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(columna -> new TableCell<>() {
            private final Button btnEliminar = new Button("Cancelar");

            {
                btnEliminar.getStyleClass().add("boton-eliminar");
                btnEliminar.setOnAction(event -> {
                    TurnoFila turno = getTableView().getItems().get(getIndex());
                    eliminarTurno(turno.getIdTurno());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });
    }

    private void eliminarTurno(int idTurno) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseás cancelar/eliminar este turno?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                if (turnoDAO.eliminar(idTurno)) {
                    cargarTurnosDesdeBD();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el turno.");
                }
            }
        });
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
    private void diaAnterior() {
        dpFecha.setValue(dpFecha.getValue().minusDays(1));
        actualizarFechaAgenda();
    }

    @FXML
    private void diaSiguiente() {
        dpFecha.setValue(dpFecha.getValue().plusDays(1));
        actualizarFechaAgenda();
    }

    @FXML
    private void irHoy() {
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

    public static class TurnoFila {
        private final int idTurno;
        private final int idPaciente;
        private final int idDoctor;
        private final LocalTime horaTurno;
        private final LocalDate fechaTurno;
        private final String nombrePaciente;
        private final String nombreDoctor;
        private final String especialidad;
        private final String obraSocial;
        private final String estado;

        public TurnoFila(int idTurno, int idPaciente, int idDoctor, LocalTime horaTurno, LocalDate fechaTurno,
                         String nombrePaciente, String nombreDoctor, String especialidad, String obraSocial, String estado) {
            this.idTurno = idTurno;
            this.idPaciente = idPaciente;
            this.idDoctor = idDoctor;
            this.horaTurno = horaTurno;
            this.fechaTurno = fechaTurno;
            this.nombrePaciente = nombrePaciente;
            this.nombreDoctor = nombreDoctor;
            this.especialidad = especialidad;
            this.obraSocial = obraSocial;
            this.estado = estado;
        }

        public int getIdTurno() { return idTurno; }
        public int getIdPaciente() { return idPaciente; }
        public int getIdDoctor() { return idDoctor; }
        public LocalTime getHoraTurno() { return horaTurno; }
        public String getHoraTexto() { return horaTurno != null ? horaTurno.toString() : ""; }
        public LocalDate getFechaTurno() { return fechaTurno; }
        public String getNombrePaciente() { return nombrePaciente; }
        public String getNombreDoctor() { return nombreDoctor; }
        public String getEspecialidad() { return especialidad; }
        public String getObraSocial() { return obraSocial; }
        public String getEstado() { return estado; }
    }
}