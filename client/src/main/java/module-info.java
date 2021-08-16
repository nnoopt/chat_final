module com.example.client_next {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires commands;


    opens com.example.client_next to javafx.fxml;
    exports com.example.client_next;
}