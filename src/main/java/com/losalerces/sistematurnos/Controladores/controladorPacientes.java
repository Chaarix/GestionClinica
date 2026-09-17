package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.BD.BaseDatos;
import com.losalerces.sistematurnos.Clases.ClasePaciente;
import com.losalerces.sistematurnos.DAO.PacienteDAO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class controladorPacientes implements Initializable {

    // =========================
    // CAMPOS DEL FORMULARIO
    // =========================

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
    private ComboBox<ObraSocialItem> cmbObraSocial;

    @FXML
    private ComboBox<String> cmbDuracionConsulta;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnModificar;

    @FXML
    private TextField txtDni;

    // =========================
    // TABLA
    // =========================

    @FXML
    private TableView<ClasePaciente> tablaPacientes;

    @FXML
    private TableColumn<ClasePaciente, Integer> colId;

    @FXML
    private TableColumn<ClasePaciente, String> colNombre;

    @FXML
    private TableColumn<ClasePaciente, String> colApellido;

    @FXML
    private TableColumn<ClasePaciente, String> colFechaNacimiento;

    @FXML
    private TableColumn<ClasePaciente, String> colTelefono;

    @FXML
    private TableColumn<ClasePaciente, String> colEmail;

    @FXML
    private TableColumn<ClasePaciente, String> colObraSocial;

    @FXML
    private TableColumn<ClasePaciente, String> colDuracion;

    @FXML
    private TableColumn<ClasePaciente, Void> colAcciones;

    @FXML
    private TableColumn<ClasePaciente, String> colDni;

    // =========================
    // DAO
    // =========================

    private final PacienteDAO pacienteDAO = new PacienteDAO();

    private final ObservableList<ClasePaciente> listaPacientes =
            FXCollections.observableArrayList();


    // =========================
    // INICIALIZAR
    // =========================

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        configurarTabla();

        cargarObrasSociales();

        cargarDuraciones();

        cargarPacientes();

        btnModificar.setDisable(true);

        tablaPacientes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        cargarPacienteEnFormulario(seleccionado);
                    }
                });
    }


    // =========================
    // CONFIGURAR TABLA
    // =========================

    private void configurarTabla() {

        colId.setCellValueFactory(datos ->
                new SimpleIntegerProperty(
                        datos.getValue().idPaciente()
                ).asObject()
        );

        colNombre.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().nombre()
                )
        );

        colApellido.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().apellido()
                )
        );

        colFechaNacimiento.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().fechaNacimiento()
                )
        );

        colTelefono.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().telefono()
                )
        );

        colEmail.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().email()
                )
        );

        colObraSocial.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        obtenerNombreObraSocial(
                                datos.getValue().idObraSocial()
                        )
                )
        );
        colDni.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().dni()
                )
        );

        /*
         * Actualmente ClasePaciente no tiene duración.
         * Por eso esta columna queda vacía.
         *
         * Después podemos agregar duración al modelo y a la BD.
         */
        colDuracion.setCellValueFactory(datos ->
                new SimpleStringProperty("")
        );

        configurarBotonEliminar();
    }


    // =========================
    // CARGAR PACIENTES
    // =========================

    private void cargarPacientes() {

        listaPacientes.clear();

        List<ClasePaciente> pacientes =
                pacienteDAO.listar();

        listaPacientes.addAll(pacientes);

        tablaPacientes.setItems(listaPacientes);
    }


    // =========================
    // GUARDAR
    // =========================
    @FXML
    private void guardarPaciente(ActionEvent event) {

        if (!validarFormulario()) {
            return;
        }

        String dni = txtDni.getText().trim();

        if (pacienteDAO.existeDni(dni)) {

            mostrarError(
                    "DNI duplicado",
                    "Ya existe un paciente registrado con ese DNI."
            );

            return;
        }

        ObraSocialItem obraSocial =
                cmbObraSocial.getValue();

        ClasePaciente paciente =
                new ClasePaciente(
                        0,
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        dpFechaNacimiento.getValue().toString(),
                        txtTelefono.getText().trim(),
                        txtEmail.getText().trim(),
                        obraSocial.id(),
                        dni
                );

        boolean guardado =
                pacienteDAO.guardar(paciente);

        if (guardado) {

            mostrarInformacion(
                    "Paciente guardado",
                    "El paciente fue registrado correctamente."
            );

            cargarPacientes();

            limpiarFormulario();

        } else {

            mostrarError(
                    "Error",
                    "No se pudo guardar el paciente."
            );
        }
    }


    // =========================
    // MODIFICAR
    // =========================

    @FXML
    private void modificarPaciente(ActionEvent event) {

        ClasePaciente seleccionado =
                tablaPacientes
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarError(
                    "Paciente no seleccionado",
                    "Seleccioná un paciente de la tabla."
            );

            return;
        }

        if (!validarFormulario()) {
            return;
        }

        String dni = txtDni.getText().trim();

        // Verifica que el DNI no pertenezca a OTRO paciente
        if (pacienteDAO.existeDniEnOtroPaciente(
                dni,
                seleccionado.idPaciente()
        )) {

            mostrarError(
                    "DNI duplicado",
                    "Ya existe otro paciente registrado con ese DNI."
            );

            return;
        }

        ObraSocialItem obraSocial =
                cmbObraSocial.getValue();

        ClasePaciente pacienteActualizado =
                new ClasePaciente(
                        seleccionado.idPaciente(),
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        dpFechaNacimiento.getValue().toString(),
                        txtTelefono.getText().trim(),
                        txtEmail.getText().trim(),
                        obraSocial.id(),
                        dni
                );

        boolean actualizado =
                pacienteDAO.actualizar(
                        pacienteActualizado
                );

        if (actualizado) {

            mostrarInformacion(
                    "Paciente modificado",
                    "Los datos fueron actualizados correctamente."
            );

            cargarPacientes();
            limpiarFormulario();

        } else {

            mostrarError(
                    "Error",
                    "No se pudo modificar el paciente."
            );
        }
    }


    // =========================
    // BUSCAR
    // =========================

    @FXML
    private void buscarPaciente(ActionEvent event) {

        String busqueda =
                txtBuscar.getText()
                        .trim()
                        .toLowerCase();

        if (busqueda.isEmpty()) {

            tablaPacientes.setItems(
                    listaPacientes
            );

            return;
        }

        ObservableList<ClasePaciente> resultado =
                FXCollections.observableArrayList();

        for (ClasePaciente paciente : listaPacientes) {

            String nombre =
                    paciente.nombre() != null
                            ? paciente.nombre().toLowerCase()
                            : "";

            String apellido =
                    paciente.apellido() != null
                            ? paciente.apellido().toLowerCase()
                            : "";

            if (nombre.contains(busqueda)
                    || apellido.contains(busqueda)) {

                resultado.add(paciente);
            }
        }

        tablaPacientes.setItems(resultado);
    }


    // =========================
    // LIMPIAR
    // =========================

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        limpiarFormulario();
    }

    private void limpiarFormulario() {

        txtNombre.clear();

        txtApellido.clear();

        dpFechaNacimiento.setValue(null);

        txtTelefono.clear();

        txtEmail.clear();

        txtDni.clear();

        cmbObraSocial
                .getSelectionModel()
                .clearSelection();

        cmbDuracionConsulta
                .getSelectionModel()
                .clearSelection();

        tablaPacientes
                .getSelectionModel()
                .clearSelection();

        btnModificar.setDisable(true);
    }


    // =========================
    // SELECCIONAR PACIENTE
    // =========================

    private void cargarPacienteEnFormulario(
            ClasePaciente paciente
    ) {

        txtNombre.setText(
                paciente.nombre()
        );

        txtApellido.setText(
                paciente.apellido()
        );

        txtTelefono.setText(
                paciente.telefono()
        );

        txtEmail.setText(
                paciente.email()
        );
        txtDni.setText(
                paciente.dni()
        );

        // FECHA

        try {

            if (paciente.fechaNacimiento() != null
                    && !paciente.fechaNacimiento().isBlank()) {

                dpFechaNacimiento.setValue(
                        LocalDate.parse(
                                paciente.fechaNacimiento()
                        )
                );
            }

        } catch (Exception e) {

            dpFechaNacimiento.setValue(null);
        }


        // OBRA SOCIAL

        for (ObraSocialItem obra :
                cmbObraSocial.getItems()) {

            if (obra.id()
                    == paciente.idObraSocial()) {

                cmbObraSocial.setValue(obra);

                break;
            }
        }

        btnModificar.setDisable(false);
    }


    // =========================
    // ELIMINAR
    // =========================

    private void configurarBotonEliminar() {

        colAcciones.setCellFactory(columna ->
                new TableCell<>() {

                    private final Button btnEliminar =
                            new Button("Eliminar");

                    {
                        btnEliminar
                                .getStyleClass()
                                .add("boton-secundario");

                        btnEliminar.setOnAction(event -> {

                            ClasePaciente paciente =
                                    getTableView()
                                            .getItems()
                                            .get(
                                                    getIndex()
                                            );

                            eliminarPaciente(
                                    paciente
                            );
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {

                            setGraphic(null);

                        } else {

                            setGraphic(
                                    btnEliminar
                            );
                        }
                    }
                });
    }

    private void eliminarPaciente(
            ClasePaciente paciente
    ) {

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle(
                "Eliminar paciente"
        );

        confirmacion.setHeaderText(
                "¿Desea eliminar al paciente?"
        );

        confirmacion.setContentText(
                paciente.nombre()
                        + " "
                        + paciente.apellido()
        );

        confirmacion.showAndWait()
                .ifPresent(respuesta -> {

                    if (respuesta
                            == ButtonType.OK) {

                        boolean eliminado =
                                pacienteDAO.eliminar(
                                        paciente.idPaciente()
                                );

                        if (eliminado) {

                            cargarPacientes();

                            limpiarFormulario();
                        }
                    }
                });
    }


    // =========================
    // OBRAS SOCIALES
    // =========================

    private void cargarObrasSociales() {

        cmbObraSocial
                .getItems()
                .clear();

        String sql =
                "SELECT id_obra_social, nombre " +
                        "FROM obra_social " +
                        "ORDER BY nombre";

        try (
                Connection conn =
                        BaseDatos.getConnection();

                Statement stmt =
                        conn.createStatement();

                ResultSet rs =
                        stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                cmbObraSocial
                        .getItems()
                        .add(
                                new ObraSocialItem(
                                        rs.getInt(
                                                "id_obra_social"
                                        ),
                                        rs.getString(
                                                "nombre"
                                        )
                                )
                        );
            }

        } catch (Exception e) {

            System.err.println(
                    "Error al cargar obras sociales: "
                            + e.getMessage()
            );
        }
    }


    private String obtenerNombreObraSocial(
            int idObraSocial
    ) {

        for (ObraSocialItem obra :
                cmbObraSocial.getItems()) {

            if (obra.id()
                    == idObraSocial) {

                return obra.nombre();
            }
        }

        return String.valueOf(
                idObraSocial
        );
    }


    // =========================
    // DURACIONES
    // =========================

    private void cargarDuraciones() {

        cmbDuracionConsulta
                .getItems()
                .addAll(
                        "15 minutos",
                        "20 minutos",
                        "30 minutos",
                        "45 minutos",
                        "60 minutos"
                );
    }


    // =========================
    // VALIDACIÓN
    // =========================

    private boolean validarFormulario() {

        if (txtNombre.getText()
                .trim()
                .isEmpty()) {

            mostrarError(
                    "Campo obligatorio",
                    "Ingresá el nombre."
            );

            return false;
        }

        if (txtApellido.getText()
                .trim()
                .isEmpty()) {

            mostrarError(
                    "Campo obligatorio",
                    "Ingresá el apellido."
            );

            return false;
        }

        if (dpFechaNacimiento
                .getValue() == null) {

            mostrarError(
                    "Campo obligatorio",
                    "Seleccioná la fecha de nacimiento."
            );

            return false;
        }

        if (cmbObraSocial
                .getValue() == null) {

            mostrarError(
                    "Campo obligatorio",
                    "Seleccioná una obra social."
            );

            return false;
        }
        if (txtDni.getText().trim().isEmpty()) {

            mostrarError(
                    "Campo obligatorio",
                    "Ingresá el DNI."
            );

            return false;
        }

        if (!txtDni.getText().trim().matches("\\d+")) {

            mostrarError(
                    "DNI inválido",
                    "El DNI debe contener solamente números."
            );

            return false;
        }

        return true;

    }

    // =========================
    // ALERTAS
    // =========================

    private void mostrarError(
            String titulo,
            String mensaje
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }

    private void mostrarInformacion(
            String titulo,
            String mensaje
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }


    // =========================
    // ITEM OBRA SOCIAL
    // =========================

    private record ObraSocialItem(
            int id,
            String nombre
    ) {

        @Override
        public String toString() {
            return nombre;
        }
    }
}