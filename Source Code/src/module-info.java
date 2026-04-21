module FoundationsF25 {
    requires javafx.controls;
    requires java.sql;
    requires javafx.graphics; 
   // requires org.junit.jupiter.api;
	requires javafx.base;
	requires org.junit.jupiter.api;

    opens applicationMain to javafx.graphics, javafx.fxml;
}