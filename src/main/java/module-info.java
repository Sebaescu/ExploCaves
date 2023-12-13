module com.sebaescu.mavenproject1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.sebaescu.mavenproject1 to javafx.fxml;
    exports com.sebaescu.mavenproject1;
}
