package com.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * The Main class serves as the entry point for the checkers game application.
 * It initializes and displays the primary stage and scene.
 */
public class Main extends Application {

    /**
     * @param stage The primary stage for this application, onto which
     *              the application scene can be set.
     * @throws IOException If the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 520);
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch();
    }
}