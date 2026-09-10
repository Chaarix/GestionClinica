package com.losalerces.sistematurnos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

@Override
public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("obrasociales.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagen/montaña_clinica.png")));
    stage.setTitle("Clinica Los Alerces");
    stage.setScene(scene);
    stage.show();
    }
}


