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
        System.out.println("[INIT] >>> Inicializando ControladorObraSocial...");

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
                System.out.println("[TABLA EVENTO] Fila seleccionada -> ID: " + obraSocialSeleccionada.idObraSocial() + " | Nombre: " + obraSocialSeleccionada.nombre());
            }
        });

        actualizarTabla();
        System.out.println("[INIT] <<< ControladorObraSocial inicializado correctamente.");
    }

    private void asignarIcono(javafx.stage.Stage stage) {
        try {
            System.out.println("[Icono/CSS] Intentando asignar icono y CSS a la ventana...");
            // 1. Carga del icono de la montaña
            java.io.InputStream streamImagen = getClass().getResourceAsStream("/imagen/montaña_clinica.png");
            if (streamImagen != null) {
                stage.getIcons().add(new javafx.scene.image.Image(streamImagen));
                System.out.println("[Icono/CSS] Icono de montaña cargado con éxito.");
            } else {
                System.out.println("[Icono/CSS] Advertencia: No se encontró la imagen en '/imagen/montaña_clinica.png'");
            }

            // 2. LE AGREGAMOS EL CSS A LA ALERTA (Ruta de tu carpeta de recursos)
            if (stage.getScene() != null) {
                String urlCss = getClass().getResource("/com/losalerces/sistematurnos/style.css").toExternalForm();
                stage.getScene().getStylesheets().add(urlCss);
                System.out.println("[Icono/CSS] Archivo style.css aplicado correctamente.");
            }

        } catch (Exception e) {
            System.out.println("[Icono/CSS] Error al cargar los recursos de la alerta: " + e.getMessage());
        }
    }


    @FXML
    private void accionGuardar() {
        String nombreStr = txtNombre.getText().trim();
        System.out.println("\n[ACCIÓN GUARDAR] Intentando guardar Obra Social con texto: '" + nombreStr + "'");

        if (nombreStr.isEmpty()) {
            System.out.println("[ACCIÓN GUARDAR] Validación fallida: El campo de nombre está vacío.");
            mostrarAlerta("Atención", "Campo requerido", "Por favor, ingrese el nombre de la Obra Social.", Alert.AlertType.WARNING);
            return;
        }

        if (obraSocialSeleccionada == null) {
            System.out.println("[ACCIÓN GUARDAR] Modo: NUEVA Obra Social.");
            ClaseObraSocial nuevaOS = new ClaseObraSocial(0, nombreStr);


            System.out.println("[DAO] Ejecutando ObraSocialDAO.insertar() para: " + nombreStr);
            boolean insertado = obraSocialDAO.insertar(nuevaOS);
            System.out.println("[DAO] Resultado de inserción: " + (insertado ? "ÉXITO" : "FALLO"));

            if (insertado) {
                mostrarAlerta("Éxito", "Registro completado", "La Obra Social '" + nombreStr + "' se guardó correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo guardar", "Ocurrió un problema al intentar registrar en la base de datos.", Alert.AlertType.ERROR);
            }
        } else {
            System.out.println("[ACCIÓN GUARDAR] Modo: MODIFICAR Obra Social existente (ID: " + obraSocialSeleccionada.idObraSocial() + ").");
            String nombreViejo = obraSocialSeleccionada.nombre();

            if (nombreStr.equalsIgnoreCase(nombreViejo)) {
                System.out.println("[ACCIÓN GUARDAR] Sin cambios detectados en el nombre.");
                mostrarAlerta("Atención", "Sin cambios detectados",
                        "No modificó el nombre de la Obra Social. Escriba un nombre diferente para guardar.", Alert.AlertType.WARNING);
                return;
            }

            System.out.println("[ACCIÓN GUARDAR] Cambiando nombre de '" + nombreViejo + "' a '" + nombreStr + "'");
            obraSocialSeleccionada.setNombre(nombreStr);

            System.out.println("[DAO] Ejecutando ObraSocialDAO.modificar()...");
            boolean modificado = obraSocialDAO.modificar(obraSocialSeleccionada);
            System.out.println("[DAO] Resultado de modificación: " + (modificado ? "ÉXITO" : "FALLO"));

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
        System.out.println("\n[ACCIÓN ELIMINAR] Botón presionado.");
        if (obraSocialSeleccionada == null) {
            System.out.println("[ACCIÓN ELIMINAR] Operación cancelada: No hay ninguna Obra Social seleccionada.");
            mostrarAlerta("Selección requerida", "Fila no seleccionada", "Por favor, seleccione una fila.", Alert.AlertType.WARNING);
            return;
        }

        System.out.println("[ACCIÓN ELIMINAR] Preparando confirmación para: " + obraSocialSeleccionada.nombre() + " (ID: " + obraSocialSeleccionada.idObraSocial() + ")");

        // 1. Alerta de confirmación de JavaFX
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta Obra Social?");
        confirmacion.setContentText("Vas a borrar '" + obraSocialSeleccionada.nombre() + "' de forma permanente.");

        // LE APLICAMOS EL CSS DIRECTAMENTE AL DIALOG PANE (Ruta de tu carpeta de recursos)
        try {
            String urlCss = getClass().getResource("/com/losalerces/sistematurnos/style.css").toExternalForm();
            confirmacion.getDialogPane().getStylesheets().add(urlCss);
        } catch (Exception e) {
            System.out.println("[Confirmación] No se pudo aplicar el archivo style.css: " + e.getMessage());
        }

        // 2. Le inyectamos el icono usando el método único
        asignarIcono((javafx.stage.Stage) confirmacion.getDialogPane().getScene().getWindow());

        // 3. Botones en español limpios
        ButtonType btnSi = new ButtonType("Sí, eliminar");
        ButtonType btnNo = ButtonType.CANCEL;
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        java.util.Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnSi) {
            System.out.println("[ACCIÓN ELIMINAR] Usuario confirmó la eliminación. Ejecutando ObraSocialDAO.eliminar(" + obraSocialSeleccionada.idObraSocial() + ")...");
            boolean eliminado = obraSocialDAO.eliminar(obraSocialSeleccionada.idObraSocial());
            System.out.println("[DAO] Resultado de eliminación: " + (eliminado ? "ÉXITO" : "FALLO"));

            if (eliminado) {
                mostrarAlerta("Éxito", "Obra Social eliminada.", "El registro se borró correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar.", "Ocurrió un problema en la base de datos.", Alert.AlertType.ERROR);
            }
        } else {
            System.out.println("[ACCIÓN ELIMINAR] Eliminación cancelada por el usuario.");
        }
    }

    @FXML
    private void accionCancelar() {
        System.out.println("[ACCIÓN CANCELAR] Botón presionado.");
        if (!txtNombre.getText().trim().isEmpty() || obraSocialSeleccionada != null) {
            limpiarCampos();
            System.out.println("[Controlador] Operación cancelada de forma limpia.");
        } else {
            System.out.println("[ACCIÓN CANCELAR] El formulario ya estaba limpio.");
        }
    }


    private void actualizarTabla() {
        System.out.println("[BD OBRAS SOCIALES] Consultando registros con ObraSocialDAO.listarTodos()...");
        listaObrasSociales.clear();
        List<ClaseObraSocial> desdeBD = obraSocialDAO.listarTodos();
        System.out.println("[BD OBRAS SOCIALES] Cantidad de registros obtenidos de la BD: " + (desdeBD != null ? desdeBD.size() : 0));

        if (desdeBD != null) {
            for (ClaseObraSocial os : desdeBD) {
                System.out.println("  -> ID: " + os.idObraSocial() + " | Nombre: " + os.nombre());
            }
        }

        listaObrasSociales.addAll(desdeBD);
        tblObrasSociales.setItems(listaObrasSociales);
        System.out.println("[BD OBRAS SOCIALES] Tabla visual actualizada con éxito.");
    }

    private void limpiarCampos() {
        System.out.println("[LIMPIAR] Limpiando formulario y selección de tabla...");
        txtNombre.clear();
        obraSocialSeleccionada = null;
        tblObrasSociales.getSelectionModel().clearSelection();
        System.out.println("[LIMPIAR] Formulario limpio.");
    }

    private void mostrarAlerta(String titulo, String encabezado, String mensaje, Alert.AlertType tipo) {
        System.out.println("[ALERTA] Mostrando cuadro emergente [" + tipo + "] - Título: '" + titulo + "' | Mensaje: '" + mensaje + "'");
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);

        // 1. LE APLICAMOS EL CSS DIRECTAMENTE AL DIALOG PANE (Ruta de tu carpeta de recursos)
        try {
            String urlCss = getClass().getResource("/com/losalerces/sistematurnos/style.css").toExternalForm();
            alerta.getDialogPane().getStylesheets().add(urlCss);
        } catch (Exception e) {
            System.out.println("[Alerta] No se pudo aplicar el archivo style.css: " + e.getMessage());
        }

        // 2. LLAMAMOS A TU MÉTODO PARA PONERLE EL ICONO DE LA MONTAÑA
        asignarIcono((javafx.stage.Stage) alerta.getDialogPane().getScene().getWindow());

        alerta.showAndWait();
    }
}