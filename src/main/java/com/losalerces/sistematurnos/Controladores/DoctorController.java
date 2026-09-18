package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClaseDoctor;
import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.DAO.DoctorDAOImpl;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorController {

    // =====================================
    // CAMPOS DEL FORMULARIO
    // =====================================

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtEspecialidad;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnModificar;

    @FXML
    private FlowPane contenedorObrasSociales;


    // =====================================
    // TABLA
    // =====================================

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
    private TableColumn<DoctorFila, String> colObrasSociales;

    @FXML
    private TableColumn<DoctorFila, Void> colAcciones;


    // =====================================
    // VARIABLES
    // =====================================

    private final ObservableList<DoctorFila> doctores =
            FXCollections.observableArrayList();

    private DoctorFila doctorSeleccionado;

    private boolean modoEdicion = false;

    private final DoctorDAOImpl doctorDAO =
            new DoctorDAOImpl();

    private final ObraSocialDAO obraSocialDAO =
            new ObraSocialDAO();

    private final List<CheckBox> checksObrasSociales =
            new ArrayList<>();


    // =====================================
    // INICIALIZAR
    // =====================================

    @FXML
    public void initialize() {

        cargarObrasSocialesDesdeBD();

        configurarTabla();

        cargarDoctoresDesdeBD();

        btnModificar.setDisable(true);
    }


    // =====================================
    // CARGAR DOCTORES
    // =====================================

    private void cargarDoctoresDesdeBD() {

        doctores.clear();

        try {

            List<ClaseDoctor> lista =
                    doctorDAO.listarTodos();

            for (ClaseDoctor doctor : lista) {

                String obrasSociales =
                        doctorDAO
                                .obtenerNombresObrasSociales(
                                        doctor.idDoctor()
                                );

                doctores.add(
                        new DoctorFila(
                                doctor.idDoctor(),
                                doctor.nombre(),
                                doctor.apellido(),
                                doctor.especialidad(),
                                obrasSociales
                        )
                );
            }

            tablaDoctores.setItems(
                    doctores
            );

        } catch (SQLException e) {

            mostrarAdvertencia(
                    "No se pudieron cargar los doctores.\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =====================================
    // CARGAR OBRAS SOCIALES
    // =====================================

    private void cargarObrasSocialesDesdeBD() {

        contenedorObrasSociales
                .getChildren()
                .clear();

        checksObrasSociales.clear();

        List<ClaseObraSocial> obras =
                obraSocialDAO.listarTodos();

        if (obras == null ||
                obras.isEmpty()) {

            Label mensaje =
                    new Label(
                            "No hay obras sociales registradas."
                    );

            mensaje
                    .getStyleClass()
                    .add(
                            "texto-ayuda"
                    );

            contenedorObrasSociales
                    .getChildren()
                    .add(mensaje);

            return;
        }


        for (ClaseObraSocial obra :
                obras) {

            CheckBox checkBox =
                    new CheckBox(
                            obra.nombre()
                    );

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


    // =====================================
    // CONFIGURAR TABLA
    // =====================================

    private void configurarTabla() {

        colId.setCellValueFactory(
                dato ->
                        new SimpleIntegerProperty(
                                dato.getValue()
                                        .getId()
                        ).asObject()
        );


        colNombre.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getNombre()
                        )
        );


        colApellido.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getApellido()
                        )
        );


        colEspecialidad.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getEspecialidad()
                        )
        );


        colObrasSociales.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getObrasSociales()
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
                        (
                                observable,
                                anterior,
                                seleccionado
                        ) -> {

                            if (seleccionado != null) {

                                seleccionarDoctor(
                                        seleccionado
                                );
                            }
                        }
                );
    }


    // =====================================
    // GUARDAR DOCTOR
    // =====================================

    @FXML
    private void guardarDoctor() {

        if (!validarCampos()) {
            return;
        }


        List<Integer> obrasSeleccionadas =
                obtenerIdsObrasSocialesSeleccionadas();


        try {

            // =====================================
            // MODIFICAR DOCTOR EXISTENTE
            // =====================================

            if (
                    modoEdicion
                            &&
                            doctorSeleccionado != null
            ) {

                ClaseDoctor doctor =
                        new ClaseDoctor(
                                doctorSeleccionado.getId(),
                                txtNombre
                                        .getText()
                                        .trim(),

                                txtApellido
                                        .getText()
                                        .trim(),

                                txtEspecialidad
                                        .getText()
                                        .trim()
                        );


                doctorDAO.actualizar(
                        doctor,
                        obrasSeleccionadas
                );


                doctorSeleccionado.setNombre(
                        doctor.nombre()
                );

                doctorSeleccionado.setApellido(
                        doctor.apellido()
                );

                doctorSeleccionado.setEspecialidad(
                        doctor.especialidad()
                );

                doctorSeleccionado
                        .setObrasSociales(
                                obtenerObrasSocialesSeleccionadas()
                        );


                tablaDoctores.refresh();


                mostrarMensaje(
                        "Doctor modificado",
                        "Los datos del médico fueron actualizados correctamente."
                );

            }

            // =====================================
            // GUARDAR NUEVO DOCTOR
            // =====================================

            else {

                ClaseDoctor doctor =
                        new ClaseDoctor(
                                0,

                                txtNombre
                                        .getText()
                                        .trim(),

                                txtApellido
                                        .getText()
                                        .trim(),

                                txtEspecialidad
                                        .getText()
                                        .trim()
                        );


                doctorDAO.insertar(
                        doctor,
                        obrasSeleccionadas
                );


                doctores.add(
                        new DoctorFila(
                                doctor.idDoctor(),
                                doctor.nombre(),
                                doctor.apellido(),
                                doctor.especialidad(),
                                obtenerObrasSocialesSeleccionadas()
                        )
                );


                mostrarMensaje(
                        "Doctor guardado",
                        "El médico fue registrado correctamente."
                );
            }


            limpiarFormulario();

        } catch (SQLException e) {

            mostrarAdvertencia(
                    "No se pudo guardar el doctor.\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =====================================
    // MODIFICAR DOCTOR
    // =====================================

    @FXML
    private void modificarDoctor() {

        if (doctorSeleccionado == null) {

            mostrarAdvertencia(
                    "Seleccioná un doctor para modificar."
            );

            return;
        }


        modoEdicion = true;


        mostrarMensaje(
                "Modificar doctor",
                "Modificá los datos necesarios y luego presioná Guardar doctor."
        );
    }


    // =====================================
    // BUSCAR DOCTOR
    // =====================================

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


        for (DoctorFila doctor :
                doctores) {

            if (
                    doctor
                            .getNombre()
                            .toLowerCase()
                            .contains(buscar)

                            ||

                            doctor
                                    .getApellido()
                                    .toLowerCase()
                                    .contains(buscar)

                            ||

                            doctor
                                    .getEspecialidad()
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


    // =====================================
    // SELECCIONAR DOCTOR
    // =====================================

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


        // Primero limpiar todos
        limpiarChecks();


        // Después cargar los reales desde SQLite
        try {

            List<Integer> idsObrasSociales =
                    doctorDAO
                            .listarIdsObrasSocialesPorDoctor(
                                    doctor.getId()
                            );


            for (CheckBox checkBox :
                    checksObrasSociales) {

                Object dato =
                        checkBox.getUserData();


                if (
                        dato instanceof Integer
                                &&
                                idsObrasSociales.contains(
                                        (Integer) dato
                                )
                ) {

                    checkBox.setSelected(
                            true
                    );
                }
            }

        } catch (SQLException e) {

            mostrarAdvertencia(
                    "No se pudieron cargar las obras sociales del médico."
            );

            e.printStackTrace();
        }


        btnModificar.setDisable(
                false
        );
    }


    // =====================================
    // OBTENER NOMBRES OBRAS SOCIALES
    // =====================================

    private String
    obtenerObrasSocialesSeleccionadas() {

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


    // =====================================
    // OBTENER IDS OBRAS SOCIALES
    // =====================================

    public List<Integer>
    obtenerIdsObrasSocialesSeleccionadas() {

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


    // =====================================
    // ADMINISTRAR OBRAS SOCIALES
    // =====================================

    @FXML
    private void administrarObrasSociales() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
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


            ventana.showAndWait();


            // Volver a cargar por si se agregaron
            cargarObrasSocialesDesdeBD();


            // Si había un doctor seleccionado,
            // recuperar nuevamente sus checks.
            if (doctorSeleccionado != null) {

                seleccionarDoctor(
                        doctorSeleccionado
                );
            }


        } catch (IOException e) {

            mostrarAdvertencia(
                    "No se pudo abrir la administración de obras sociales."
            );

            e.printStackTrace();
        }
    }


    // =====================================
    // BOTÓN ELIMINAR DE LA TABLA
    // =====================================

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


                                btnEliminar
                                        .setOnAction(
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


    // =====================================
    // ELIMINAR DOCTOR
    // =====================================

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
                        +
                        doctor.getNombre()
                        +
                        " "
                        +
                        doctor.getApellido()
                        +
                        "?"
        );


        confirmacion
                .showAndWait()
                .ifPresent(
                        respuesta -> {

                            if (
                                    respuesta
                                            ==
                                            ButtonType.OK
                            ) {

                                try {

                                    doctorDAO.eliminar(
                                            doctor.getId()
                                    );


                                    doctores.remove(
                                            doctor
                                    );


                                    // En caso de estar mostrando
                                    // un resultado de búsqueda
                                    tablaDoctores
                                            .getItems()
                                            .remove(
                                                    doctor
                                            );


                                    limpiarFormulario();


                                    mostrarMensaje(
                                            "Doctor eliminado",
                                            "El médico fue eliminado correctamente."
                                    );


                                } catch (SQLException e) {

                                    mostrarAdvertencia(
                                            "No se pudo eliminar el doctor.\n"
                                                    +
                                                    e.getMessage()
                                    );

                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }


    // =====================================
    // LIMPIAR FORMULARIO
    // =====================================

    @FXML
    private void limpiarFormulario() {

        txtNombre.clear();

        txtApellido.clear();

        txtEspecialidad.clear();


        limpiarChecks();


        doctorSeleccionado =
                null;


        modoEdicion =
                false;


        tablaDoctores
                .getSelectionModel()
                .clearSelection();


        btnModificar.setDisable(
                true
        );
    }


    // =====================================
    // BOTÓN LIMPIAR
    // =====================================

    @FXML
    private void handleLimpiarFormulario(
            ActionEvent event
    ) {

        limpiarFormulario();
    }


    // =====================================
    // LIMPIAR CHECKBOX
    // =====================================

    private void limpiarChecks() {

        for (CheckBox checkBox :
                checksObrasSociales) {

            checkBox.setSelected(
                    false
            );
        }
    }


    // =====================================
    // VALIDAR CAMPOS
    // =====================================

    private boolean validarCampos() {

        if (
                txtNombre
                        .getText()
                        .isBlank()

                        ||

                        txtApellido
                                .getText()
                                .isBlank()

                        ||

                        txtEspecialidad
                                .getText()
                                .isBlank()
        ) {

            mostrarAdvertencia(
                    "Nombre, apellido y especialidad son obligatorios."
            );

            return false;
        }


        if (
                obtenerIdsObrasSocialesSeleccionadas()
                        .isEmpty()
        ) {

            mostrarAdvertencia(
                    "Seleccioná al menos una obra social."
            );

            return false;
        }


        return true;
    }


    // =====================================
    // MENSAJES
    // =====================================

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


    // =====================================
    // MÉTODOS EXTRA
    // =====================================

    public void handleEliminar(
            ActionEvent actionEvent
    ) {

    }


    public void handleGuardar(
            ActionEvent actionEvent
    ) {

    }


    // =====================================
    // CLASE PARA LA TABLA
    // =====================================

    public static class DoctorFila {

        private int id;

        private String nombre;

        private String apellido;

        private String especialidad;

        private String obrasSociales;


        public DoctorFila(
                int id,
                String nombre,
                String apellido,
                String especialidad,
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