package View;

import Model.TimeClass;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

//TODO:: other functional extensions > https://www.howtogeek.com/656672/how-to-create-a-shutdown-icon-in-windows-10/
public class MainFrame extends Application {

    public static int WIDTH = 350;
    public static int HEIGHT = 9 * WIDTH / 16;

    private int totalTimeSeconds;
    static Timer timer;

    Label hourLabel;
    Label minuteLabel;
    Label secondLabel;

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("shutdown_scheduler.fxml")));

        Scene scene = new Scene(root, WIDTH, HEIGHT);
//        scene.getStylesheets().add("stylesheet.css");

        hourLabel = (Label) scene.lookup("#hour_label");
        minuteLabel = (Label) scene.lookup("#minute_label");
        secondLabel = (Label) scene.lookup("#second_label");

        totalTimeSeconds = Integer.parseInt(hourLabel.getText()) * 3600 + Integer.parseInt(minuteLabel.getText()) * 60 + Integer.parseInt(secondLabel.getText());

        hourLabel.setOnMouseClicked(mouseEvent -> showTimeSelectionPopup(mouseEvent, stage, hourLabel, "Hour"));

        minuteLabel.setOnMouseClicked(mouseEvent -> showTimeSelectionPopup(mouseEvent, stage, minuteLabel, "Minute"));

        secondLabel.setOnMouseClicked(mouseEvent -> showTimeSelectionPopup(mouseEvent, stage, secondLabel, "Second"));

        Button startButton = (Button) scene.lookup("#start_button");
        startButton.setOnAction(actionEvent -> createAndStartTimer());

        Button abortButton = (Button) scene.lookup("#abort_button");
        abortButton.setOnAction(actionEvent -> timer.cancel());

        stage.setTitle("Shutdown countdown scheduler");
        stage.getIcons().add(new Image("shutdown-4.png"));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void showTimeSelectionPopup(MouseEvent mouseEvent, Stage stage, Label label, String timeType) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            final Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(stage);
            Parent dialogRoot = null;
            try {
                dialogRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("time_selection_popup.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert dialogRoot != null;
            Scene dialogScene = new Scene(dialogRoot, 300, 200);

            TextField timeTextField = (TextField) dialogScene.lookup("#time_text_field");
            timeTextField.setText(label.getText());

            Button doneButton = (Button) dialogScene.lookup("#done_time_button");
            doneButton.setOnAction(actionEvent -> {
                label.setText(timeTextField.getText());
                totalTimeSeconds = Integer.parseInt(hourLabel.getText()) * 3600 + Integer.parseInt(minuteLabel.getText()) * 60 + Integer.parseInt(secondLabel.getText());
                dialogStage.close();
            });

            int timeChunk = switch (timeType) {
                case "Hour" -> 1;
                case "Minute", "Second" -> 10;
                default -> 0;
            };

            Button plusButton = (Button) dialogScene.lookup("#plus_button");
            plusButton.setOnAction(actionEvent -> {
                int newTime = Integer.parseInt(timeTextField.getText()) + timeChunk;
                if (newTime < 10) {
                    timeTextField.setText("0".concat(String.valueOf(newTime)));
                } else {
                    timeTextField.setText(String.valueOf(newTime));
                }
            });
            Button minusButton = (Button) dialogScene.lookup("#minus_button");
            minusButton.setOnAction(actionEvent -> {
                int newTime = Integer.parseInt(timeTextField.getText()) - timeChunk;
                if (newTime >= 0) {
                    if (newTime < 10) {
                        timeTextField.setText("0".concat( String.valueOf(newTime)));
                    } else {
                        timeTextField.setText(String.valueOf(newTime));
                    }
                }
            });

            dialogStage.setTitle("Set the " + timeType);
            dialogStage.setScene(dialogScene);
            dialogStage.show();
        }
    }


    private void createAndStartTimer() {
        int delay = 1000;
        int period = 1000;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    totalTimeSeconds -= 1;

                    if (totalTimeSeconds == 5) {
                        Runtime runtime = Runtime.getRuntime();
                        try {
                            runtime.exec("shutdown -s -t 5"); // TODO:: REDUCE THIS TO 0 SECONDS
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }

                    TimeClass timeClass = new TimeClass(totalTimeSeconds);

                    int seconds = timeClass.getSeconds();
                    if (seconds < 10) {
                        secondLabel.setText("0".concat(String.valueOf(seconds)));
                    } else {
                        secondLabel.setText(String.valueOf(seconds));
                    }

                    int minutes = timeClass.getMinutes();
                    if (minutes < 10) {
                        minuteLabel.setText("0".concat(String.valueOf(minutes)));
                    } else {
                        minuteLabel.setText(String.valueOf(minutes));
                    }

                    int hours = timeClass.getHours();
                    if (hours < 10) {
                        hourLabel.setText("0".concat(String.valueOf(hours)));
                    } else {
                        hourLabel.setText(String.valueOf(hours));
                    }
                });
            }
        }, delay, period);
    }
}
