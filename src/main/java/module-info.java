module com.redvethomas.labsql {
    requires javafx.controls;
    requires javafx.base;
    requires java.sql;
    requires javafx.fxml;

    opens com.redvethomas.labsql to javafx.base;
    opens com.redvethomas.labsql.Model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports com.redvethomas.labsql;
}