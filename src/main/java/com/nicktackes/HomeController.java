package com.nicktackes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static com.nicktackes.App.loadFXML;

public class HomeController {
    @FXML
    private BorderPane mainPane;
    @FXML
    private MenuItem homeButton;
    @FXML
    private MenuItem newTLMenuButton;
    @FXML
    private MenuItem editTLMenuButton;
    @FXML
    private MenuItem viewTLMenuButton;
    @FXML
    private MenuItem FCMenuButton;
    @FXML
    private Button newTLButton;
    @FXML
    private Button editTLButton;
    @FXML
    private Button viewTLButton;
    @FXML
    private Button flashCardButton;
    @FXML
    private Button exitButton;

    @FXML
    private void closeProgram() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void newTimeline() throws Exception {

        Utilities.initTimelineDir();
        Utilities.mozoomdar();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialDirectory(Utilities.timelinePath);

        Stage stage = (Stage) newTLButton.getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    public void newEvent() throws Exception {

        Utilities.initTimelineDir();
        Utilities.mozoomdar();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        //Title the Stage
        stage.setTitle("Event Editor");

        //Set the Scene

        Scene scene = new Scene(loadFXML("event-editor"));

        scene.getStylesheets().add(String.valueOf(App.class.getResource( "event-editor.css")));
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void viewTimeline() throws Exception {

        Utilities.initTimelineDir();
        Utilities.mozoomdar();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        //Title the Stage
        stage.setTitle("TimelineFX");

        //Set the Scene

        Scene scene = new Scene(loadFXML("timeline"));

        scene.getStylesheets().add(String.valueOf(App.class.getResource( "timeline.css")));
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void runFlashCards() throws Exception {

        Utilities.initTimelineDir();
        Utilities.mozoomdar();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        //Title the Stage
        stage.setTitle("Flash Cards");

        //Set the Scene

        Scene scene = new Scene(loadFXML("flash-cards"));

        scene.getStylesheets().add(String.valueOf(App.class.getResource( "flash-cards.css")));
        stage.setScene(scene);
        stage.show();
    }
}
