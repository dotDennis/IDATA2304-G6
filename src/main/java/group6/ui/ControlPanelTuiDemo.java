package group6.ui;

import group6.entity.device.Actuator;
import group6.entity.device.Sensor;
import group6.entity.device.SensorType;
import group6.entity.device.ActuatorType;
import group6.entity.node.ControlPanel;
import group6.entity.node.SensorNode;
import group6.net.TcpServer;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Full demo launcher:
 * - Starts a demo SensorNode with sensors & actuators
 * - Starts TcpServer in background
 * - Runs ControlPanel TUI
 *
 * @author dotDennis, GPT******
 */
public class ControlPanelTuiDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlPanelTuiDemo.class);

    public static void main(String[] args) throws Exception {

        // -------------------------------------------------------
        // 1) Create DEMO SensorNode
        // -------------------------------------------------------
        SensorNode node = new SensorNode("sensor-01");

        // Add demo sensors
        node.addSensor(new Sensor("sensor-01-temp", SensorType.TEMPERATURE) {
            @Override
            public double readValue() {
                return 20 + Math.random() * 5; // Fake temperature
            }
        });

        node.addSensor(new Sensor("sensor-01-hum", SensorType.HUMIDITY) {
            @Override
            public double readValue() {
                return 40 + Math.random() * 20;
            }
        });

        // Add demo actuators
        node.addActuator(new Actuator("heater-01", ActuatorType.HEATER));
        node.addActuator(new Actuator("fan-01", ActuatorType.FAN));

        int demoPort = 12345;

        // -------------------------------------------------------
        // 2) Launch TcpServer in background
        // -------------------------------------------------------
        TcpServer server = new TcpServer(demoPort, node);
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                LOGGER.error("Error in TcpServer demo thread", e);
            }
        }, "DemoServer");
        serverThread.start();

        System.out.println("""
                ┌───────────────────────────────┐
                │   SensorNode Demo Started     │
                ├───────────────────────────────┤
                │ Node ID : sensor-01           │
                │ Host    : localhost           │
                │ Port    : 12345               │
                └───────────────────────────────┘
                """);

        // -------------------------------------------------------
        // 3) Create ControlPanel
        // -------------------------------------------------------
        ControlPanel panel = new ControlPanel("control-01");

        Scanner scanner = new Scanner(System.in);

        // -------------------------------------------------------
        // 4) TUI Loop
        // -------------------------------------------------------
        while (true) {
            System.out.println("""
                    1) Auto-connect to demo node
                    2) Send command (heater/fan)
                    3) Show node data
                    4) Exit
                    """);
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> {
                    panel.connectToSensorNode("sensor-01", "localhost", demoPort);
                }

                case "2" -> {
                    System.out.print("Actuator (heater/fan): ");
                    String actuator = scanner.nextLine().trim();
                    System.out.print("State (1=ON,0=OFF): ");
                    boolean state = scanner.nextLine().trim().equals("1");

                    panel.sendCommand("sensor-01", actuator, state);
                }

                case "3" -> {
                    panel.displayNodeData("sensor-01");
                }

                case "4" -> {
                    panel.shutdown();
                    server.stop();
                    scanner.close();
                    System.out.println("Demo finished. Goodbye.");
                    return;
                }

                default -> System.out.println("Unknown option.");
            }
        }
    }
}
