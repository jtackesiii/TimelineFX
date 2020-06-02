package com.nicktackes;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import java.io.*;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

public class Timeline {

    @FXML
    private ComboBox<String> timelineChoice;
    @FXML
    private VBox timelinePane;
    @FXML
    private Button runButton;
    @FXML
    private ListView<String> dateListView;

    private String[] timelineList = Utilities.timelinePath.list();

    @FXML
    private void setData() {

        timelineChoice.getItems().clear();

        for (String s : timelineList) {
            String fileName = s.substring(0, s.length() - 4);
            timelineChoice.getItems().add(fileName);
        }

    }

    public void printTimeline() throws IOException {
        timelineChoice.setVisible(false);
        runButton.setVisible(false);

        //Cull data
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(Utilities.timelinePath + "/" + timelineChoice.getValue() + ".txt")));
        int row = 0;
        while (scanner.hasNextLine()) {
            row++;
            scanner.nextLine();
        }
        scanner.close();

        scanner = new Scanner(new BufferedReader(new FileReader(Utilities.timelinePath + "/" + timelineChoice.getValue() + ".txt")));
        int col = 4;

        String[][] activeTimeline = new String[row][col];
        int i = 0;
        String next;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            activeTimeline[i] = line.split("%%");
            i++;
        }
        scanner.close();

        //Sort data by date
        Arrays.sort(activeTimeline, (entry1, entry2) -> {
            final String time1 = entry1[1];
            final String time2 = entry2[1];
            return time1.compareTo(time2);
        });

        //Event Class
        TimelineEvent[] event = new TimelineEvent[row];
        DateTimeFormatter parseDates = DateTimeFormatter.ofPattern("u-M-d");
        DateTimeFormatter noEra = DateTimeFormatter.ofPattern("y - MMMM d");
        DateTimeFormatter withEra = DateTimeFormatter.ofPattern("y GG - MMMM d");
        for (i = 0; i < row; i++) {
            String eventTitle = activeTimeline[i][0];
            LocalDate startDate = LocalDate.parse(activeTimeline[i][1], parseDates);
            LocalDate endDate = LocalDate.parse(activeTimeline[i][2], parseDates);
            String eventDescription = activeTimeline[i][3].replace("##", "\n");

            event[i] = new TimelineEvent(eventTitle, startDate, endDate, eventDescription);
            if (dateListView.getItems().contains(event[i].getStartDate().format(noEra))) {
                dateListView.getItems().add("... " + event[i].getTitle());
            } else if (dateListView.getItems().contains(event[i].getStartDate().format(withEra))) {
                dateListView.getItems().add("... " + event[i].getTitle());
            } else if (event[i].getStartDate().getYear() < 1000) {
                dateListView.getItems().add(event[i].getStartDate().format(withEra));
            } else {
                dateListView.getItems().add(event[i].getStartDate().format(noEra));
            }
        }

        //ListView on-click functionality
        DateTimeFormatter tileCE = DateTimeFormatter.ofPattern("MMMM d, y");
        DateTimeFormatter tileBCE = DateTimeFormatter.ofPattern("MMMM d, y GG");
        dateListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {

            timelinePane.getChildren().clear();
            int selectedItem = dateListView.getSelectionModel().getSelectedIndex();

            //Event Tile creator
            ScrollPane sp = new ScrollPane();
            VBox tile = new VBox(10);
            tile.setId(String.valueOf(selectedItem));
            //Tile Contents
            HBox editor = new HBox(10);
            Label editButton = new Label("Edit");
            editButton.getStyleClass().add("edit-button");
            Label deleteButton = new Label("Delete");
            deleteButton.getStyleClass().add("edit-button");
            Label alertLabel = new Label();
            alertLabel.setTextFill(Paint.valueOf("#fe5f55"));
            editor.getChildren().addAll(editButton, deleteButton, alertLabel);
            TextField title = new TextField(event[selectedItem].getTitle());
            HBox dates = new HBox(10);
            TextField firstDate = new TextField();
            TextField lastDate = new TextField();
            if (event[selectedItem].getStartDate().getYear() < 1000){
                firstDate.setText(event[selectedItem].getStartDate().format(tileBCE));
            } else {
                firstDate.setText(event[selectedItem].getStartDate().format(tileCE));
            }
            if (event[selectedItem].getEndDate().getYear() < 1000){
                lastDate.setText(event[selectedItem].getEndDate().format(tileBCE));
            } else {
                lastDate.setText(event[selectedItem].getEndDate().format(tileCE));
            }
            Label lastDatePrompt = new Label();
            lastDatePrompt.getStyleClass().add("edit-button");
            lastDatePrompt.setVisible(false);
            dates.getChildren().addAll(firstDate, lastDatePrompt, lastDate);
            TextArea description = new TextArea(event[selectedItem].getDescription());
            title.setEditable(false);
            firstDate.setEditable(false);
            lastDate.setEditable(false);
            description.setEditable(false);
            description.setWrapText(true);
            if (!event[selectedItem].hasRange()) {lastDate.setVisible(false);}
            Separator separator1 = new Separator(Orientation.HORIZONTAL);
            Separator separator2 = new Separator(Orientation.HORIZONTAL);

            //Edit, Delete, and DatePrompt Button functionality

            EventHandler<MouseEvent> deleteEvent = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    // All the important information
                    String inputFileName = Utilities.timelinePath + "/" + timelineChoice.getValue() + ".txt";
                    String outputFileName = Utilities.timelinePath + "/StringReplacer.txt";
                    String lineToRemove = activeTimeline[selectedItem][0] + "%%" + activeTimeline[selectedItem][1] + "%%" + activeTimeline[selectedItem][2] + "%%" + activeTimeline[selectedItem][3];;
                    // The traps any possible read/write exceptions which might occur
                    try {
                        File inputFile = new File(inputFileName);
                        File outputFile = new File(outputFileName);
                        // Open the reader/writer, this ensure that's encapsulated
                        // in a try-with-resource block, automatically closing
                        // the resources regardless of how the block exists
                        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                            // Read each line from the reader and compare it with
                            // with the line to remove and write if required
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                if (!line.equals(lineToRemove)) {
                                    writer.write(line);
                                    writer.newLine();
                                }
                            }
                        }

                        if (inputFile.delete()) {
                            // Rename the output file to the input file
                            if (!outputFile.renameTo(inputFile)) {
                                throw new IOException("Could not rename " + outputFileName + " to " + inputFileName);
                            }
                        } else {
                            throw new IOException("Could not delete original input file " + inputFileName);
                        }
                    } catch (IOException ex) {
                        // Handle any exceptions
                        ex.printStackTrace();
                    }
                    deleteButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                    deleteButton.setText("Please restart timeline to view changes.");
                    deleteButton.setTextFill(Paint.valueOf("#fe5f55"));
                }

            };

            EventHandler<MouseEvent> deleteConfirm = new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    deleteButton.setText("Confirm Delete Event");
                    deleteButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                    deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteEvent);
                }
            };

            EventHandler<MouseEvent> lastDateEdit = new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    lastDate.setVisible(true);
                    lastDatePrompt.setText("End Date: ");
                    lastDatePrompt.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                }
            };

            EventHandler<MouseEvent> updateEvent = new EventHandler<MouseEvent>(){
                public void handle(MouseEvent event){

                    String modDescription = description.getText().replace("\n", "##");
                    String oldString = activeTimeline[selectedItem][0] + "%%" + activeTimeline[selectedItem][1] + "%%" + activeTimeline[selectedItem][2] + "%%" + activeTimeline[selectedItem][3];
                    String newString = title.getText() + "%%" + firstDate.getText() + "%%" + lastDate.getText() + "%%" + modDescription;
                    if(lastDate.getText().isBlank()){
                        newString = title.getText() + "%%" + firstDate.getText() + "%%" + firstDate.getText() + "%%" + modDescription;
                    }
                    StringBuilder oldContent = new StringBuilder();

                    if( title.getText().isBlank() ) {
                        alertLabel.setText("Please enter an event name.");
                    } else if (description.getText().isBlank() ) {
                        alertLabel.setText("Please enter an event description.");
                    } else if (!Utilities.validDate(firstDate.getText())) {
                        alertLabel.setText("Please enter a valid start date.");
                    } else if (!Utilities.validDate(lastDate.getText()) && !lastDate.getText().isBlank()) {
                        alertLabel.setText("Please enter a valid end date.");
                    } else {

                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(Utilities.timelinePath + "/" + timelineChoice.getValue() + ".txt"));
                            String line = reader.readLine();

                            while (line != null) {
                                oldContent.append(line).append(System.lineSeparator());

                                line = reader.readLine();
                            }

                            String newContent = oldContent.toString().replaceAll(oldString, newString);

                            FileWriter writer = new FileWriter(Utilities.timelinePath + "/" + timelineChoice.getValue() + ".txt");

                            writer.write(newContent);

                            reader.close();
                            writer.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        title.setEditable(false);
                        firstDate.setEditable(false);
                        lastDate.setEditable(false);
                        lastDatePrompt.setVisible(false);
                        description.setEditable(false);
                        editButton.setText("Please restart timeline to view changes.");
                        editButton.setTextFill(Paint.valueOf("#fe5f55"));
                        editButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                    }
                }
            };

            EventHandler<MouseEvent> editEvent = new EventHandler<MouseEvent>(){
                public void handle(MouseEvent click){
                    title.setEditable(true);
                    firstDate.setEditable(true);
                    lastDate.setEditable(true);
                    Tooltip tip = new Tooltip("Enter a negative year for BC.");
                    tip.setShowDelay(Duration.seconds(0.5));
                    firstDate.setTooltip(tip);
                    lastDate.setTooltip(tip);
                    firstDate.setText(event[selectedItem].getStartDate().format(parseDates));
                    lastDate.setText(event[selectedItem].getEndDate().format(parseDates));
                    if (firstDate.getText().equals(lastDate.getText())) {
                        lastDatePrompt.setVisible(true);
                        lastDatePrompt.setText("Add end Date?");
                    }
//                    Tooltip descriptionTip = new Tooltip("No special characters or diacritics, sorry!");
//                    descriptionTip.setShowDelay(Duration.seconds(0.5));
                    description.setEditable(true);
//                    description.setTooltip(descriptionTip);
//                    title.setTooltip(descriptionTip);
                    editButton.setText("Update");
                    editButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                    editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, updateEvent);
                    //styles
                    title.setStyle("-fx-border-width: 1; -fx-border-radius: 5; -fx-border-color: #495867;");
                    description.setStyle("-fx-border-width: 1; -fx-border-radius: 5; -fx-border-color: #495867;");
                    firstDate.setStyle("-fx-border-width: 1; -fx-border-radius: 5; -fx-border-color: #495867;");
                    lastDate.setStyle("-fx-border-width: 1; -fx-border-radius: 5; -fx-border-color: #495867;");

                }
            };

            editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, editEvent);
            lastDatePrompt.addEventHandler(MouseEvent.MOUSE_CLICKED, lastDateEdit);
            deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteConfirm);


            //Print Tiles to Screen
            tile.getChildren().addAll(editor, title, separator1, dates, separator2, description);
            tile.getStyleClass().add("tile");
            sp.getStyleClass().add("tile-container");
            sp.fitToHeightProperty().set(true);
            sp.fitToWidthProperty().set(true);
            sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
            sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            sp.setContent(tile);
            timelinePane.getChildren().add(sp);

        });

        //set initial ListView selection
        dateListView.getSelectionModel().select(0);


    }

}
