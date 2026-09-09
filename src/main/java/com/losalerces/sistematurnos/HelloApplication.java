package com.losalerces.sistematurnos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("menuprincipal.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 950, 620);

        stage.setTitle("Clínica Los Alerces");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(620);
        stage.show();
    }
}