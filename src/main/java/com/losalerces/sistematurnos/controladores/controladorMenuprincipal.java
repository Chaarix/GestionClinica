package com.losalerces.sistematurnos.Controladores;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class controladorMenuprincipal {

    @FXML
    private StackPane contenedorPrincipal;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFechaAgenda;

    @FXML
    private ImageView imageView;


    // =============================
    // TABLA
    // =============================

    @FXML
    private TableView<TurnoFila> tablaAgenda;

    @FXML
    private TableColumn<TurnoFila, String> colHora;

    @FXML
    private TableColumn<TurnoFila, String> colPaciente;

    @FXML
    private TableColumn<TurnoFila, String> colDoctor;

    @FXML
    private TableColumn<TurnoFila, String> colEspecialidad;

    @FXML
    private TableColumn<TurnoFila, String> colEstado;

    @FXML
    private TableColumn<TurnoFila, Void> colAcciones;


    private final ObservableList<TurnoFila> turnos =
            FXCollections.observableArrayList();


    // =============================
    // INICIALIZACIÓN
    // =============================

    @FXML
    public void initialize() {

        cargarLogo();

        configurarTabla();

        actualizarFechaHora();

        /*
         * Datos temporales para visualizar la agenda.
         *
         * Después reemplazamos este método
         * por cargarTurnosDesdeBD().
         */
        cargarDatosDePrueba();

        actualizarEstados();


        // Actualiza reloj y agenda automáticamente

        Timeline reloj = new Timeline(

                new KeyFrame(
                        Duration.seconds(30),
                        event -> {

                            actualizarFechaHora();
                            actualizarEstados();

                        }
                )
        );

        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }


    // =============================
    // LOGO
    // =============================

    private void cargarLogo() {

        var url = getClass().getResource(
                "/com/losalerces/sistematurnos/imagen/logo_clinica.png"
        );

        if (url != null) {

            Image imagen = new Image(url.toExternalForm());

            imageView.setImage(imagen);

            /*
             * Tu imagen tiene bastante espacio blanco.
             * Esto recorta visualmente ese borde.
             */

            if (imagen.getWidth() > 500 &&
                    imagen.getHeight() > 300) {

                imageView.setViewport(
                        new Rectangle2D(
                                35,
                                35,
                                imagen.getWidth() - 70,
                                imagen.getHeight() - 70
                        )
                );
            }

        } else {

            System.err.println(
                    "No se encontró logo_clinica.png"
            );
        }
    }


    // =============================
    // FECHA / HORA
    // =============================

    private void actualizarFechaHora() {

        Locale locale =
                new Locale("es", "AR");


        DateTimeFormatter formatoFecha =
                DateTimeFormatter.ofPattern(
                        "EEEE, d 'de' MMMM 'de' yyyy",
                        locale
                );


        DateTimeFormatter formatoHora =
                DateTimeFormatter.ofPattern("HH:mm");


        String fecha =
                LocalDate.now().format(formatoFecha);


        fecha =
                fecha.substring(0, 1).toUpperCase()
                        + fecha.substring(1);


        lblFecha.setText(fecha);

        lblFechaAgenda.setText(fecha);

        lblHora.setText(
                LocalTime.now().format(formatoHora)
        );
    }


    // =============================
    // CONFIGURACIÓN TABLA
    // =============================

    private void configurarTabla() {

        colHora.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getHoraTexto()
                        )
        );


        colPaciente.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getPaciente()
                        )
        );


        colDoctor.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getDoctor()
                        )
        );


        colEspecialidad.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getEspecialidad()
                        )
        );


        colEstado.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue().getEstado()
                        )
        );


        configurarColumnaEstado();

        configurarColumnaAcciones();


        tablaAgenda.setItems(turnos);

        tablaAgenda.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
    }


    // =============================
    // COLUMNA ESTADO
    // =============================

    private void configurarColumnaEstado() {

        colEstado.setCellFactory(columna ->
                new TableCell<>() {

                    private final Label etiqueta =
                            new Label();


                    @Override
                    protected void updateItem(
                            String estado,
                            boolean empty
                    ) {

                        super.updateItem(
                                estado,
                                empty
                        );


                        if (empty || estado == null) {

                            setGraphic(null);

                            return;
                        }


                        etiqueta.setText(estado);

                        etiqueta
                                .getStyleClass()
                                .setAll("estado");


                        switch (estado) {

                            case "Atendido" ->
                                    etiqueta
                                            .getStyleClass()
                                            .add("estado-atendido");

                            case "En curso" ->
                                    etiqueta
                                            .getStyleClass()
                                            .add("estado-curso");

                            case "Próximo" ->
                                    etiqueta
                                            .getStyleClass()
                                            .add("estado-proximo");

                            case "Disponible" ->
                                    etiqueta
                                            .getStyleClass()
                                            .add("estado-disponible");

                            default ->
                                    etiqueta
                                            .getStyleClass()
                                            .add("estado-programado");
                        }


                        setGraphic(etiqueta);
                    }
                }
        );
    }


    // =============================
    // COLUMNA ACCIONES
    // =============================

    private void configurarColumnaAcciones() {

        colAcciones.setCellFactory(columna ->
                new TableCell<>() {

                    private final Button boton =
                            new Button("Ver");


                    {
                        boton
                                .getStyleClass()
                                .add("boton-tabla");


                        boton.setOnAction(event -> {

                            TurnoFila turno =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());


                            System.out.println(
                                    "Turno seleccionado: "
                                            + turno.getPaciente()
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

                            setGraphic(boton);
                        }
                    }
                }
        );
    }


    // =============================
    // ACTUALIZACIÓN EN TIEMPO REAL
    // =============================

    private void actualizarEstados() {

        LocalTime ahora =
                LocalTime.now();


        for (TurnoFila turno : turnos) {

            /*
             * Si la fila no tiene paciente,
             * consideramos que ese horario está libre.
             */

            if (turno.getPaciente() == null ||
                    turno.getPaciente().isBlank()) {

                turno.setEstado(
                        "Disponible"
                );

                continue;
            }


            LocalTime inicio =
                    turno.getHora();


            LocalTime fin =
                    inicio.plusMinutes(
                            turno.getDuracionMinutos()
                    );


            if (!ahora.isBefore(fin)) {

                turno.setEstado(
                        "Atendido"
                );

            } else if (
                    !ahora.isBefore(inicio)
                            && ahora.isBefore(fin)
            ) {

                turno.setEstado(
                        "En curso"
                );

            } else {

                long minutos =
                        java.time.Duration
                                .between(
                                        ahora,
                                        inicio
                                )
                                .toMinutes();


                if (minutos >= 0 &&
                        minutos <= 30) {

                    turno.setEstado(
                            "Próximo"
                    );

                } else {

                    turno.setEstado(
                            "Programado"
                    );
                }
            }
        }


        tablaAgenda.refresh();
    }


    // =============================
    // DATOS TEMPORALES
    // =============================

    private void cargarDatosDePrueba() {

        /*
         * Esto es solamente para poder
         * ver la tabla funcionando ahora.
         *
         * Cuando conectemos el DAO,
         * este método se reemplaza.
         */

        LocalTime ahora =
                LocalTime.now();


        int minuto =
                ahora.getMinute() < 30
                        ? 0
                        : 30;


        LocalTime base =
                LocalTime.of(
                                ahora.getHour(),
                                minuto
                        )
                        .minusMinutes(90);


        turnos.clear();


        turnos.add(
                new TurnoFila(
                        base,
                        "Carlos Méndez",
                        "Dr. López",
                        "Clínica Médica",
                        30
                )
        );


        turnos.add(
                new TurnoFila(
                        base.plusMinutes(30),
                        "Sofía Torres",
                        "Dra. Fernández",
                        "Pediatría",
                        30
                )
        );


        turnos.add(
                new TurnoFila(
                        base.plusMinutes(60),
                        "María González",
                        "Dr. López",
                        "Clínica Médica",
                        30
                )
        );


        turnos.add(
                new TurnoFila(
                        base.plusMinutes(90),
                        "Lucía Martínez",
                        "Dra. Pérez",
                        "Cardiología",
                        30
                )
        );


        turnos.add(
                new TurnoFila(
                        base.plusMinutes(120),
                        "Ana Ruiz",
                        "Dra. Pérez",
                        "Cardiología",
                        30
                )
        );


        turnos.add(
                new TurnoFila(
                        base.plusMinutes(150),
                        "",
                        "Dr. López",
                        "Clínica Médica",
                        30
                )
        );


        turnos.add(
                new TurnoFila(
                        base.plusMinutes(180),
                        "Matías Rojas",
                        "Dr. López",
                        "Clínica Médica",
                        30
                )
        );
    }


    // =============================
    // CAMBIO DE VISTAS
    // =============================

    @FXML
    private void mostrarTurnos() {

        cargarVista(
                "turnos.fxml"
        );
    }


    @FXML
    private void mostrarDoctores() {

        cargarVista(
                "doctores.fxml"
        );
    }


    @FXML
    private void mostrarPacientes() {

        cargarVista(
                "pacientes.fxml"
        );
    }


    private void cargarVista(
            String archivo
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/losalerces/sistematurnos/"
                                            + archivo
                            )
                    );


            Parent vista =
                    loader.load();


            contenedorPrincipal
                    .getChildren()
                    .setAll(vista);


        } catch (IOException e) {

            System.err.println(
                    "Error cargando: "
                            + archivo
            );

            e.printStackTrace();
        }
    }


    // =================================================
    // CLASE PARA LAS FILAS DE LA AGENDA
    // =================================================

    public static class TurnoFila {

        private LocalTime hora;

        private String paciente;

        private String doctor;

        private String especialidad;

        private String estado;

        private int duracionMinutos;


        public TurnoFila(
                LocalTime hora,
                String paciente,
                String doctor,
                String especialidad,
                int duracionMinutos
        ) {

            this.hora = hora;

            this.paciente = paciente;

            this.doctor = doctor;

            this.especialidad = especialidad;

            this.duracionMinutos =
                    duracionMinutos;

            this.estado =
                    "Programado";
        }


        public LocalTime getHora() {
            return hora;
        }


        public String getHoraTexto() {

            return hora.format(
                    DateTimeFormatter.ofPattern(
                            "HH:mm"
                    )
            );
        }


        public String getPaciente() {
            return paciente;
        }


        public String getDoctor() {
            return doctor;
        }


        public String getEspecialidad() {
            return especialidad;
        }


        public String getEstado() {
            return estado;
        }


        public void setEstado(
                String estado
        ) {
            this.estado = estado;
        }


        public int getDuracionMinutos() {
            return duracionMinutos;
        }
    }
}