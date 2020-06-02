package com.nicktackes;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EventEditor {

    @FXML
    private Label alertLabel;
    @FXML
    private ComboBox<String> timelineChoice = null;
    @FXML
    private TextField eventName = null;
    @FXML
    private TextField startYear = null;
    @FXML
    private TextField startMonth = null;
    @FXML
    private TextField startDay = null;
    @FXML
    private TextField endYear = null;
    @FXML
    private TextField endMonth = null;
    @FXML
    private TextField endDay = null;
    @FXML
    private TextArea eventDescription = null;
    @FXML
    private void initialize() {
        Tooltip tip = new Tooltip("Enter a negative year for BC.");
        tip.setShowDelay(Duration.seconds(0.5));
        startYear.setTooltip(tip);
        endYear.setTooltip(tip);
//        Tooltip descriptionTip = new Tooltip("No special characters or diacritics, sorry!");
//        descriptionTip.setShowDelay(Duration.seconds(0.5));
//        eventDescription.setTooltip(descriptionTip);
//        eventName.setTooltip(descriptionTip);
    }

    public String[] timelineList = Utilities.timelinePath.list();

    public boolean endDateIsEmpty() {
       return (endYear.getText().isBlank() || endMonth.getText().isBlank() || endDay.getText().isBlank());
    }

    @FXML
    private void onClick() throws IOException {

        if (eventName.getText().trim().isEmpty()) {

            alertLabel.setVisible(true);
            alertLabel.setText("Please Enter an Event Name.");

        } else if (eventDescription.getText().trim().isEmpty()) {

            alertLabel.setVisible(true);
            alertLabel.setText("Please Enter an Event Description.");

        } else if (timelineChoice.getValue() == null) {

            alertLabel.setVisible(true);
            alertLabel.setText("Please Select a Timeline.");

        } else if (startYear.getText().trim().isEmpty() || startMonth.getText().trim().isEmpty() || startDay.getText().trim().isEmpty()) {

            alertLabel.setVisible(true);
            alertLabel.setText("Please Enter an Event Start Date.");

        } else if (!formatChecker()) {

            alertLabel.setVisible(true);
            alertLabel.setText("Invalid input. Please enter a valid date.");

        } else if (!endDateIsEmpty() && !altFormatChecker()) {

            alertLabel.setVisible(true);
            alertLabel.setText("Invalid input. Please enter a valid end date or leave blank.");

        } else {

            String modDescription = eventDescription.getText().replace("\n", "##");
            eventDescription.setText(modDescription);

            int year1 = Integer.parseInt(startYear.getText());
            int year2 = 1;

            String startDate = startYear.getText() + "-" + startMonth.getText() + "-" + startDay.getText();
            String endDate = endYear.getText() + "-" + endMonth.getText() + "-" + endDay.getText();

            if (!endDateIsEmpty()) {
                year2 = Integer.parseInt(startYear.getText());
            }

            if (year1 < 0) {
                startDate = (year1 + 1) + "-" + startMonth.getText() + "-" + startDay.getText();
            }

            if (year2 < 0) {
                endDate = (year2 + 1) + "-" + startMonth.getText() + "-" + startDay.getText();
            }

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(Utilities.timelinePath + "/" + timelineChoice.getValue() + ".txt", true)  //Set true for append mode
            );

            if (endDateIsEmpty()) {
                writer.write(eventName.getText() + "%%" + startDate + "%%" + startDate + "%%" + eventDescription.getText());
            } else {
                writer.write(eventName.getText() + "%%" + startDate + "%%" + endDate + "%%" + eventDescription.getText());
            }
            writer.newLine();
            writer.close();

            // Refresh Window

            eventName.clear();
            startYear.clear();
            startMonth.clear();
            startDay.clear();
            endYear.clear();
            endMonth.clear();
            endDay.clear();
            eventDescription.clear();

            // Success Label

            alertLabel.setText("Event created!");
            alertLabel.setVisible(true);

            PauseTransition visiblePause = new PauseTransition(
                    Duration.seconds(3)
            );
            visiblePause.setOnFinished(
                    event -> alertLabel.setVisible(false)
            );
            visiblePause.play();

        }
    }

    private boolean formatChecker(){
        return (Utilities.isYear(startYear.getText()) &&
                Utilities.isMonth(startMonth.getText()) &&
                Utilities.isDay(startDay.getText()));
    }

    private boolean altFormatChecker(){
        return (Utilities.isYear(endYear.getText()) &&
                Utilities.isMonth(endMonth.getText()) &&
                Utilities.isDay(endDay.getText()));
    }

    @FXML
    private void setData(){

        timelineChoice.getItems().clear();

        for (String s : timelineList) {
            String fileName = s.substring(0, s.length() - 4);
            timelineChoice.getItems().add(fileName);
        }

    }


}
