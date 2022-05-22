module pl.edu.pg.eti {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires org.apache.commons.configuration2;
    requires java.sql;
    requires json.simple;
    requires vavr;
    requires org.apache.commons.lang3;
    requires com.google.common;

    exports pl.edu.pg.eti;
    exports pl.edu.pg.eti.gui;
    exports pl.edu.pg.eti.gui.controller;
    exports pl.edu.pg.eti.gui.control;
    exports pl.edu.pg.eti.backend.event;
    exports pl.edu.pg.eti.backend.connection;

    opens pl.edu.pg.eti.gui.controller to javafx.fxml;
}