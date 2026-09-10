package com.losalerces.sistematurnos.Controladores;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class controladorMenuprincipal implements Initializable {

    @FXML
    private StackPane contenedorPrincipal;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblHora;

    @FXML
    public ImageView iv;

    private void actualizarFechaHora() {
        Locale locale = new Locale("es", "AR");

        DateTimeFormatter formatoFecha =
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", locale);

        DateTimeFormatter formatoHora =
                DateTimeFormatter.ofPattern("HH:mm");

        String fecha = LocalDate.now().format(formatoFecha);
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);

        lblFecha.setText(fecha);
        lblHora.setText(LocalTime.now().format(formatoHora));
    }

    @FXML
    private void mostrarTurnos() {
        cargarVista("turnos.fxml");
    }

    @FXML
    private void mostrarDoctores() {
        cargarVista("doctor.fxml");
    }



    private void cargarVista(String archivo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/losalerces/sistematurnos/" + archivo)
            );

            Parent vista = loader.load();
            contenedorPrincipal.getChildren().setAll(vista);

        } catch (IOException e) {
            System.out.println("Error cargando: " + archivo);
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actualizarFechaHora();

        Timeline reloj = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> actualizarFechaHora())
        );
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();

        java.io.InputStream stream = getClass().getResourceAsStream("/imagen/logo_clinica.png");

        if (stream != null) {
            iv.setImage(new Image(stream));
        } else {
            System.err.println("Error crítico: No se encontró el archivo");
        }
    }

    public void mostrarPacientes(ActionEvent actionEvent) {
    }
}