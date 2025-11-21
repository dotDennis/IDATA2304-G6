# Student Made Gadgets - A smart greenhouse solution

A distributed network application for monitoring and controlling greenhouse environments.

## 📋 Overview

TCP-based system with **sensor nodes** (temperature, humidity, light, pH, wind, fertilizer) and **actuators** (heater, fan, window, valve, lock, light) communicating with **control panels** via a custom protocol.

**Course Project**: IDATA2304 - NTNU 2025

## Features

- 6 sensor types + 6 actuator types
- TCP client-server architecture with custom protocol
- JavaFX GUI with real-time updates
- Dynamic device management
- Data logging to CSV
- Saving and Loading configuration from JSON

## Architecture

```
Greenhouse → SensorNode (TCP Server) ⟷ ControlPanel (TCP Client) → GUI
```

**Layers**: Entity → Logic → Network → Protocol → UI

## Protocol

**Format**: `TYPE|nodeId|data`

**Examples**:
```
DATA|sensor-01|temperature#temp-01:22.5
COMMAND|sensor-01|heater:1
SUCCESS|sensor-01|heater:1
```

**Message Types**: HELLO, WELCOME, DATA, COMMAND, SUCCESS, FAILURE, ERROR, KEEPALIVE

See [`protocol.md`](protocol.md) for complete specification.

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+

### Installation & Run

```bash
git clone <repository-url>
cd IDATA2304-G6
mvn clean package
mvn javafx:run
```

### Quick Start

1. Launch application
2. Click "Add Control Node"
3. Add sensor node (ID: `sensor-01`, host: `localhost`, port: `12345`)
4. Add sensors and actuators
5. Monitor and control devices in real-time

## Usage

### Creating Embedded Sensor Nodes

Use the GUI or programmatically:

```java
EmbeddedSensorNodeManager manager = new EmbeddedSensorNodeManager();
manager.createNode("sensor-01", "localhost", 12345, 5000);
manager.addSensor("sensor-01", SensorType.TEMPERATURE, "temp-01", 5000);
manager.addActuator("sensor-01", ActuatorType.HEATER, "heater-01");
```

### Sending Commands

```java
controller.sendCommand("sensor-01", "heater", true);  // Turn ON
controller.requestNodeRefresh("sensor-01", RefreshTarget.ALL);
```

### Sensor History

Data logged to CSV: `history/[timestamp]/[nodeId]/[sensor].csv`

## Testing

```bash
mvn test  # Run all tests
```

**Coverage**: 8 test classes, 200+ test methods covering entities, logic, and protocol layers.

## 📂 Project Structure

```
src/main/java/group6/
├── entity/          # Sensors, actuators, nodes
├── logic/           # Factories, registries, history
├── net/             # TCP client/server
├── protocol/        # Messages, device keys
└── ui/              # JavaFX controllers & views

src/test/java/group6/
├── entity/          # Entity tests
├── logic/           # Factory tests
└── protocol/        # Protocol tests
```

## Configuration

Config saved to `resources/config.json`:
- Control panels and sensor nodes
- Device configurations
- Update intervals

## Contributors

**Group 6 - IDATA2304**
- **dotDennis**
- **Fidjor**

## 📚 Resources

- [`protocol.md`](protocol.md) - Complete protocol specification
- [`entities.puml`](entities.puml)`entities.puml` - Entity model diagram
- [`physical2logical.puml`](physical2logical.puml)
---
