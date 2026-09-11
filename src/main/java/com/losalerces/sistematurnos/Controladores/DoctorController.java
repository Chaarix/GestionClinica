package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClaseDoctor;
import com.losalerces.sistematurnos.DAO.DoctorDAO;
import com.losalerces.sistematurnos.DAO.DoctorDAOImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class DoctorController {
    @FXML private TableView<ClaseDoctor> tblDoctores;
    @FXML private TableColumn<ClaseDoctor, String> colNombre;
    @FXML private TableColumn<ClaseDoctor, String> colApellido;
    @FXML private TableColumn<ClaseDoctor, String> colEspecialidad;


    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private ComboBox<String> cmbEspecialidad;

    @FXML private Label lblTituloForm;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;

    private ObservableList<ClaseDoctor> listaDoctores;
    private ClaseDoctor doctorSeleccionado;
    private final DoctorDAO doctorDAO = new DoctorDAOImpl();
    //private final DoctorDAO doctorDAO = new DoctorDAO();

    @FXML
    public void initialize() {
        listaDoctores = FXCollections.observableArrayList();
        cmbEspecialidad.getItems().addAll("Cardiología", "Pediatría", "Traumatología", "Clínica Médica");

        // 1. Vincular columnas usando la sintaxis de tus métodos getter
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nombre()));
        colApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().apellido()));
        colEspecialidad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().especialidad()));

        // 2. Carga inicial asíncrona
        cargarDoctores();

        // 3. Filtro de búsqueda por Apellido o DNI
        FilteredList<ClaseDoctor> filteredData = new FilteredList<>(listaDoctores, p -> true);
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(doctor -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return doctor.apellido().toLowerCase().contains(filter);
            });
        });
        tblDoctores.setItems(filteredData);

        // 4. Oyente de selección de fila
        tblDoctores.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                doctorSeleccionado = newSel;
                llenarFormulario(newSel);
            }
        });

        // 5. Validaciones de botones con Bindings
        btnEliminar.disableProperty().bind(tblDoctores.getSelectionModel().selectedItemProperty().isNull());
        btnGuardar.disableProperty().bind(
                txtNombre.textProperty().isEmpty()
                        .or(txtApellido.textProperty().isEmpty())
                        .or(cmbEspecialidad.valueProperty().isNull())
        );
    }

    private void cargarDoctores() {
        Task<List<ClaseDoctor>> task = new Task<>() {
            @Override
            protected List<ClaseDoctor> call() throws Exception {
                return doctorDAO.listarTodos();
            }
        };
        task.setOnSucceeded(e -> listaDoctores.setAll(task.getValue()));
        task.setOnFailed(e -> mostrarAlerta("Error", "Error al leer la base de datos.", Alert.AlertType.ERROR));
        new Thread(task).start();



    }

    private void llenarFormulario(ClaseDoctor doc) {
        txtNombre.setText(doc.getNombre());
        txtApellido.setText(doc.getApellido());
        cmbEspecialidad.setValue(doc.getEspecialidad());
        lblTituloForm.setText("Modificar Doctor (ID: " + doc.idDoctor() + ")");
    }

    @FXML
    private void handleGuardar() {
        if (doctorSeleccionado == null) {
            // ---- ALTA ----
            ClaseDoctor nuevoDoc = new ClaseDoctor(0, txtNombre.getText(), txtApellido.getText(),cmbEspecialidad.getValue());
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    doctorDAO.insertar(nuevoDoc);
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                listaDoctores.add(nuevoDoc);
                mostrarAlerta("Éxito", "Doctor registrado con éxito.", Alert.AlertType.INFORMATION);
                handleLimpiarFormulario();
            });
            task.setOnFailed(e -> mostrarAlerta("Error", "No se pudo insertar el doctor.", Alert.AlertType.ERROR));
            new Thread(task).start();
        } else {
            // ---- MODIFICACIÓN ----
            doctorSeleccionado.setNombre(txtNombre.getText())
                    .setApellido(txtApellido.getText())
                    .setEspecialidad(cmbEspecialidad.getValue());

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    doctorDAO.actualizar(doctorSeleccionado);
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                tblDoctores.refresh(); // Necesario porque el modelo no usa Properties de JavaFX
                mostrarAlerta("Éxito", "Datos actualizados correctamente.", Alert.AlertType.INFORMATION);
                handleLimpiarFormulario();
            });
            task.setOnFailed(e -> mostrarAlerta("Error", "No se pudo actualizar el registro.", Alert.AlertType.ERROR));
            new Thread(task).start();
        }
    }

    @FXML
    private void handleEliminar() {
        ClaseDoctor aEliminar = tblDoctores.getSelectionModel().getSelectedItem();
        if (aEliminar == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Dar de baja al Dr./Dra. " + aEliminar.apellido() + "?", ButtonType.YES, ButtonType.NO);
        confirmacion.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        doctorDAO.eliminar(aEliminar.idDoctor());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    listaDoctores.remove(aEliminar);
                    handleLimpiarFormulario();
                });
                task.setOnFailed(e -> mostrarAlerta("Error", "No se pudo eliminar el registro.", Alert.AlertType.ERROR));
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void handleLimpiarFormulario() {
        doctorSeleccionado = null;
        tblDoctores.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtApellido.clear();
        cmbEspecialidad.setValue(null);
        lblTituloForm.setText("Registrar Nuevo Doctor");
    }

    private void mostrarAlerta(String t, String m, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
