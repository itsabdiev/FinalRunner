module com.example.finalrunner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.finalrunner to javafx.fxml;
    exports com.example.finalrunner;
}