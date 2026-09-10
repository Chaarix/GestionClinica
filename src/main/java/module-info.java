module com.losalerces.sistematurnos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.sql;

    opens com.losalerces.sistematurnos to javafx.fxml;
    opens com.losalerces.sistematurnos.controladores to javafx.fxml;

    exports com.losalerces.sistematurnos;
}