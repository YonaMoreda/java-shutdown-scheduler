package View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame extends Application {

    public static int WIDTH = 350;
    public static int HEIGHT = 9 * WIDTH / 16;

    static int interval = 20;
    static Timer timer;
//    static Label secondLabel;

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("shutdown_scheduler.fxml")));

        Scene scene = new Scene(root, WIDTH, HEIGHT);
//        scene.getStylesheets().add("stylesheet.css");

        Label hourLabel = (Label) scene.lookup("#hour_label");
        Label minuteLabel = (Label) scene.lookup("#minute_label");
        Label secondLabel = (Label) scene.lookup("#second_label");
//        hourLabel.setOnMouseClicked(mouseEvent -> hourLabel.setText(String.valueOf(Integer.parseInt(hourLabel.getText()) + 1)));

        int delay = 1000;
        int period = 1000;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    secondLabel.setText(String.valueOf(Integer.parseInt(secondLabel.getText()) - 1));
                });

            }
        }, delay, period);


        stage.setTitle("Shutdown countdown scheduler");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }


}
