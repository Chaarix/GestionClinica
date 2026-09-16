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
    // Dependencias de Apache POI necesarias
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    // Si llegaras a tener errores de reflexión en tiempo de ejecución con Apache POI / XMLBeans,
    // es posible que necesites abrir los paquetes o requerir java.desktop y xml.dom:

    requires java.xml;

    opens com.losalerces.sistematurnos to javafx.fxml;
    opens com.losalerces.sistematurnos.Controladores to javafx.fxml;

    exports com.losalerces.sistematurnos;
}