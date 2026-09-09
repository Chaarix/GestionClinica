package com.losalerces.sistematurnos.Controladores;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

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

    @FXML
    private void accionGuardar() {
        String nombreStr = txtNombre.getText().trim();

        if (nombreStr.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, ingrese el nombre.", Alert.AlertType.WARNING);
            return;
        }

        if (obraSocialSeleccionada == null) {
            // Se le pasan "" a teléfono y dirección ya que tu constructor todavía los pide
            ClaseObraSocial nuevaOS = new ClaseObraSocial(0, nombreStr);
            boolean insertado = obraSocialDAO.insertar(nuevaOS);

            if (insertado) {
                mostrarAlerta("Éxito", "Obra Social guardada correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo guardar.", Alert.AlertType.ERROR);
            }
        } else {
            obraSocialSeleccionada.setNombre(nombreStr);
            boolean modificado = obraSocialDAO.modificar(obraSocialSeleccionada);

            if (modificado) {
                mostrarAlerta("Éxito", "Obra Social modificada correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo modificar.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void accionEliminar() {
        if (obraSocialSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Por favor, seleccione una fila.", Alert.AlertType.WARNING);
            return;
        }

        boolean eliminado = obraSocialDAO.eliminar(obraSocialSeleccionada.idObraSocial());

        if (eliminado) {
            mostrarAlerta("Éxito", "Obra Social eliminada.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            actualizarTabla();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void accionCancelar() {
        limpiarCampos();
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

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}