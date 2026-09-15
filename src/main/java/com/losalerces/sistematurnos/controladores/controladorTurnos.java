package com.losalerces.sistematurnos.Controladores;

/*
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
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 */
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
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class controladorTurnos {
    /*

    @FXML
    private DatePicker dpFecha;

    @FXML
    private ComboBox<String> cmbDoctor;

    @FXML
    private ComboBox<String> cmbHorario;

    @FXML
    private ComboBox<String> cmbPaciente;

    @FXML
    private ComboBox<String> cmbFiltroEstado; // Opcional para filtrar por estado si lo agregas al FXML

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
    private final DoctorDAOImpl doctorDAO = new DoctorDAOImpl();

    private final Map<String, Integer> mapaPacientes = new HashMap<>();
    private final Map<Integer, String> mapaObrasSocialesIdANombre = new HashMap<>();
    private final Map<String, Integer> mapaDoctoresNombreAId = new HashMap<>();
    private final Map<Integer, String> mapaDoctoresIdANombre = new HashMap<>();
    private final Map<String, String> mapaDoctoresEspecialidad = new HashMap<>();

     */
    @FXML
    private DatePicker dpFecha;

    @FXML
    private ComboBox<String> cmbDoctor;

    @FXML
    private ComboBox<String> cmbHorario;

    @FXML
    private ComboBox<String> cmbPaciente;

    @FXML
    private ComboBox<String> cmbFiltroEstado; // Opcional para filtrar por estado si lo agregas al FXML

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
    private final DoctorDAOImpl doctorDAO = new DoctorDAOImpl();

    private final Map<String, Integer> mapaPacientes = new HashMap<>();
    private final Map<Integer, String> mapaObrasSocialesIdANombre = new HashMap<>();
    private final Map<String, Integer> mapaDoctoresNombreAId = new HashMap<>();
    private final Map<Integer, String> mapaDoctoresIdANombre = new HashMap<>();
    private final Map<String, String> mapaDoctoresEspecialidad = new HashMap<>();

    @FXML
    public void initialize() {
        cmbFiltroEstado.getItems().addAll("Todos", "Pendiente", "Atendido", "Cancelado");

        dpFecha.setValue(LocalDate.now());
        actualizarFechaAgenda();

        dpFecha.setConverter(new javafx.util.converter.LocalDateStringConverter() {
            @Override
            public LocalDate fromString(String text) {
                try {
                    if (text == null || text.trim().isEmpty()) return null;
                    return super.fromString(text);
                } catch (Exception e) {
                    return null;
                }
            }
        });

        configurarTabla();
        cargarMapaObrasSociales();
        cargarDoctoresDesdeBD();
        cargarPacientes();
        cargarTurnosDesdeBD();

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
                String nombreFormateado = d.nombre() + " " + d.apellido();
                cmbDoctor.getItems().add(nombreFormateado);
                mapaDoctoresNombreAId.put(nombreFormateado, d.idDoctor());
                mapaDoctoresIdANombre.put(d.idDoctor(), nombreFormateado);
                mapaDoctoresEspecialidad.put(nombreFormateado, d.especialidad());
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "No se pudieron cargar los doctores: " + e.getMessage());
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
        if (doctorSeleccionado == null) return;

        String especialidad = mapaDoctoresEspecialidad.getOrDefault(doctorSeleccionado, "General");
        lblEspecialidad.setText(especialidad);
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

        ClasePaciente pacienteObj = pacienteDAO.buscarPorId(idPaciente);
        String nombreObraSocial = "Particular";
        if (pacienteObj != null) {
            // CORRECCIÓN AQUÍ: pasamos el idObraSocial y el valor por defecto "Particular"
            nombreObraSocial = mapaObrasSocialesIdANombre.getOrDefault(pacienteObj.idObraSocial(), "Particular");
        }
        String especialidadDoc = mapaDoctoresEspecialidad.getOrDefault(cmbDoctor.getValue(), "General");

        // System.out.println solicitados
        System.out.println("===== DATOS DEL NUEVO TURNO GUARDADO =====");
        System.out.println("Tipo de Turno / Estado: Próximo");
        System.out.println("Paciente: " + cmbPaciente.getValue() + " (ID: " + idPaciente + ")");
        System.out.println("Obra Social del Paciente: " + nombreObraSocial);
        System.out.println("Doctor: " + cmbDoctor.getValue() + " (ID: " + idDoctor + ")");
        System.out.println("Especialidad del Doctor: " + especialidadDoc);
        System.out.println("Fecha: " + dpFecha.getValue());
        System.out.println("Hora: " + cmbHorario.getValue());
        System.out.println("===========================================");

        System.out.println("Obra Social del Paciente: " + nombreObraSocial + " (ID OS en BD: " + (pacienteObj != null ? pacienteObj.idObraSocial() : "N/A") + ")");
        // Al crear un nuevo turno, el estado inicial por defecto es "Próximo"
        ClaseTurno nuevoTurno = new ClaseTurno(
                0,
                idPaciente,
                idDoctor,
                "Próximo",
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
                    t.estado() // Muestra el estado real proveniente de la BD (Próximo, Atendido, Cancelado)
            ));
        }
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(columna -> new TableCell<>() {
            private final Button btnAtendido = new Button("Atender");
            private final Button btnCancelar = new Button("Cancelar");
            private final HBox contenedorBotones = new HBox(5, btnAtendido, btnCancelar);

            {
                btnAtendido.getStyleClass().add("boton-atendido");
                btnCancelar.getStyleClass().add("boton-eliminar");

                btnAtendido.setOnAction(event -> {
                    TurnoFila turnoFila = getTableView().getItems().get(getIndex());
                    actualizarEstadoTurno(turnoFila, "Atendido");
                });

                btnCancelar.setOnAction(event -> {
                    TurnoFila turnoFila = getTableView().getItems().get(getIndex());
                    actualizarEstadoTurno(turnoFila, "Cancelado");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TurnoFila turnoFila = getTableView().getItems().get(getIndex());
                    // Ocultar botones si el turno ya fue cancelado o atendido opcionalmente, o dejarlos siempre accesibles
                    if ("Cancelado".equals(turnoFila.getEstado())) {
                        btnAtendido.setDisable(true);
                        btnCancelar.setDisable(true);
                    } else {
                        btnAtendido.setDisable(false);
                        btnCancelar.setDisable(false);
                    }
                    setGraphic(contenedorBotones);
                }
            }
        });
    }

    private void actualizarEstadoTurno(TurnoFila turnoFila, String nuevoEstado) {
        ClaseTurno turnoBD = turnoDAO.buscarPorId(turnoFila.getIdTurno());
        if (turnoBD != null) {
            ClaseTurno turnoModificado = new ClaseTurno(
                    turnoBD.idTurno(),
                    turnoBD.idPaciente(),
                    turnoBD.idDoctor(),
                    nuevoEstado,
                    turnoBD.fechaTurno(),
                    turnoBD.horaTurno()
            );

            if (turnoDAO.modificar(turnoModificado)) {
                cargarTurnosDesdeBD();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el estado del turno.");
            }
        }
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
    @FXML
    private void filtrarPorEstadoTabla() {
        // Aquí implementas la lógica para filtrar la tabla de turnos según el estado seleccionado en el ComboBox
        String estadoSeleccionado = cmbFiltroEstado.getValue();
        if (estadoSeleccionado != null) {
            // Ejemplo: filtrar tu lista observable en base a 'estadoSeleccionado'


            //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        }
    }
}