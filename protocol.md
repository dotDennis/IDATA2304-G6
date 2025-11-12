# Communication Protocol for Smart Greenhouse System

## 1. Introduction

This document describes the application-layer communication protocol used for data exchange between sensor/actuator 
nodes and control-panel nodes in the Smart Greenhouse Management system.

The protocol enables:
- Sensor data transmission from sensor nodes to control panels
- Actuator status reporting
- Remote control commands from control panels to sensor nodes
- Node discovery and identification

## 2. Terminology

- **Sensor Node**: A physical device that hosts sensors and actuators in the greenhouse
- **Control Panel Node**: A device that displays sensor data and sends control commands
- **Sensor**: A device that measures environmental data (temperature, humidity, etc.)
- **Actuator**: A device that performs physical actions (heater, fan, window opener, etc.)
- **Message**: A unit of data transmitted between nodes
- **Node ID**: A unique identifier for each node in the system

## 3. Transport Protocol

**Transport Layer**: TCP (Transmission Control Protocol)

**Justification**: TCP was chosen because:
- Reliability is critical for control commands (e.g., turning on/off actuators)
- Guaranteed delivery ensures sensor data is not lost
- Ordered delivery prevents confusion in data interpretation
- Built-in error checking simplifies implementation

**Port Number**: 12345

**Justification**: Port 12345 is:
- Above 1024 (not a privileged port)
- Not commonly used by other services
- Easy to remember for testing

## 4. Architecture

### 4.1 Actors

The system consists of two types of nodes:

**Sensor Nodes** (Servers)
    - Host sensors and actuators
    - Listen for incoming connections from control panels
    - Send sensor data when requested, or periodically
    - Receive and execute control commands

**Control Panel Nodes** (Clients)
    - Connect to sensor nodes
    - Request and display sensor data
    - Send control commands to actuators
    - Monitor actuator states

### 4.2 Client-Server Model

- **Servers**: Sensor nodes act as TCP servers, listening on port 12345
- **Clients**: Control panel nodes act as TCP clients, connecting to sensor nodes
- **Connection Model**: Each control panel can connect to multiple sensor nodes simultaneously
- **Multiple Clients**: Multiple control panels can connect to the same sensor node

## 5. Information Flow

### 5.1 Data Flow Model

The protocol uses a **Push Model** for sensor data:

1. **Connection Establishment**:
    - Control panel initiates TCP connection to sensor node
    - Sensor node accepts connection
    - Connection remains open for the duration of the session

2. **Sensor Data Broadcasting**:
    - Sensor nodes automatically push sensor readings at regular intervals (every 5 seconds)
    - No explicit request needed from control panel
    - Data includes all sensors attached to that node

3. **Actuator Status Updates**:
    - Sent automatically after any actuator state change
    - Sent periodically (every 10 seconds) even if no change

4. **Control Commands**:
    - Control panel sends commands at any time
    - Sensor node responds with acknowledgment
    - Actuator status update follows immediately

### 5.2 Message Timing

- Sensor data: Pushed every 5 seconds
- Actuator status: Pushed every 10 seconds or immediately after state change
- Commands: Sent on-demand by control panel
- Responses: Immediate (within 100ms)

## 6. Protocol Type

### 6.1 Connection-Oriented

The protocol is **connection-oriented**:
- Uses TCP which maintains a persistent connection
- Connection established once at startup
- Connection remains open during the entire session
- Proper connection closing with disconnect messages

### 6.2 Stateful Protocol

The protocol is **stateful**:
- Server maintains state of connected clients
- Each sensor node remembers which control panels are connected
- Sensor node tracks current sensor readings and actuator states
- Control panel maintains knowledge of connected sensor nodes

**Justification**:
- Stateful design allows efficient push model (server knows who to push to)
- Connection state enables proper error detection (broken connections)
- Maintains context for commands (which actuator belongs to which node)


## 7. Message Format

### 7.1 Marshalling Method

The protocol uses **separator-based marshalling**:
- Fields separated by pipe character `|`
- Multiple values within a field separated by comma `,`
- Key-value pairs use colon `:`

### 7.1.1 Framing & Encoding

**Why:** TCP is a byte stream (no message boundaries). We add an explicit frame so each application message is received whole.

- **Framing:** Every message is sent as **[ length:int32 (big-endian) ][ payload:byte[length] ]**.
  - Receiver first reads 4 bytes (length), then reads exactly `length` bytes as the message payload.
  - **Empty frames:** `length = 0` is allowed (reserved for heartbeat/keepalive pings).

- **Encoding:** Unless otherwise stated, the payload is a **UTF-8 encoded string** carrying the protocol message
  (e.g., `HELLO|sensor-01|caps=TEMP,HUMID` or JSON if we choose to).
  
- **Maximum frame size:** Default **1 MiB** (configurable). Frames above this are rejected.

- **Timeouts:** Implementations may use a socket read timeout. On timeout, a node can (a) send a heartbeat, (b) log and continue, or (c) reconnect per reliability policy.

- **Examples:**
  - `HELLO|control-01`
  - `DATA|sensor-01|temperature=22.5,humidity=65`
  - `COMMAND|control-01->sensor-01|actuator=heater|action=ON`

**Example**: `SENSOR_DATA|1|temperature:22.5,humidity:65.0`

### 7.2 Message Types

#### 7.2.1 SENSOR_DATA (Sensor Node → Control Panel)

Sends current sensor readings from a sensor node.

**Format**: `SENSOR_DATA|<nodeId>|<sensorType>:<value>,<sensorType>:<value>,...`

**Example**:
```
SENSOR_DATA|1|temperature:22.5,humidity:65.0
SENSOR_DATA|2|temperature:18.3,humidity:72.5,light:450.0
```

**Fields**:
- `nodeId`: Unique identifier of the sensor node
- `sensorType`: Type of sensor (temperature, humidity, light, ph, wind_speed, fertilizer)
- `value`: Numerical reading from the sensor

**Sent by**: Sensor Node (automatically every 5 seconds)

---

#### 7.2.2 ACTUATOR_STATUS (Sensor Node → Control Panel)

Reports current state of all actuators on a sensor node.

**Format**: `ACTUATOR_STATUS|<nodeId>|<actuatorType>:<state>,<actuatorType>:<state>,...`

**Example**:
```
ACTUATOR_STATUS|1|heater:1,fan:0,window_opener:0
ACTUATOR_STATUS|2|heater:1,fan:1
```

**Fields**:
- `nodeId`: Unique identifier of the sensor node
- `actuatorType`: Type of actuator (fan, heater, window_opener, valve, door_lock, light_switch)
- `state`: 1 for ON, 0 for OFF

**Sent by**: Sensor Node (every 10 seconds or after state change)

---

#### 7.2.3 COMMAND (Control Panel → Sensor Node)

Sends a control command to change actuator state.

**Format**: `COMMAND|<nodeId>|<actuatorType>:<action>`

**Example**:
```
COMMAND|1|heater:1
COMMAND|2|fan:0
```

**Fields**:
- `nodeId`: Target sensor node identifier
- `actuatorType`: Type of actuator to control
- `action`: 1 for ON, 0 for OFF

**Sent by**: Control Panel (on user request)

---

#### 7.2.4 ACK (Sensor Node → Control Panel)

Acknowledges successful command execution.

**Format**: `ACK|<nodeId>|<actuatorType>:<state>`

**Example**:
```
ACK|1|heater:1
ACK|2|fan:0
```

**Fields**:
- `nodeId`: Sensor node that executed the command
- `actuatorType`: Actuator that was controlled
- `state`: New state (1 for ON, 0 for OFF)

**Sent by**: Sensor Node (immediately after executing command)

---

#### 7.2.5 ERROR (Sensor Node → Control Panel)

Reports an error in command processing.

**Format**: `ERROR|<nodeId>|<errorMessage>`

**Example**:
```
ERROR|1|Unknown actuator type: sprinkler
ERROR|2|Invalid command format
```

**Fields**:
- `nodeId`: Sensor node reporting the error
- `errorMessage`: Human-readable error description

**Sent by**: Sensor Node (when command cannot be executed)

## 8. Error Handling

### 8.1 Invalid Message Format

**Scenario**: A node receives a message that doesn't match any known format.

**Action**:
- The receiving node logs the error locally
- Sends an ERROR message back to sender: `ERROR|<nodeId>|Invalid message format`
- Ignores the invalid message and continues operation

### 8.2 Unknown Actuator Type

**Scenario**: Control panel sends command for actuator that doesn't exist on sensor node.

**Action**:
- Sensor node sends ERROR message: `ERROR|<nodeId>|Unknown actuator: <actuatorType>`
- No state change occurs

### 8.3 Connection Loss

**Scenario**: TCP connection is broken unexpectedly.

**Action**:
- Sensor node: Detects broken connection, cleans up resources, continues listening for new connections
- Control panel: Detects broken connection, attempts to reconnect after 5 seconds, retries up to 3 times

### 8.4 Invalid Node ID

**Scenario**: Control panel sends command with wrong node ID.

**Action**:
- Sensor node ignores commands not addressed to its own node ID
- No response sent (command was not meant for this node)

## 9. Realistic Scenario

### Scenario: Farmer monitors greenhouse and adjusts temperature

**Initial Setup**:
- Sensor Node 1 is running in a greenhouse with temperature sensor and heater
- Control Panel connects to Sensor Node 1

**Step-by-Step Message Flow**:

1. **Connection Established**
    - Control Panel opens TCP connection to Sensor Node 1 on port 12345
    - Connection is established

2. **Automatic Sensor Data (5 seconds)**

Sensor Node → Control Panel: SENSOR_DATA|1|temperature:18.5

- Farmer sees temperature is 18.5°C

3. **Automatic Actuator Status (10 seconds)**

Sensor Node → Control Panel: ACTUATOR_STATUS|1|heater:0

- Farmer sees heater is currently OFF

4. **Next Sensor Reading (5 seconds later)**

Sensor Node → Control Panel: SENSOR_DATA|1|temperature:18.2

- Temperature dropped to 18.2°C

5. **Farmer Decides to Turn On Heater**

Control Panel → Sensor Node: COMMAND|1|heater:1

- Control panel sends command to turn on heater

6. **Sensor Node Responds**

Sensor Node → Control Panel: ACK|1|heater:1
Sensor Node → Control Panel: ACTUATOR_STATUS|1|heater:1

- Acknowledgment sent immediately
- Updated actuator status follows

7. **Continued Monitoring (5 seconds later)**

Sensor Node → Control Panel: SENSOR_DATA|1|temperature:19.5

- Temperature rising due to heater being on

8. **Regular Status Update (10 seconds after step 6)**

Sensor Node → Control Panel: ACTUATOR_STATUS|1|heater:1

- Periodic status confirmation

**User Perspective**:
- Farmer sees temperature displayed and updating every 5 seconds
- Farmer sees heater status (ON/OFF indicator)
- Farmer clicks "Turn ON Heater" button
- Heater icon immediately changes to ON
- Temperature gradually increases over time

## 10. Reliability Mechanisms

The protocol relies primarily on TCP's built-in reliability:

- **Guaranteed Delivery**: TCP ensures all messages arrive in order
- **Connection Monitoring**: Both nodes detect broken connections through TCP
- **Reconnection**: Control panels automatically attempt reconnection after connection loss
- **Acknowledgments**: ACK messages confirm command execution at application level

**Potential Future Enhancements**:
- Message sequence numbers to detect missing messages
- Heartbeat messages to detect silent failures
- Command timeout and retry mechanism

## 11. Security Mechanisms

**Current Implementation**:
No security mechanisms are implemented in the current version.

**Justification**:
- Focus is on learning network protocols and socket programming
- Security adds significant complexity
- Suitable for isolated, trusted local networks

**Potential Future Enhancements**:
- TLS/SSL encryption for TCP connections
- Authentication mechanism for control panels
- Authorization (which users can control which actuators)
- Message signing to prevent tampering