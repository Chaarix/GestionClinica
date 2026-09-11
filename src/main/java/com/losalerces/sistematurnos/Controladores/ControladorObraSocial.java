package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class ControladorObraSocial {

    @FXML
    private TextField txtNombre;

    @FXML
    private TableView<ClaseObraSocial> tblObrasSociales;

    @FXML
    private TableColumn<ClaseObraSocial, Integer> colId;

    @FXML
    private TableColumn<ClaseObraSocial, String> colNombre;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnEliminar;

    private final ObraSocialDAO obraSocialDAO = new ObraSocialDAO();
    private final ObservableList<ClaseObraSocial> listaObrasSociales = FXCollections.observableArrayList();
    private ClaseObraSocial obraSocialSeleccionada = null;

    @FXML
    public void initialize() {
        // Enlazamos las columnas de forma explícita adaptándonos a tus métodos
        colId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().idObraSocial()).asObject()
        );

        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().nombre())
        );

        // Escuchamos cuando el usuario hace clic en una fila de la tabla
        tblObrasSociales.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                obraSocialSeleccionada = newSelection;
                txtNombre.setText(obraSocialSeleccionada.nombre());
            }
        });

        actualizarTabla();
    }

    private void asignarIcono(javafx.stage.Stage stage) {
        try {
            java.io.InputStream streamImagen = getClass().getResourceAsStream("/imagen/montaña_clinica.png");
            if (streamImagen != null) {
                stage.getIcons().add(new javafx.scene.image.Image(streamImagen));
            }
        } catch (Exception e) {
            System.out.println("[Icono] Error al cargar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void accionGuardar() {
        String nombreStr = txtNombre.getText().trim();

        if (nombreStr.isEmpty()) {
            mostrarAlerta("Atención", "Campo requerido", "Por favor, ingrese el nombre de la Obra Social.", Alert.AlertType.WARNING);
            return;
        }

        if (obraSocialSeleccionada == null) {
            ClaseObraSocial nuevaOS = new ClaseObraSocial(0, nombreStr);
            boolean insertado = obraSocialDAO.insertar(nuevaOS);

            if (insertado) {
                mostrarAlerta("Éxito", "Registro completado", "La Obra Social '" + nombreStr + "' se guardó correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo guardar", "Ocurrió un problema al intentar registrar en la base de datos.", Alert.AlertType.ERROR);
            }
        } else {
            String nombreViejo = obraSocialSeleccionada.nombre();

            if (nombreStr.equalsIgnoreCase(nombreViejo)) {
                mostrarAlerta("Atención", "Sin cambios detectados",
                        "No modificó el nombre de la Obra Social. Escriba un nombre diferente para guardar.", Alert.AlertType.WARNING);
                return;
            }

            obraSocialSeleccionada.setNombre(nombreStr);
            boolean modificado = obraSocialDAO.modificar(obraSocialSeleccionada);

            if (modificado) {
                mostrarAlerta("Éxito", "Modificación completada", "Se cambió el nombre de '" + nombreViejo + "' a '" + nombreStr + "' correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo modificar", "No se pudieron aplicar los cambios en la base de datos.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void accionEliminar() {
        if (obraSocialSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Fila no seleccionada", "Por favor, seleccione una fila.", Alert.AlertType.WARNING);
            return;
        }

        // 1. Alerta de confirmación de JavaFX
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta Obra Social?");
        confirmacion.setContentText("Vas a borrar '" + obraSocialSeleccionada.nombre() + "' de forma permanente.");

        // 2. Le inyectamos el icono usando el método único
        asignarIcono((javafx.stage.Stage) confirmacion.getDialogPane().getScene().getWindow());

        // 3. Botones en español limpios (Usamos CANCEL para evitar fallos de ButtonData)
        ButtonType btnSi = new ButtonType("Sí, eliminar");
        ButtonType btnNo = ButtonType.CANCEL;
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        java.util.Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnSi) {
            boolean eliminado = obraSocialDAO.eliminar(obraSocialSeleccionada.idObraSocial());

            if (eliminado) {
                mostrarAlerta("Éxito", "Obra Social eliminada.", "El registro se borró correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar.", "Ocurrió un problema en la base de datos.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void accionCancelar() {
        if (!txtNombre.getText().trim().isEmpty() || obraSocialSeleccionada != null) {

            limpiarCampos();

            System.out.println("[Controlador] Operación cancelada de forma limpia.");

        }
    }


    private void actualizarTabla() {
        listaObrasSociales.clear();
        List<ClaseObraSocial> desdeBD = obraSocialDAO.listarTodos();
        listaObrasSociales.addAll(desdeBD);
        tblObrasSociales.setItems(listaObrasSociales);
    }

    private void limpiarCampos() {
        txtNombre.clear();
        obraSocialSeleccionada = null;
        tblObrasSociales.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String encabezado, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);

        // LLAMAMOS AL MÉTODO ÚNICO PASÁNDOLE LA VENTANA DE LA ALERTA
        asignarIcono((javafx.stage.Stage) alerta.getDialogPane().getScene().getWindow());

        alerta.showAndWait();
    }
}