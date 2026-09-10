package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class controladorDoctores {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtEspecialidad;

    @FXML
    private ComboBox<String> cmbDuracion;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnModificar;


    // ==========================================
    // CONTENEDOR DINÁMICO DE OBRAS SOCIALES
    // ==========================================

    @FXML
    private FlowPane contenedorObrasSociales;


    // ==========================================
    // TABLA
    // ==========================================

    @FXML
    private TableView<DoctorFila> tablaDoctores;

    @FXML
    private TableColumn<DoctorFila, Integer> colId;

    @FXML
    private TableColumn<DoctorFila, String> colNombre;

    @FXML
    private TableColumn<DoctorFila, String> colApellido;

    @FXML
    private TableColumn<DoctorFila, String> colEspecialidad;

    @FXML
    private TableColumn<DoctorFila, String> colDuracion;

    @FXML
    private TableColumn<DoctorFila, String> colObrasSociales;

    @FXML
    private TableColumn<DoctorFila, Void> colAcciones;


    private final ObservableList<DoctorFila> doctores =
            FXCollections.observableArrayList();


    private DoctorFila doctorSeleccionado;


    // DAO REAL DE OBRAS SOCIALES

    private final ObraSocialDAO obraSocialDAO =
            new ObraSocialDAO();


    // CheckBox creados dinámicamente

    private final List<CheckBox> checksObrasSociales =
            new ArrayList<>();


    // ==========================================
    // INITIALIZE
    // ==========================================

    @FXML
    public void initialize() {

        cargarDuraciones();

        cargarObrasSocialesDesdeBD();

        configurarTabla();

        cargarDatosDePrueba();

        btnModificar.setDisable(true);
    }


    // ==========================================
    // DURACIÓN DE CONSULTA
    // ==========================================

    private void cargarDuraciones() {

        cmbDuracion.getItems().addAll(
                "15 minutos",
                "20 minutos",
                "30 minutos",
                "40 minutos",
                "45 minutos",
                "60 minutos"
        );
    }


    // ==========================================
    // CARGAR OBRAS SOCIALES DESDE LA BD
    // ==========================================

    private void cargarObrasSocialesDesdeBD() {

        contenedorObrasSociales
                .getChildren()
                .clear();

        checksObrasSociales.clear();


        List<ClaseObraSocial> obras =
                obraSocialDAO.listarTodos();


        if (obras == null || obras.isEmpty()) {

            Label mensaje =
                    new Label(
                            "No hay obras sociales registradas."
                    );

            mensaje.getStyleClass()
                    .add("texto-ayuda");

            contenedorObrasSociales
                    .getChildren()
                    .add(mensaje);

            return;
        }


        for (ClaseObraSocial obra : obras) {

            CheckBox checkBox =
                    new CheckBox(
                            obra.nombre()
                    );


            /*
             * Guardamos el ID real de la obra social
             * dentro del propio CheckBox.
             */

            checkBox.setUserData(
                    obra.idObraSocial()
            );


            checksObrasSociales.add(
                    checkBox
            );


            contenedorObrasSociales
                    .getChildren()
                    .add(checkBox);
        }
    }


    // ==========================================
    // CONFIGURAR TABLA
    // ==========================================

    private void configurarTabla() {

        colId.setCellValueFactory(
                dato ->
                        new SimpleIntegerProperty(
                                dato.getValue().getId()
                        ).asObject()
        );


        colNombre.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getNombre()
                        )
        );


        colApellido.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getApellido()
                        )
        );


        colEspecialidad.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getEspecialidad()
                        )
        );


        colDuracion.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getDuracion()
                        )
        );


        colObrasSociales.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getObrasSociales()
                        )
        );


        configurarAcciones();


        tablaDoctores.setItems(
                doctores
        );


        tablaDoctores
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, anterior, seleccionado) -> {

                            if (seleccionado != null) {

                                seleccionarDoctor(
                                        seleccionado
                                );
                            }
                        }
                );
    }


    // ==========================================
    // GUARDAR DOCTOR
    // ==========================================

    @FXML
    private void guardarDoctor() {

        if (!validarCampos()) {
            return;
        }


        int nuevoId =
                doctores.size() + 1;


        DoctorFila doctor =
                new DoctorFila(
                        nuevoId,
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        txtEspecialidad.getText().trim(),
                        cmbDuracion.getValue(),
                        obtenerObrasSocialesSeleccionadas()
                );


        doctores.add(
                doctor
        );


        mostrarMensaje(
                "Doctor guardado",
                "El médico fue registrado correctamente."
        );


        limpiarFormulario();
    }


    // ==========================================
    // MODIFICAR DOCTOR
    // ==========================================

    @FXML
    private void modificarDoctor() {

        if (doctorSeleccionado == null) {

            mostrarAdvertencia(
                    "Seleccioná un doctor."
            );

            return;
        }


        if (!validarCampos()) {
            return;
        }


        doctorSeleccionado.setNombre(
                txtNombre.getText().trim()
        );


        doctorSeleccionado.setApellido(
                txtApellido.getText().trim()
        );


        doctorSeleccionado.setEspecialidad(
                txtEspecialidad.getText().trim()
        );


        doctorSeleccionado.setDuracion(
                cmbDuracion.getValue()
        );


        doctorSeleccionado.setObrasSociales(
                obtenerObrasSocialesSeleccionadas()
        );


        tablaDoctores.refresh();


        mostrarMensaje(
                "Doctor modificado",
                "Los datos fueron actualizados."
        );


        limpiarFormulario();
    }


    // ==========================================
    // BUSCAR
    // ==========================================

    @FXML
    private void buscarDoctor() {

        String buscar =
                txtBuscar
                        .getText()
                        .trim()
                        .toLowerCase();


        if (buscar.isEmpty()) {

            tablaDoctores.setItems(
                    doctores
            );

            return;
        }


        ObservableList<DoctorFila> resultado =
                FXCollections.observableArrayList();


        for (DoctorFila doctor : doctores) {

            if (
                    doctor.getNombre()
                            .toLowerCase()
                            .contains(buscar)

                            ||

                            doctor.getApellido()
                                    .toLowerCase()
                                    .contains(buscar)

                            ||

                            doctor.getEspecialidad()
                                    .toLowerCase()
                                    .contains(buscar)
            ) {

                resultado.add(
                        doctor
                );
            }
        }


        tablaDoctores.setItems(
                resultado
        );
    }


    // ==========================================
    // SELECCIONAR DOCTOR
    // ==========================================

    private void seleccionarDoctor(
            DoctorFila doctor
    ) {

        doctorSeleccionado =
                doctor;


        txtNombre.setText(
                doctor.getNombre()
        );


        txtApellido.setText(
                doctor.getApellido()
        );


        txtEspecialidad.setText(
                doctor.getEspecialidad()
        );


        cmbDuracion.setValue(
                doctor.getDuracion()
        );


        seleccionarObrasSociales(
                doctor.getObrasSociales()
        );


        btnModificar.setDisable(
                false
        );
    }


    // ==========================================
    // OBRAS SOCIALES SELECCIONADAS
    // ==========================================

    private String obtenerObrasSocialesSeleccionadas() {

        StringBuilder resultado =
                new StringBuilder();


        for (CheckBox checkBox :
                checksObrasSociales) {

            if (checkBox.isSelected()) {

                if (!resultado.isEmpty()) {

                    resultado.append(
                            ", "
                    );
                }


                resultado.append(
                        checkBox.getText()
                );
            }
        }


        return resultado.toString();
    }


    // ==========================================
    // OBTENER IDS DE LAS OBRAS SELECCIONADAS
    // ==========================================

    public List<Integer> obtenerIdsObrasSocialesSeleccionadas() {

        List<Integer> ids =
                new ArrayList<>();


        for (CheckBox checkBox :
                checksObrasSociales) {

            if (checkBox.isSelected()) {

                Object dato =
                        checkBox.getUserData();


                if (dato instanceof Integer) {

                    ids.add(
                            (Integer) dato
                    );
                }
            }
        }


        return ids;
    }


    // ==========================================
    // MARCAR OBRAS AL SELECCIONAR UN DOCTOR
    // ==========================================

    private void seleccionarObrasSociales(
            String obrasDoctor
    ) {

        limpiarChecks();


        if (
                obrasDoctor == null
                        ||
                        obrasDoctor.isBlank()
        ) {

            return;
        }


        String[] nombres =
                obrasDoctor.split(",");


        for (String nombre : nombres) {

            String nombreLimpio =
                    nombre.trim();


            for (CheckBox checkBox :
                    checksObrasSociales) {

                if (
                        checkBox
                                .getText()
                                .equalsIgnoreCase(
                                        nombreLimpio
                                )
                ) {

                    checkBox.setSelected(
                            true
                    );
                }
            }
        }
    }


    // ==========================================
    // ADMINISTRAR OBRAS SOCIALES
    // ==========================================

    @FXML
    private void administrarObrasSociales() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/losalerces/sistematurnos/obrasociales.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            Stage ventana =
                    new Stage();


            ventana.setTitle(
                    "Administrar Obras Sociales"
            );


            ventana.setScene(
                    new Scene(
                            root,
                            520,
                            430
                    )
            );


            ventana.initModality(
                    Modality.APPLICATION_MODAL
            );


            ventana.setResizable(
                    false
            );


            /*
             * Espera hasta que el usuario
             * cierre la administración.
             */

            ventana.showAndWait();


            /*
             * IMPORTANTE:
             *
             * Cuando se cierra la ventana,
             * volvemos a consultar SQLite.
             *
             * Por ejemplo:
             * agregaste "SEROS" ->
             * cerrás ->
             * aparece inmediatamente acá.
             */

            cargarObrasSocialesDesdeBD();


        } catch (IOException e) {

            e.printStackTrace();


            mostrarAdvertencia(
                    "No se pudo abrir la administración de obras sociales."
            );
        }
    }


    // ==========================================
    // COLUMNA ELIMINAR
    // ==========================================

    private void configurarAcciones() {

        colAcciones.setCellFactory(
                columna ->
                        new TableCell<>() {

                            private final Button btnEliminar =
                                    new Button(
                                            "Eliminar"
                                    );


                            {
                                btnEliminar
                                        .getStyleClass()
                                        .add(
                                                "boton-eliminar"
                                        );


                                btnEliminar.setOnAction(
                                        event -> {

                                            DoctorFila doctor =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );


                                            eliminarDoctor(
                                                    doctor
                                            );
                                        }
                                );
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


                                setGraphic(
                                        empty
                                                ? null
                                                : btnEliminar
                                );
                            }
                        }
        );
    }


    // ==========================================
    // ELIMINAR DOCTOR
    // ==========================================

    private void eliminarDoctor(
            DoctorFila doctor
    ) {

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmacion.setTitle(
                "Eliminar doctor"
        );


        confirmacion.setHeaderText(
                null
        );


        confirmacion.setContentText(
                "¿Deseás eliminar al doctor "
                        + doctor.getNombre()
                        + " "
                        + doctor.getApellido()
                        + "?"
        );


        confirmacion
                .showAndWait()
                .ifPresent(
                        respuesta -> {

                            if (
                                    respuesta
                                            == ButtonType.OK
                            ) {

                                doctores.remove(
                                        doctor
                                );


                                limpiarFormulario();
                            }
                        }
                );
    }


    // ==========================================
    // LIMPIAR FORMULARIO
    // ==========================================

    @FXML
    private void limpiarFormulario() {

        txtNombre.clear();

        txtApellido.clear();

        txtEspecialidad.clear();

        cmbDuracion.setValue(
                null
        );


        limpiarChecks();


        doctorSeleccionado =
                null;


        tablaDoctores
                .getSelectionModel()
                .clearSelection();


        btnModificar.setDisable(
                true
        );
    }


    private void limpiarChecks() {

        for (CheckBox checkBox :
                checksObrasSociales) {

            checkBox.setSelected(
                    false
            );
        }
    }


    // ==========================================
    // VALIDAR
    // ==========================================

    private boolean validarCampos() {

        if (
                txtNombre.getText().isBlank()

                        ||

                        txtApellido.getText().isBlank()

                        ||

                        txtEspecialidad.getText().isBlank()
        ) {

            mostrarAdvertencia(
                    "Nombre, apellido y especialidad son obligatorios."
            );

            return false;
        }


        if (
                cmbDuracion.getValue()
                        == null
        ) {

            mostrarAdvertencia(
                    "Seleccioná la duración de la consulta."
            );

            return false;
        }


        if (
                obtenerObrasSocialesSeleccionadas()
                        .isBlank()
        ) {

            mostrarAdvertencia(
                    "Seleccioná al menos una obra social."
            );

            return false;
        }


        return true;
    }


    // ==========================================
    // ALERTAS
    // ==========================================

    private void mostrarAdvertencia(
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.WARNING
                );


        alerta.setHeaderText(
                null
        );


        alerta.setContentText(
                mensaje
        );


        alerta.showAndWait();
    }


    private void mostrarMensaje(
            String titulo,
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alerta.setTitle(
                titulo
        );


        alerta.setHeaderText(
                null
        );


        alerta.setContentText(
                mensaje
        );


        alerta.showAndWait();
    }


    // ==========================================
    // DATOS DE PRUEBA
    // ==========================================

    private void cargarDatosDePrueba() {

        doctores.addAll(

                new DoctorFila(
                        1,
                        "Juan",
                        "López",
                        "Clínica Médica",
                        "30 minutos",
                        "PAMI, OSDE"
                ),

                new DoctorFila(
                        2,
                        "María",
                        "Pérez",
                        "Cardiología",
                        "30 minutos",
                        "PAMI"
                ),

                new DoctorFila(
                        3,
                        "Carolina",
                        "Fernández",
                        "Pediatría",
                        "20 minutos",
                        "OSDE"
                )
        );
    }


    // ==========================================
    // CLASE TEMPORAL DOCTOR
    // ==========================================

    public static class DoctorFila {

        private int id;

        private String nombre;

        private String apellido;

        private String especialidad;

        private String duracion;

        private String obrasSociales;


        public DoctorFila(
                int id,
                String nombre,
                String apellido,
                String especialidad,
                String duracion,
                String obrasSociales
        ) {

            this.id =
                    id;

            this.nombre =
                    nombre;

            this.apellido =
                    apellido;

            this.especialidad =
                    especialidad;

            this.duracion =
                    duracion;

            this.obrasSociales =
                    obrasSociales;
        }


        public int getId() {

            return id;
        }


        public String getNombre() {

            return nombre;
        }


        public void setNombre(
                String nombre
        ) {

            this.nombre =
                    nombre;
        }


        public String getApellido() {

            return apellido;
        }


        public void setApellido(
                String apellido
        ) {

            this.apellido =
                    apellido;
        }


        public String getEspecialidad() {

            return especialidad;
        }


        public void setEspecialidad(
                String especialidad
        ) {

            this.especialidad =
                    especialidad;
        }


        public String getDuracion() {

            return duracion;
        }


        public void setDuracion(
                String duracion
        ) {

            this.duracion =
                    duracion;
        }


        public String getObrasSociales() {

            return obrasSociales;
        }


        public void setObrasSociales(
                String obrasSociales
        ) {

            this.obrasSociales =
                    obrasSociales;
        }
    }
}