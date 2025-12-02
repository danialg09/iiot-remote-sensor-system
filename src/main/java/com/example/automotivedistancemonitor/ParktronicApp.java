package com.example.automotivedistancemonitor;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import java.util.Scanner;


public class ParktronicApp extends Application {

    private static final String COM_PORT_NAME = "COM5";
    private static final int BAUD_RATE = 9600;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;

    private Label distanceLabel = new Label("--- CM");
    private Pane radarPane = new Pane();
    private VBox root;

    private Arc arcRed;
    private Arc arcOrange;
    private Arc arcYellow;
    private Arc arcGreen;

    private Thread monitoringThread;

    @Override
    public void start(Stage stage) {

        root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1a1a1a;");

        distanceLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: #00ffc8;"); // Neon color

        radarPane.setMinSize(WIDTH, HEIGHT - 100);

        setupRadar(radarPane);

        root.getChildren().addAll(distanceLabel, radarPane);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("IIoT Automotive Distance Monitor");
        stage.setScene(scene);
        stage.show();

        startMonitoringThread();
    }

    private void setupRadar(Pane pane) {
        double centerX = WIDTH / 2.0;
        double centerY = HEIGHT / 2.0;

        Image carImage = new Image(
                getClass().getResource(
                        "/com/example/automotivedistancemonitor/images/car.png"
                ).toExternalForm()
        );

        ImageView carView = new ImageView(carImage);

        carView.setFitWidth(200);
        carView.setPreserveRatio(true);

        carView.setX(centerX - carView.getFitWidth() / 2);
        carView.setY(centerY - carImage.getHeight() / 2 + 40);


        Polygon carShape = new Polygon(
                centerX - 100, centerY - 250,
                centerX + 100, centerY - 250,
                centerX + 100, centerY + 30,
                centerX - 100, centerY + 30
        );
        carShape.setFill(Color.web("#808080", 0.5));
        carShape.setStroke(Color.web("#808080"));
        carShape.setStrokeWidth(2);

        arcGreen  = createArc(centerX, centerY + 30, 140, 140, Color.web("#1abc9c", 0.18));
        arcYellow = createArc(centerX, centerY + 30, 105, 105, Color.web("#f1c40f", 0.18));
        arcOrange = createArc(centerX, centerY + 30, 75,  75,  Color.web("#e67e22", 0.20));
        arcRed    = createArc(centerX, centerY + 30, 50,  50,  Color.web("#e74c3c", 0.22));

        pane.getChildren().addAll(arcGreen, arcYellow, arcOrange, arcRed, carShape, carView);
    }

    private Arc createArc(double x, double y, double radiusX, double radiusY, Color color) {
        Arc arc = new Arc(x, y, radiusX, radiusY, 180, 180); // стартуем с 180°, идем на 180°
        arc.setType(ArcType.OPEN);
        arc.setStroke(color);
        arc.setStrokeWidth(8);
        arc.setFill(Color.TRANSPARENT);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        return arc;
    }

    private void startMonitoringThread() {
        monitoringThread = new Thread(() -> {
            SerialPort comPort = SerialPort.getCommPort(COM_PORT_NAME);
            comPort.setBaudRate(BAUD_RATE);

            if (comPort.openPort()) {
                System.out.println("Port " + COM_PORT_NAME + " opened successfully!");
                comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                Scanner scanner = new java.util.Scanner(comPort.getInputStream());

                while (scanner.hasNextLine()) {
                    try {
                        String line = scanner.nextLine().trim();
                        int distance = Integer.parseInt(line);

                        Platform.runLater(() -> updateUI(distance));

                    } catch (NumberFormatException e) {
                        System.err.println("Invalid data received: " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Monitoring thread error: " + e.getMessage());
                        break;
                    }
                }
                scanner.close();
                comPort.closePort();

            } else {
                Platform.runLater(() -> distanceLabel.setText("ERROR: PORT " + COM_PORT_NAME + " failed to open."));
                System.err.println("ERROR: Could not open port " + COM_PORT_NAME);
            }
        });
        monitoringThread.setDaemon(true);
        monitoringThread.start();
    }

    private void updateUI(int distance) {

        arcGreen.setFill(Color.web("#1abc9c", 0.15));
        arcYellow.setFill(Color.web("#f1c40f", 0.15));
        arcOrange.setFill(Color.web("#e67e22", 0.15));
        arcRed.setFill(Color.web("#e74c3c", 0.15));

        String labelColor;

        if (distance <= 10) {
            arcRed.setFill(Color.web("#e74c3c", 0.8)); // Bright Red
            labelColor = "#e74c3c";

        } else if (distance <= 20) {
            arcRed.setFill(Color.web("#e74c3c", 0.4)); // Dim Red
            arcOrange.setFill(Color.web("#e67e22", 0.6)); // Orange
            labelColor = "#e67e22";

        } else if (distance <= 35) {
            arcRed.setFill(Color.web("#e74c3c", 0.2));
            arcOrange.setFill(Color.web("#e67e22", 0.3));
            arcYellow.setFill(Color.web("#f1c40f", 0.6)); // Yellow
            labelColor = "#f1c40f";

        } else if (distance <= 50) {
            arcGreen.setFill(Color.web("#1abc9c", 0.5)); // Green
            labelColor = "#1abc9c";
        } else {
            labelColor = "white";
        }

        distanceLabel.setText(distance + " cm");
        distanceLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: " + labelColor + ";");
    }

    @Override
    public void stop() {
        if (monitoringThread != null && monitoringThread.isAlive()) {
            monitoringThread.interrupt();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
