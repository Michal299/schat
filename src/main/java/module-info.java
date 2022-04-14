module pl.edu.pg.eti {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires org.apache.commons.configuration2;
    requires java.sql;

    exports pl.edu.pg.eti;
    exports pl.edu.pg.eti.gui;
    exports pl.edu.pg.eti.gui.controller;
}