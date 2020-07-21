package View;

import Model.TimeClass;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame extends Application {

    public static int WIDTH = 350;
    public static int HEIGHT = 9 * WIDTH / 16;

    private int totalTimeSeconds;
    static Timer timer;

    @FXML
    private MenuItem quit_menu_item;
    @FXML
    private MenuItem about_menu_item;
    @FXML
    private Label hour_label;
    @FXML
    private Label minute_label;
    @FXML
    private Label second_label;
    @FXML
    private Button start_button;
    @FXML
    private Button abort_button;
    @FXML
    private ChoiceBox task_choice_box;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("shutdown_scheduler.fxml")));
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add("stylesheet.css");
        setStageProperties(stage, scene);
    }

    @FXML
    void initialize() {
        setTimeLabelFonts();
        totalTimeSeconds = Integer.parseInt(hour_label.getText()) * 3600 + Integer.parseInt(minute_label.getText()) * 60 + Integer.parseInt(second_label.getText());
        setLabelMouseClickedEvent();
        setStartAbortActions();
        String[] st = {"Shutdown", "Lock", "Sleep", "Restart", "Hibernate"};
        task_choice_box.setItems(FXCollections.observableArrayList(st));
        setMenuItemActions();
    }

    private void setMenuItemActions() {
        quit_menu_item.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });
        about_menu_item.setOnAction(actionEvent -> {
            final Stage aboutStage = new Stage();
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.getIcons().add(new Image("shutdown-4.png"));

            Parent aboutRoot = null;
            try {
                aboutRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("about_popup.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert aboutRoot != null;
            Scene aboutScene = new Scene(aboutRoot, WIDTH, HEIGHT);
            aboutScene.getStylesheets().add("stylesheet.css");

            aboutStage.setTitle("About");
            aboutStage.setScene(aboutScene);
            aboutStage.setResizable(false);
            aboutStage.show();
        });
    }


    private void setStageProperties(Stage stage, Scene scene) {
        stage.setTitle("Countdown - Scheduler");
        stage.getIcons().add(new Image("shutdown-4.png"));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    private void setStartAbortActions() {
        start_button.setOnAction(actionEvent -> {
            createAndStartTimer();
            start_button.setDisable(true);
            abort_button.setDisable(false);
        });
        abort_button.setOnAction(actionEvent -> {
            timer.cancel();
            start_button.setDisable(false);
            abort_button.setDisable(true);
        });
    }

    private void setLabelMouseClickedEvent() {
        hour_label.setOnMouseClicked(mouseEvent -> showTimeSelectionPopup(mouseEvent, hour_label, "Hour"));
        minute_label.setOnMouseClicked(mouseEvent -> showTimeSelectionPopup(mouseEvent, minute_label, "Minute"));
        second_label.setOnMouseClicked(mouseEvent -> showTimeSelectionPopup(mouseEvent, second_label, "Second"));
    }

    private void setTimeLabelFonts() {
        hour_label.setFont(Font.loadFont(Objects.requireNonNull(getClass().getClassLoader().getResource("DigitalDisplay.ttf")).toExternalForm(), 100));
        minute_label.setFont(Font.loadFont(Objects.requireNonNull(getClass().getClassLoader().getResource("DigitalDisplay.ttf")).toExternalForm(), 100));
        second_label.setFont(Font.loadFont(Objects.requireNonNull(getClass().getClassLoader().getResource("DigitalDisplay.ttf")).toExternalForm(), 100));
    }

    private void showTimeSelectionPopup(MouseEvent mouseEvent, Label label, String timeType) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            final Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(((Node) mouseEvent.getSource()).getScene().getWindow());
            dialogStage.getIcons().add(new Image("shutdown-4.png"));

            Parent dialogRoot = null;
            try {
                dialogRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("time_selection_popup.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert dialogRoot != null;
            Scene dialogScene = new Scene(dialogRoot, WIDTH, HEIGHT);
            dialogScene.getStylesheets().add("stylesheet.css");


            TextField timeTextField = (TextField) dialogScene.lookup("#time_text_field");
            timeTextField.setFont(Font.loadFont(Objects.requireNonNull(getClass().getClassLoader().getResource("DigitalDisplay.ttf")).toExternalForm(), 74));
            timeTextField.setText(label.getText());

            Button doneButton = (Button) dialogScene.lookup("#done_time_button");
            doneButton.setOnAction(actionEvent -> {
                label.setText(timeTextField.getText());
                totalTimeSeconds = Integer.parseInt(hour_label.getText()) * 3600 + Integer.parseInt(minute_label.getText()) * 60 + Integer.parseInt(second_label.getText());
                dialogStage.close();
            });

            int timeChunk = switch (timeType) {
                case "Hour" -> 1;
                case "Minute" -> 5;
                case "Second" -> 10;
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
                if (newTime < 0) {
                    newTime = 0;
                }
                if (newTime < 10) {
                    timeTextField.setText("0".concat(String.valueOf(newTime)));
                } else {
                    timeTextField.setText(String.valueOf(newTime));
                }

            });
            dialogStage.setTitle("Set the " + timeType);
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(false);
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

                    if (totalTimeSeconds == 0) {
                        Runtime runtime = Runtime.getRuntime();
                        try {
                            runtime.exec(getDesiredCommand());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }

                    TimeClass timeClass = new TimeClass(totalTimeSeconds);

                    int seconds = timeClass.getSeconds();
                    if (seconds < 10) {
                        second_label.setText("0".concat(String.valueOf(seconds)));
                    } else {
                        second_label.setText(String.valueOf(seconds));
                    }

                    int minutes = timeClass.getMinutes();
                    if (minutes < 10) {
                        minute_label.setText("0".concat(String.valueOf(minutes)));
                    } else {
                        minute_label.setText(String.valueOf(minutes));
                    }

                    int hours = timeClass.getHours();
                    if (hours < 10) {
                        hour_label.setText("0".concat(String.valueOf(hours)));
                    } else {
                        hour_label.setText(String.valueOf(hours));
                    }
                });
            }

            private String getDesiredCommand() {
                String chosenTask = (String) task_choice_box.getValue();
                if (chosenTask == null) {
                    return "shutdown -s -t 0";
                }
                return switch (chosenTask) {
                    case "Sleep" -> "rundll32 powrprof.dll,SetSuspendState 0,1,0";
                    case "Restart" -> "shutdown -r -t 0";
                    case "Hibernate" -> "rundll32 PowrProf.dll,SetSuspendState";
                    case "Lock" -> "rundll32 User32.dll,LockWorkStation";
                    default -> "shutdown -s -t 0";
                };
            }
        }, delay, period);
    }
}
