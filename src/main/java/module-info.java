module com.checkers {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.checkers to javafx.fxml;
    exports com.checkers;
}