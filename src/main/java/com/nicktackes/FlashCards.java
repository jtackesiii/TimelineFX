package com.nicktackes;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FlashCards {

    @FXML
    private ComboBox<String> timelineChoice;
    @FXML
    private VBox flashCardPane;
    @FXML
    private TextField titleBox;
    @FXML
    private TextField dateBox;
    @FXML
    private TextArea descriptionBox;
    @FXML
    private Label score;
    @FXML
    private Button runButton;
    @FXML
    private Button correct;
    @FXML
    private Button incorrect;

    public String[] timelineList = Utilities.timelinePath.list();

    public int questionBank = 0;
    public int answerCount = 0;
    public int correctAnswers = 0;

    public ArrayList<TimelineEvent> questionList = new ArrayList<>();

    public int questionIndex;
    public int missingPiece;

    public void setQuestionIndex(){
        this.questionIndex = (int) (Math.random() * (questionBank + 1) - 1);
    }

    public void setMissingPiece(){
        this.missingPiece = (int) (Math.random() * 3);
    }

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

        descriptionBox.getScene().lookup("#descriptionBox .content").setCursor(Cursor.HAND);

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
        while (scanner.hasNextLine()) {
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
        for (i = 0; i < row; i++) {
            String eventTitle = activeTimeline[i][0];
            LocalDate startDate = LocalDate.parse(activeTimeline[i][1], parseDates);
            LocalDate endDate = LocalDate.parse(activeTimeline[i][2], parseDates);
            String modDescription = activeTimeline[i][3].replace("##", "\n");
            Boolean isYearOnly = Boolean.parseBoolean(activeTimeline[i][4]);

            event[i] = new TimelineEvent(eventTitle, startDate, endDate, modDescription, isYearOnly);
            questionList.add(event[i]);
        }

        questionBank = event.length;

        //Make Flashcard

        setQuestionIndex();
        setMissingPiece();

        fillFlashCard(questionIndex);

        if(missingPiece == 1) {
            titleBox.setText("What event was this?");
            titleBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        } else if (missingPiece == 2) {
            dateBox.setText("When was this?");
            dateBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        } else {
            descriptionBox.setText("What happened?");
            descriptionBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        }

        flashCardPane.setVisible(true);

    }

    public void rightAnswer(ActionEvent actionEvent) {
        correctAnswers++;
        answerCount++;
        score.setText("Score: " + correctAnswers + "/" + answerCount);

        setQuestionIndex();
        setMissingPiece();

        titleBox.setStyle("-fx-text-fill: #f7f7ff");
        dateBox.setStyle("-fx-text-fill: #f7f7ff");
        descriptionBox.setStyle("-fx-text-fill: #f7f7ff");

        fillFlashCard(questionIndex);

        if(missingPiece == 1) {
            titleBox.setText("What event was this?");
            titleBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        } else if (missingPiece == 2) {
            dateBox.setText("When was this?");
            dateBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        } else {
            descriptionBox.setText("What happened?");
            descriptionBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        }
    }

    public void wrongAnswer(ActionEvent actionEvent) {
        answerCount++;
        score.setText("Score: " + correctAnswers + "/" + answerCount);

        setQuestionIndex();
        setMissingPiece();

        titleBox.setStyle("-fx-text-fill: #f7f7ff");
        dateBox.setStyle("-fx-text-fill: #f7f7ff");
        descriptionBox.setStyle("-fx-text-fill: #f7f7ff");

        fillFlashCard(questionIndex);

        if(missingPiece == 1) {
            titleBox.setText("What event was this?");
            titleBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        } else if (missingPiece == 2) {
            dateBox.setText("When was this?");
            titleBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        } else {
            descriptionBox.setText("What happened?");
            descriptionBox.addEventHandler(MouseEvent.MOUSE_CLICKED, answerQuery);
        }
    }

    public void wrongAnswerInit(ActionEvent actionEvent) {

        fillFlashCard(questionIndex);

        if(missingPiece == 1) {
            titleBox.setStyle("-fx-text-fill: #fe5f55");
        } else if (missingPiece == 2) {
            dateBox.setStyle("-fx-text-fill: #fe5f55");
        } else {
            descriptionBox.setStyle("-fx-text-fill: #fe5f55");
        }


        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(1)
        );
        visiblePause.setOnFinished(
                this::wrongAnswer
        );
        visiblePause.play();
    }

    public void rightAnswerInit(ActionEvent actionEvent) {

        fillFlashCard(questionIndex);

        if(missingPiece == 1) {
            titleBox.setStyle("-fx-text-fill: #599556");
        } else if (missingPiece == 2) {
            dateBox.setStyle("-fx-text-fill: #599556");
        } else {
            descriptionBox.setStyle("-fx-text-fill: #599556");
        }


        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(1)
        );
        visiblePause.setOnFinished(
                this::rightAnswer
        );
        visiblePause.play();
    }

    private void fillFlashCard(int questionIndex) {
        titleBox.setText(questionList.get(questionIndex).title);
        if(questionList.get(questionIndex).startDate.getYear() < 1000) {
            if (questionList.get(questionIndex).hasRange()) {
                if (questionList.get(questionIndex).isYearOnly) {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("y GG")) + "-" + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("y GG")));
                }else if (questionList.get(questionIndex).sameMonth()) {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d")) + "-" + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("d, y GG")));
                } else if (questionList.get(questionIndex).sameYear()) {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d")) + " - " + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("MMMM d, y GG")));
                } else {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d, y GG")) + " - " + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("MMMM d, y GG")));
                }
            } else if (questionList.get(questionIndex).isYearOnly) {
                dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("y GG")));
            } else {
                dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d, y GG")));
            }
        } else {
            if (questionList.get(questionIndex).hasRange()) {
                if (questionList.get(questionIndex).isYearOnly) {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("y")) + "-" + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("y")));
                } if (questionList.get(questionIndex).sameMonth()) {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d")) + "-" + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("d, y")));
                } else if (questionList.get(questionIndex).sameYear()) {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d")) + " - " + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("MMMM d, y")));
                } else {
                    dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d, y")) + " - " + questionList.get(questionIndex).endDate.format(DateTimeFormatter.ofPattern("MMMM d, y")));
                }
            } else {
                dateBox.setText(questionList.get(questionIndex).startDate.format(DateTimeFormatter.ofPattern("MMMM d, y")));
            }
        }
        descriptionBox.setText(questionList.get(questionIndex).description);
    }

    EventHandler<MouseEvent> answerQuery = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            fillFlashCard(questionIndex);

            if(missingPiece == 1) {
                titleBox.setStyle("-fx-text-fill: #fe5f55");
                titleBox.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
            } else if (missingPiece == 2) {
                dateBox.setStyle("-fx-text-fill: #fe5f55");
                dateBox.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
            } else {
                descriptionBox.setStyle("-fx-text-fill: #fe5f55");
                descriptionBox.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
            }

        }
    };

}