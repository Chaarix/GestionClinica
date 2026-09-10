module com.losalerces.sistematurnos {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires java.sql;


    opens com.losalerces.sistematurnos to javafx.fxml;
    opens com.losalerces.sistematurnos.Clases to javafx.fxml;
    opens com.losalerces.sistematurnos.Controladores to javafx.fxml;

    exports com.losalerces.sistematurnos;
    exports com.losalerces.sistematurnos.Clases;
    exports com.losalerces.sistematurnos.Controladores;
}