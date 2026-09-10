package com.losalerces.sistematurnos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AbmDoctorView.fxml"));

        // 1. Quitamos los tamaños fijos (320, 240) para que la escena use el tamaño del FXML
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Gestión de Clínica - Doctores");
        stage.setScene(scene);

        // 2. ¡ESTA LÍNEA HACE QUE SALGA EN TODA LA PANTALLA MAXIMIZADA!
        stage.setMaximized(true);

        stage.show();
    }
}
