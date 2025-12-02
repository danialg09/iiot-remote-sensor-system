# ***IIoT Remote Sensor Platform (Automotive HMI Prototype)***

## Project Overview

This project is a real-time **Human-Machine Interface (HMI)** application designed to monitor the distance from an object using an **Arduino** board and an **HC-SR04 ultrasonic sensor**. The data is streamed via a serial port and visualized dynamically using a **JavaFX** graphical interface.

The application serves as a fully functional proof-of-concept for an automotive parking assist system, where the background color and visual feedback change instantly based on the proximity of an obstacle.

### Core Features

* **Real-time Data Streaming:** Continuous and reliable data reading from the serial port.
* **HMI Feedback:** Dynamic color-coded alerts (Green, Yellow, Red) based on object distance.
* **System Integration:** Demonstrates integration of high-level Java code with external hardware.
* **Modern Stack:** Built on modern Java standards for clean, maintainable code.

---

## ⚙️ Technology Stack

| Technology | Role | Version / Tool         |
| :--- | :--- |:-----------------------|
| **Language** | Core Language | Java 17+               |
| **GUI Framework** | Visual Interface | JavaFX                 |
| **Build System** | Dependency Management | Maven                  |
| **Serial Communication**| COM Port Handling | JSerialComm            |
| **Hardware** | Sensor/Microcontroller | Arduino NANO / HC-SR04 |

---

## 🚀 Getting Started

### Prerequisites

1.  **Java Development Kit (JDK):** Version 17 or higher.
2.  **Maven:** Installed and configured.
3.  **Arduino IDE:** With the ultrasonic sensor code uploaded and running at 9600 baud.
4.  **COM Port:** Ensure the Arduino is connected and identify its COM port number (e.g., COM5).

### 1. Hardware Setup (Arduino)

* Connect the **HC-SR04 sensor** to the Arduino board (VCC, GND, Trigger, Echo).
* Upload the prepared Arduino sketch to output the raw distance values (integers followed by newline) to the Serial Monitor at 9600 baud.
* **Crucially:** Ensure the **Serial Monitor is closed** before running the Java application, as the COM port can only be accessed by one program at a time.

### HC-SR04 Wiring
Connect the ultrasonic sensor to the **Arduino Nano** according to the following scheme:

| Sensor Pin (HC-SR04) | Arduino Nano Pin  |
| :--- |:------------------|
| **VCC** | 5V                |
| **GND** | GND               |
| **Trig** | **Digital Pin 7** |
| **Echo** | **Digital Pin 6** |

[➡️ **View the Arduino Code:**](arduino/park2.0.ino)

### 2. Software Setup & Run

1.  **Configure COM Port:** Update the COM port name in the Java source file (`ParktronicApp.java`) to match your Arduino's port (e.g., change `"COM5"` to your port).
2.  **Build and Run:** Use the Maven command line tool to build the project and launch the JavaFX application:

```bash
# Clean the project, compile dependencies, and run the main class
mvn clean javafx:run
```
## ⚠️ *Note on Development Path*

*The initial implementation of this project utilized a custom **JNI (Java Native Interface)** layer with WinAPI (C++) to connect the application to the serial port.*

*This was done to demonstrate proficiency in native I/O integration and toolchain management. However, the solution was refactored to use the **JSerialComm** library in the final product to ensure greater stability, cross-platform compatibility, and ease of maintenance.*
