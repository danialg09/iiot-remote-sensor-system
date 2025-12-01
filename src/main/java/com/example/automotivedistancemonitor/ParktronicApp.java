package com.example.automotivedistancemonitor;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class ParktronicApp extends Application {

    // Label для вывода дистанции и root для смены цвета
    private Label distanceLabel = new Label("Distance: --- cm");
    private StackPane root;

    @Override
    public void start(Stage stage) throws Exception {

        // --- 1. UI Setup (Минимальная эстетика) ---
        root = new StackPane(distanceLabel);

        // Темный фон (Automotive Vibe)
        root.setStyle("-fx-background-color: #121212;");
        distanceLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white;");

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Automotive Distance Monitor");
        stage.setScene(scene);
        stage.show();

        // --- 2. Запуск фонового потока ---
        startMonitoringThread();
    }

    private void startMonitoringThread() {

        // 1. Находим и открываем порт (COM5)
        SerialPort comPort = SerialPort.getCommPort("COM5");
        comPort.setBaudRate(9600); // Скорость как в Arduino

        if (comPort.openPort()) {
            System.out.println("Serial Port COM5 opened successfully!");

            // 2. Включаем режим ожидания данных
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            java.util.Scanner scanner = new java.util.Scanner(comPort.getInputStream());

            Thread monitoringThread = new Thread(() -> {
                while (true) {
                    try {
                        // 3. Читаем данные строкой (блокирующая операция, но JSerialComm справляется)
                        if (scanner.hasNextLine()) {
                            String data = scanner.nextLine().trim(); // Читаем строку (до \n)
                            int distance = Integer.parseInt(data);

                            // 4. Обновление UI
                            Platform.runLater(() -> {
                                distanceLabel.setText("Distance: " + distance + " cm");
                                // ... (твоя логика смены цвета)

                            });
                        }

                    } catch (Exception e) {
                        // Порт закрылся или данные приходят кривые
                        System.err.println("Read Error: " + e.getMessage());
                        break;
                    }
                }
            });
            monitoringThread.setDaemon(true);
            monitoringThread.start();

        } else {
            System.err.println("ERROR: Could not open COM5!");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
