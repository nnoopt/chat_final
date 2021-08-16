module com.example.chat_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.chat_final to javafx.fxml;
    exports com.example.chat_final;
}