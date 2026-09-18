package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.Clases.ClasePaciente;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;
import com.losalerces.sistematurnos.DAO.PacienteDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControladorPacientes {

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

    private final ObservableList<PacienteFila> pacientes = FXCollections.observableArrayList();

    private PacienteFila pacienteSeleccionado;

    private final PacienteDAO pacienteDAO = new PacienteDAO();

    private final ObraSocialDAO obraSocialDAO = new ObraSocialDAO();
    private final Map<String, Integer> mapaObrasSociales = new HashMap<>();
    private final Map<String, Integer> mapaObrasSocialesNombreAId = new HashMap<>();
    private final Map<Integer, String> mapaObrasSocialesIdANombre = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("[INIT] >>> Inicializando controladorPacientes...");
        configurarTabla();
        cargarObrasSociales();
        cargarDuraciones();
        cargarPacientesDesdeBD();
        btnModificar.setDisable(true);
        System.out.println("[INIT] <<< controladorPacientes inicializado correctamente.");
    }

    private void cargarDuraciones() {
        System.out.println("[DURACIONES] Cargando opciones predeterminadas de duración de consulta...");
        cmbDuracionConsulta.getItems().addAll(
                "15 minutos",
                "20 minutos",
                "30 minutos",
                "40 minutos",
                "45 minutos",
                "60 minutos"
        );
        System.out.println("[DURACIONES] Opciones cargadas exitosamente.");
    }

    private void cargarObrasSociales() {
        System.out.println("[OBRAS SOCIALES] Conectando con BD (ObraSocialDAO) para listar todas las obras sociales...");
        cmbObraSocial.getItems().clear();
        mapaObrasSociales.clear();
        mapaObrasSocialesNombreAId.clear();
        mapaObrasSocialesIdANombre.clear();

        List<ClaseObraSocial> listaBD = obraSocialDAO.listarTodos();
        System.out.println("[OBRAS SOCIALES] Cantidad de obras sociales encontradas en BD: " + (listaBD != null ? listaBD.size() : 0));

        if (listaBD != null) {
            for (ClaseObraSocial os : listaBD) {
                System.out.println("[OBRAS SOCIALES] Procesando -> ID: " + os.idObraSocial() + " | Nombre: " + os.nombre());
                cmbObraSocial.getItems().add(os.nombre());
                mapaObrasSociales.put(os.nombre(), os.idObraSocial());
                mapaObrasSocialesNombreAId.put(os.nombre(), os.idObraSocial());
                mapaObrasSocialesIdANombre.put(os.idObraSocial(), os.nombre());
            }
        }
        System.out.println("[OBRAS SOCIALES] Mapeo completado.");
    }

    private void configurarTabla() {
        System.out.println("[TABLA] Configurando factorías de celdas para tablaPacientes...");
        colId.setCellValueFactory(
                dato -> new SimpleIntegerProperty(dato.getValue().getId()).asObject()
        );

        colNombre.setCellValueFactory(
                dato -> new SimpleStringProperty(dato.getValue().getNombre())
        );

        colApellido.setCellValueFactory(
                dato -> new SimpleStringProperty(dato.getValue().getApellido())
        );

        colFechaNacimiento.setCellValueFactory(
                dato -> new SimpleStringProperty(dato.getValue().getFechaNacimientoTexto())
        );

        colTelefono.setCellValueFactory(
                dato -> new SimpleStringProperty(dato.getValue().getTelefono())
        );

        colEmail.setCellValueFactory(
                dato -> new SimpleStringProperty(dato.getValue().getEmail())
        );

        colObraSocial.setCellValueFactory(
                dato -> new SimpleStringProperty(dato.getValue().getObraSocial())
        );

        colDuracion.setCellValueFactory(
                dato -> new SimpleStringProperty(dato.getValue().getDuracionConsulta())
        );

        configurarAcciones();

        tablaPacientes.setItems(pacientes);

        tablaPacientes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        System.out.println("[TABLA EVENTO] Fila seleccionada en la tabla -> ID: " + seleccionado.getId() + ", Nombre: " + seleccionado.getNombre() + " " + seleccionado.getApellido());
                        seleccionarPaciente(seleccionado);
                    }
                });
        System.out.println("[TABLA] Configuración de tabla finalizada.");
    }

    @FXML
    private void guardarPaciente() {
        System.out.println("\n[GUARDAR PACIENTE] Intentando guardar nuevo paciente...");
        if (!validarCampos()) {
            System.out.println("[GUARDAR PACIENTE] Validación fallida. Operación cancelada.");
            return;
        }

        String nombreOSSeleccionada = cmbObraSocial.getValue();
        int idOSObtenido = obtenerIdObraSocial(nombreOSSeleccionada);

        System.out.println("[GUARDAR PACIENTE] Datos capturados del formulario:");
        System.out.println(" - Nombre: " + txtNombre.getText().trim());
        System.out.println(" - Apellido: " + txtApellido.getText().trim());
        System.out.println(" - Fecha Nacimiento: " + (dpFechaNacimiento.getValue() != null ? dpFechaNacimiento.getValue().toString() : "VACÍO"));
        System.out.println(" - Teléfono: " + txtTelefono.getText().trim());
        System.out.println(" - Email: " + txtEmail.getText().trim());
        System.out.println(" - Obra Social Seleccionada: " + nombreOSSeleccionada + " (ID resuelto en mapa: " + idOSObtenido + ")");

        ClasePaciente nuevoPaciente = new ClasePaciente(
                0,
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                dpFechaNacimiento.getValue() != null ? dpFechaNacimiento.getValue().toString() : "",
                txtTelefono.getText().trim(),
                txtEmail.getText().trim(),
                idOSObtenido
        );

        System.out.println("[GUARDAR PACIENTE] Enviando objeto ClasePaciente a PacienteDAO.guardar()...");
        boolean exito = pacienteDAO.guardar(nuevoPaciente);
        System.out.println("[GUARDAR PACIENTE] Resultado de la consulta DAO: " + (exito ? "ÉXITO" : "FALLO"));

        if (exito) {
            cargarPacientesDesdeBD();
            limpiarFormulario();
        } else {
            mostrarAdvertencia("No se pudo guardar el paciente en la base de datos.");
        }
    }

    private void cargarPacientesDesdeBD() {
        System.out.println("[BD PACIENTES] Consultando lista de pacientes desde PacienteDAO.listar()...");
        pacientes.clear();
        List<ClasePaciente> listaDAO = pacienteDAO.listar();
        System.out.println("[BD PACIENTES] Cantidad de registros devueltos por la BD: " + (listaDAO != null ? listaDAO.size() : 0));

        if (listaDAO != null) {
            for (ClasePaciente p : listaDAO) {
                String nombreOS = obtenerNombreObraSocial(p.idObraSocial());
                System.out.println("[BD PACIENTES] -> Mapeando Paciente ID: " + p.idPaciente() + " | " + p.nombre() + " " + p.apellido() + " | ID Obra Social FK: " + p.idObraSocial() + " -> Nombre OS: " + nombreOS);

                pacientes.add(new PacienteFila(
                        p.idPaciente(),
                        p.nombre(),
                        p.apellido(),
                        p.fechaNacimiento() != null && !p.fechaNacimiento().isEmpty() ? LocalDate.parse(p.fechaNacimiento()) : null,
                        p.telefono(),
                        p.email(),
                        nombreOS,
                        cmbDuracionConsulta.getItems().isEmpty() ? "30 minutos" : cmbDuracionConsulta.getItems().get(0)
                ));
            }
        }
        System.out.println("[BD PACIENTES] Tabla recargada con éxito en la interfaz.");
    }

    private String obtenerNombreObraSocial(int id) {
        String nombreResuelto = mapaObrasSocialesIdANombre.getOrDefault(id, "Particular");
        System.out.println("[MAPA OS] Buscando nombre para ID Obra Social [" + id + "] -> Resultado: " + nombreResuelto);
        return nombreResuelto;
    }

    private int obtenerIdObraSocial(String nombre) {
        if (nombre == null) {
            System.out.println("[MAPA OS] El nombre de obra social recibido es nulo. Retornando ID por defecto: 0");
            return 0;
        }
        int idResuelto = mapaObrasSocialesNombreAId.getOrDefault(nombre, mapaObrasSociales.getOrDefault(nombre, 0));
        System.out.println("[MAPA OS] Buscando ID para nombre de Obra Social [" + nombre + "] -> Resultado ID: " + idResuelto);
        return idResuelto;
    }

    @FXML
    private void modificarPaciente() {
        System.out.println("\n[MODIFICAR PACIENTE] Intentando actualizar paciente seleccionado...");
        if (pacienteSeleccionado == null) {
            System.out.println("[MODIFICAR PACIENTE] No hay ningún paciente seleccionado para modificar.");
            mostrarAdvertencia("Seleccioná un paciente.");
            return;
        }

        if (!validarCampos()) {
            System.out.println("[MODIFICAR PACIENTE] Validación de campos fallida.");
            return;
        }

        int idPacienteActualizar = pacienteSeleccionado.getId();
        String nombreOSSeleccionada = cmbObraSocial.getValue();
        int idOSObtenido = obtenerIdObraSocial(nombreOSSeleccionada);

        System.out.println("[MODIFICAR PACIENTE] Datos nuevos para ID " + idPacienteActualizar + ":");
        System.out.println(" - Nombre: " + txtNombre.getText().trim());
        System.out.println(" - Apellido: " + txtApellido.getText().trim());
        System.out.println(" - Fecha Nacimiento: " + (dpFechaNacimiento.getValue() != null ? dpFechaNacimiento.getValue().toString() : "VACÍO"));
        System.out.println(" - Teléfono: " + txtTelefono.getText().trim());
        System.out.println(" - Email: " + txtEmail.getText().trim());
        System.out.println(" - Obra Social: " + nombreOSSeleccionada + " (ID: " + idOSObtenido + ")");

        ClasePaciente pacienteActualizado = new ClasePaciente(
                idPacienteActualizar,
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                dpFechaNacimiento.getValue() != null ? dpFechaNacimiento.getValue().toString() : "",
                txtTelefono.getText().trim(),
                txtEmail.getText().trim(),
                idOSObtenido
        );

        System.out.println("[MODIFICAR PACIENTE] Enviando a PacienteDAO.actualizar()...");
        boolean exito = pacienteDAO.actualizar(pacienteActualizado);
        System.out.println("[MODIFICAR PACIENTE] Resultado de actualización DAO: " + (exito ? "ÉXITO" : "FALLO"));

        if (exito) {
            cargarPacientesDesdeBD();
            limpiarFormulario();
        } else {
            mostrarAdvertencia("No se pudo actualizar el paciente en la base de datos.");
        }
    }

    @FXML
    private void buscarPaciente() {
        String buscar = txtBuscar.getText().trim().toLowerCase();
        System.out.println("[BUSCAR PACIENTE] Término de búsqueda ingresado: '" + buscar + "'");

        if (buscar.isEmpty()) {
            System.out.println("[BUSCAR PACIENTE] Campo de búsqueda vacío. Mostrando lista completa.");
            tablaPacientes.setItems(pacientes);
            return;
        }

        ObservableList<PacienteFila> resultado = FXCollections.observableArrayList();

        for (PacienteFila paciente : pacientes) {
            if (paciente.getNombre().toLowerCase().contains(buscar)
                    || paciente.getApellido().toLowerCase().contains(buscar)) {
                resultado.add(paciente);
            }
        }

        System.out.println("[BUSCAR PACIENTE] Coincidencias encontradas: " + resultado.size());
        tablaPacientes.setItems(resultado);
    }

    private void seleccionarPaciente(PacienteFila paciente) {
        pacienteSeleccionado = paciente;
        System.out.println("[SELECCIÓN] Cargando datos del paciente ID " + paciente.id + " en los campos del formulario...");

        txtNombre.setText(paciente.getNombre());
        txtApellido.setText(paciente.getApellido());
        dpFechaNacimiento.setValue(paciente.getFechaNacimiento());
        txtTelefono.setText(paciente.getTelefono());
        txtEmail.setText(paciente.getEmail());
        cmbObraSocial.setValue(paciente.getObraSocial());
        cmbDuracionConsulta.setValue(paciente.getDuracionConsulta());

        btnModificar.setDisable(false);
        System.out.println("[SELECCIÓN] Formulario poblado y botón 'Modificar' habilitado.");
    }

    private void configurarAcciones() {
        System.out.println("[ACCIONES TABLA] Configurando botón de eliminar en la columna de acciones...");
        colAcciones.setCellFactory(
                columna -> new TableCell<>() {
                    private final Button btnEliminar = new Button("Eliminar");

                    {
                        btnEliminar.getStyleClass().add("boton-eliminar");

                        btnEliminar.setOnAction(event -> {
                            PacienteFila paciente = getTableView().getItems().get(getIndex());
                            System.out.println("[ACCIÓN ELIMINAR] Botón presionado para paciente ID: " + paciente.getId() + " (" + paciente.getNombre() + " " + paciente.getApellido() + ")");
                            eliminarPaciente(paciente);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btnEliminar);
                    }
                }
        );
    }

    private void eliminarPaciente(PacienteFila paciente) {
        System.out.println("[ELIMINAR PACIENTE] Solicitando confirmación de eliminación para ID: " + paciente.getId());
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseás eliminar a " + paciente.getNombre() + " " + paciente.getApellido() + "?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                System.out.println("[ELIMINAR PACIENTE] Usuario confirmó eliminación. Ejecutando PacienteDAO.eliminar(" + paciente.getId() + ")...");
                boolean exito = pacienteDAO.eliminar(paciente.getId());
                System.out.println("[ELIMINAR PACIENTE] Resultado de eliminación DAO: " + (exito ? "ÉXITO" : "FALLO"));

                if (exito) {
                    cargarPacientesDesdeBD();
                    limpiarFormulario();
                } else {
                    mostrarAdvertencia("No se pudo eliminar el paciente de la base de datos.");
                }
            } else {
                System.out.println("[ELIMINAR PACIENTE] Eliminación cancelada por el usuario.");
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        System.out.println("[LIMPIAR] Limpiando campos del formulario y reseteando estado...");
        txtNombre.clear();
        txtApellido.clear();
        dpFechaNacimiento.setValue(null);
        txtTelefono.clear();
        txtEmail.clear();
        cmbObraSocial.setValue(null);
        cmbDuracionConsulta.setValue(null);

        pacienteSeleccionado = null;
        tablaPacientes.getSelectionModel().clearSelection();
        btnModificar.setDisable(true);
        System.out.println("[LIMPIAR] Formulario limpio y botón 'Modificar' deshabilitado.");
    }

    private boolean validarCampos() {
        System.out.println("[VALIDACIÓN] Validando campos del formulario...");
        if (txtNombre.getText().isBlank() || txtApellido.getText().isBlank()) {
            System.out.println("[VALIDACIÓN ERROR] Nombre o apellido están vacíos.");
            mostrarAdvertencia("Nombre y apellido son obligatorios.");
            return false;
        }

        if (dpFechaNacimiento.getValue() != null && dpFechaNacimiento.getValue().isAfter(LocalDate.now())) {
            System.out.println("[VALIDACIÓN ERROR] La fecha de nacimiento es posterior a la fecha actual.");
            mostrarAdvertencia("La fecha de nacimiento no puede ser futura.");
            return false;
        }

        if (cmbDuracionConsulta.getValue() == null) {
            System.out.println("[VALIDACIÓN ERROR] No se seleccionó la duración de la consulta.");
            mostrarAdvertencia("Seleccioná la duración de la consulta.");
            return false;
        }

        System.out.println("[VALIDACIÓN] ¡Validación exitosa!");
        return true;
    }

    private void mostrarAdvertencia(String mensaje) {
        System.out.println("[ALERTA VISUAL] Mostrando advertencia emergente: " + mensaje);
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        public LocalDate getFechaNacimiento() { return fechaNacimiento; }
        public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
        public String getFechaNacimientoTexto() {
            return fechaNacimiento == null ? "" : fechaNacimiento.toString();
        }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getObraSocial() { return obraSocial; }
        public void setObraSocial(String obraSocial) { this.obraSocial = obraSocial; }
        public String getDuracionConsulta() { return duracionConsulta; }
        public void setDuracionConsulta(String duracionConsulta) { this.duracionConsulta = duracionConsulta; }
    }
}