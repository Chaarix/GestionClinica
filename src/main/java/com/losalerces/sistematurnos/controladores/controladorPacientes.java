package com.losalerces.sistematurnos.controladores;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class controladorPacientes {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtEmail;

    @FXML
    private ComboBox<String> cmbObraSocial;

    @FXML
    private ComboBox<String> cmbDuracionConsulta;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnModificar;


    @FXML
    private TableView<PacienteFila> tablaPacientes;

    @FXML
    private TableColumn<PacienteFila, Integer> colId;

    @FXML
    private TableColumn<PacienteFila, String> colNombre;

    @FXML
    private TableColumn<PacienteFila, String> colApellido;

    @FXML
    private TableColumn<PacienteFila, String> colFechaNacimiento;

    @FXML
    private TableColumn<PacienteFila, String> colTelefono;

    @FXML
    private TableColumn<PacienteFila, String> colEmail;

    @FXML
    private TableColumn<PacienteFila, String> colObraSocial;

    @FXML
    private TableColumn<PacienteFila, String> colDuracion;

    @FXML
    private TableColumn<PacienteFila, Void> colAcciones;


    private final ObservableList<PacienteFila> pacientes =
            FXCollections.observableArrayList();

    private PacienteFila pacienteSeleccionado;


    @FXML
    public void initialize() {

        configurarTabla();

        cargarObrasSociales();

        cargarDuraciones();

        cargarDatosDePrueba();

        btnModificar.setDisable(true);
    }


    private void cargarDuraciones() {

        cmbDuracionConsulta.getItems().addAll(
                "15 minutos",
                "20 minutos",
                "30 minutos",
                "40 minutos",
                "45 minutos",
                "60 minutos"
        );
    }


    private void cargarObrasSociales() {

        cmbObraSocial.getItems().addAll(
                "PAMI",
                "OSDE",
                "Swiss Medical",
                "Sancor Salud",
                "Particular"
        );
    }


    private void configurarTabla() {

        colId.setCellValueFactory(
                dato -> new SimpleIntegerProperty(
                        dato.getValue().getId()
                ).asObject()
        );

        colNombre.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getNombre()
                )
        );

        colApellido.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getApellido()
                )
        );

        colFechaNacimiento.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getFechaNacimientoTexto()
                )
        );

        colTelefono.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getTelefono()
                )
        );

        colEmail.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getEmail()
                )
        );

        colObraSocial.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getObraSocial()
                )
        );

        colDuracion.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getDuracionConsulta()
                )
        );

        configurarAcciones();

        tablaPacientes.setItems(pacientes);

        tablaPacientes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        seleccionarPaciente(seleccionado);
                    }
                });
    }


    @FXML
    private void guardarPaciente() {

        if (!validarCampos()) {
            return;
        }

        int nuevoId = pacientes.size() + 1;

        PacienteFila paciente =
                new PacienteFila(
                        nuevoId,
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        dpFechaNacimiento.getValue(),
                        txtTelefono.getText().trim(),
                        txtEmail.getText().trim(),
                        cmbObraSocial.getValue(),
                        cmbDuracionConsulta.getValue()
                );

        pacientes.add(paciente);

        limpiarFormulario();
    }


    @FXML
    private void modificarPaciente() {

        if (pacienteSeleccionado == null) {

            mostrarAdvertencia(
                    "Seleccioná un paciente."
            );

            return;
        }

        if (!validarCampos()) {
            return;
        }

        pacienteSeleccionado.setNombre(
                txtNombre.getText().trim()
        );

        pacienteSeleccionado.setApellido(
                txtApellido.getText().trim()
        );

        pacienteSeleccionado.setFechaNacimiento(
                dpFechaNacimiento.getValue()
        );

        pacienteSeleccionado.setTelefono(
                txtTelefono.getText().trim()
        );

        pacienteSeleccionado.setEmail(
                txtEmail.getText().trim()
        );

        pacienteSeleccionado.setObraSocial(
                cmbObraSocial.getValue()
        );

        pacienteSeleccionado.setDuracionConsulta(
                cmbDuracionConsulta.getValue()
        );

        tablaPacientes.refresh();

        limpiarFormulario();
    }


    @FXML
    private void buscarPaciente() {

        String buscar =
                txtBuscar.getText()
                        .trim()
                        .toLowerCase();

        if (buscar.isEmpty()) {

            tablaPacientes.setItems(pacientes);

            return;
        }

        ObservableList<PacienteFila> resultado =
                FXCollections.observableArrayList();

        for (PacienteFila paciente : pacientes) {

            if (paciente.getNombre().toLowerCase().contains(buscar)
                    || paciente.getApellido().toLowerCase().contains(buscar)) {

                resultado.add(paciente);
            }
        }

        tablaPacientes.setItems(resultado);
    }


    private void seleccionarPaciente(
            PacienteFila paciente
    ) {

        pacienteSeleccionado = paciente;

        txtNombre.setText(
                paciente.getNombre()
        );

        txtApellido.setText(
                paciente.getApellido()
        );

        dpFechaNacimiento.setValue(
                paciente.getFechaNacimiento()
        );

        txtTelefono.setText(
                paciente.getTelefono()
        );

        txtEmail.setText(
                paciente.getEmail()
        );

        cmbObraSocial.setValue(
                paciente.getObraSocial()
        );

        cmbDuracionConsulta.setValue(
                paciente.getDuracionConsulta()
        );

        btnModificar.setDisable(false);
    }


    private void configurarAcciones() {

        colAcciones.setCellFactory(
                columna -> new TableCell<>() {

                    private final Button btnEliminar =
                            new Button("Eliminar");

                    {
                        btnEliminar
                                .getStyleClass()
                                .add("boton-eliminar");

                        btnEliminar.setOnAction(event -> {

                            PacienteFila paciente =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            eliminarPaciente(paciente);
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        setGraphic(
                                empty
                                        ? null
                                        : btnEliminar
                        );
                    }
                }
        );
    }


    private void eliminarPaciente(
            PacienteFila paciente
    ) {

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Deseás eliminar a "
                        + paciente.getNombre()
                        + " "
                        + paciente.getApellido()
                        + "?"
        );

        confirmacion.showAndWait()
                .ifPresent(respuesta -> {

                    if (respuesta == ButtonType.OK) {

                        pacientes.remove(paciente);

                        limpiarFormulario();
                    }
                });
    }


    @FXML
    private void limpiarFormulario() {

        txtNombre.clear();
        txtApellido.clear();

        dpFechaNacimiento.setValue(null);

        txtTelefono.clear();
        txtEmail.clear();

        cmbObraSocial.setValue(null);

        cmbDuracionConsulta.setValue(null);

        pacienteSeleccionado = null;

        tablaPacientes
                .getSelectionModel()
                .clearSelection();

        btnModificar.setDisable(true);
    }


    private boolean validarCampos() {

        if (txtNombre.getText().isBlank()
                || txtApellido.getText().isBlank()) {

            mostrarAdvertencia(
                    "Nombre y apellido son obligatorios."
            );

            return false;
        }

        if (dpFechaNacimiento.getValue() != null
                && dpFechaNacimiento.getValue()
                .isAfter(LocalDate.now())) {

            mostrarAdvertencia(
                    "La fecha de nacimiento no puede ser futura."
            );

            return false;
        }

        if (cmbDuracionConsulta.getValue() == null) {

            mostrarAdvertencia(
                    "Seleccioná la duración de la consulta."
            );

            return false;
        }

        return true;
    }


    private void mostrarAdvertencia(
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }


    private void cargarDatosDePrueba() {

        pacientes.addAll(

                new PacienteFila(
                        1,
                        "Ana",
                        "Ruiz",
                        LocalDate.of(1985, 5, 12),
                        "2945123456",
                        "ana@gmail.com",
                        "PAMI",
                        "30 minutos"
                ),

                new PacienteFila(
                        2,
                        "Carlos",
                        "Méndez",
                        LocalDate.of(1992, 10, 7),
                        "2945234567",
                        "carlos@gmail.com",
                        "OSDE",
                        "20 minutos"
                )
        );
    }


    public static class PacienteFila {

        private int id;
        private String nombre;
        private String apellido;
        private LocalDate fechaNacimiento;
        private String telefono;
        private String email;
        private String obraSocial;
        private String duracionConsulta;


        public PacienteFila(
                int id,
                String nombre,
                String apellido,
                LocalDate fechaNacimiento,
                String telefono,
                String email,
                String obraSocial,
                String duracionConsulta
        ) {

            this.id = id;
            this.nombre = nombre;
            this.apellido = apellido;
            this.fechaNacimiento = fechaNacimiento;
            this.telefono = telefono;
            this.email = email;
            this.obraSocial = obraSocial;
            this.duracionConsulta = duracionConsulta;
        }


        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public LocalDate getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(
                LocalDate fechaNacimiento
        ) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public String getFechaNacimientoTexto() {

            if (fechaNacimiento == null) {
                return "";
            }

            return fechaNacimiento.toString();
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getObraSocial() {
            return obraSocial;
        }

        public void setObraSocial(
                String obraSocial
        ) {
            this.obraSocial = obraSocial;
        }

        public String getDuracionConsulta() {
            return duracionConsulta;
        }

        public void setDuracionConsulta(
                String duracionConsulta
        ) {
            this.duracionConsulta = duracionConsulta;
        }
    }
}