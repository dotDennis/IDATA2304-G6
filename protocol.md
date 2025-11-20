# Communication Protocol for Smart Greenhouse System

## 1. Introduction

This document describes the application-layer communication protocol used for data exchange between sensor/actuator 
nodes and control-panel nodes in the Smart Greenhouse Management system, by Student Made Gadgets.

The protocol enables:

- Sensor data transmission from sensor nodes to control panels
- Actuator status reporting
- Remote control commands from control panels to sensor nodes
- Node discovery and identification

## 2. Terminology

- **Sensor Node**: A physical device that hosts sensors and actuators in the greenhouse (simulated & embedded in).
- **Control Panel Node**: A device that displays sensor data and sends control commands.
- **Sensor**: A device that measures environmental data (temperature, humidity, etc.)
- **Actuator**: A device that performs physical actions (heater, fan, window opener, etc.)
- **Message**: A unit of data transmitted between nodes at application layer.
- **Node ID**: A unique identifier for each node in the system

## 3. Transport Protocol

**Transport Layer**: TCP (Transmission Control Protocol)

**Justification**: TCP was chosen because:
- Reliability is critical for control commands (e.g., turning on/off actuators)
- Guaranteed delivery ensures sensor data is not lost
- Built-in error detection and retransmission simplifies the implementation

### 3.1 Ports

Each sensor node runs a TCP server on a configured port (e.g. in the range of 1024-65535)
Control-panel nodes connect to the appropiate host/port for each node.

Ports are chosen so that:

- They are above 1024 (not a privileged port)
- They avoid common known ports used by system services
- Different embedded sensor nodes can listen on different ports on the same host.

## 4. Architecture

### 4.1 Actors

The system consists of two main types of nodes:

**Sensor Nodes** (Servers)
    - Host sensors and actuators
    - Listen for incoming connections from control panels
    - Push sensor data and actuator status, or periodically
    - Receive and execute control commands

**Control Panel Nodes** (Clients)
    - Connect to sensor nodes
    - Receive, parse and display sensor data
    - Recieve and display actuators states.
    - Send control commands to actuators through sensornode.

### 4.2 Client-Server Model

- **Servers**: Sensor nodes act as TCP servers
- **Clients**: Control panel nodes act as TCP clients
- **Connection Model**: Each control panel can connect to multiple sensor nodes simultaneously
- **Per Node Model**: Only one control panel can connect to the same sensor node at a time.

## 5. Information Flow

### 5.1 Data Flow Model

The protocol uses a **push model** for runtime data:

1. **Connection Establishment**:
    - Control panel initiates TCP connection to a sensor node
    - Sensor node accepts connection
    - Connection remains open the for the duration of the session

2. **Sensor Data Updates**:
    - Sensor nodes automatically push sensor readings at configurable intervals (default every 5 seconds)
    - No explicit request needed from control panel
    - A single DATA message includes all data updates from the node.
    - Updates are saved in a pending queue, to avoid spam.

3. **Actuator Status Updates**:
    - Sent automatically after any actuator state change
    - Sent periodically (every 10 seconds) even if no change

4. **Control Commands**:
    - Control panel sends COMMAND messages at any time
    - Sensor node executes the command if its valid.
    - Sensor node responds with SUCCESS or FAILURE
    - Actuator status (DATA) update is sent after successful execution.

### 5.2 Message Timing

- Sensor data: Pushed every x seconds
- Actuator status: Pushed every x seconds or immediately after state change
- Commands: Sent on-demand by user through control panel.
- Responses: Immediate

(x is configured upon construction, default is 5-10 seconds)

## 6. Protocol Type

### 6.1 Connection-Oriented

The protocol is **connection-oriented**:

- Uses TCP with a persistent connection per control-panel ↔ sensor-node pair.
- Connection established once and stays open until one side closes it.
- Connection remains open during the entire session.

### 6.2 Stateful Protocol

The protocol is **stateful**:

- Each sensor node tracks:
  - Its own node ID.
  - Current sensor readings.
  - Current actuator states.
- Each control panel tracks:
  - Which sensor nodes it is connected to.
  - The latest data and status per node.

The stateful design:
  - Enables the push model (server knows which client to send updates to).
  - Simplifies error detection (connection loss, invalid commands).
  - Retains context (e.g. which actuators exist on a node).

## 7. Message Format

### 7.1 Marshalling Method

The application payloads are encoded as UTF‑8 text and use **separator-based marshalling**:

- Fields are separated by the pipe character: `|`  
- Lists within fields use the comma: `,`  
- Key-value pairs use the colon: `:` `

Example payload (before framing):

```text
DATA|node-01|temperature:22.5,humidity:65.0
```

### 7.1.1 Framing & Encoding

TCP is a byte stream without inherent message boundaries.  
The protocol therefore uses **length-prefixed framing**:

- Each message is sent as:
  ```text
  [ length:int32 (big-endian) ][ payload:byte[length] ]
  ```

- The receiver:
  1. Reads 4 bytes to obtain `length`.  
  2. Reads exactly `length` bytes as the payload.  
  3. Decodes the payload as UTF‑8 text and parses it according to the rules above.

Additional rules:
- **Empty frames:** `length = 0` is reserved for heartbeats/keepalive pings.  
- **Maximum frame size:** Default is **1 MiB**; larger frames are rejected.  
- **Encoding:** Application payloads are UTF‑8 text.  

### 7.2 Message Types

All messages share the same top-level structure:

```text
TYPE|field1|field2|...
```

Where `TYPE` is an uppercase identifier such as `DATA`, `COMMAND`, etc.

#### 7.2.1 (SENSOR) DATA (Sensor Node → Control Panel)

Sends current sensor readings from a sensor node.

**Format:**

```text
DATA|<nodeId>|<sensorKey>:<value>,<sensorKey>:<value>,...
```

where `sensorKey` is normally `sensorType` or `sensorType#deviceId`.

**Example:**

```text
DATA|node-01|temperature:22.5,humidity:65.0
DATA|node-02|temperature#temp-01:18.3,humidity#hum-01:72.5,light#lux-01:450.0
```

**Fields**:
- `nodeId`: Unique identifier of the sensor node.
- `sensorKey`: Either just the type (`temperature`) or `type#deviceId` (`temperature#temp-01`).  
- `value`: Numerical reading from the sensor (double).

**Sender:** Sensor Node (automatically at the configured refresh interval).

---

#### 7.2.2 (ACTUATOR STATUS) DATA (Sensor Node → Control Panel)

Reports current state of all actuators on a sensor node.

**Format:**

```text
DATA|<nodeId>|<actuatorKey>:<state>,<actuatorKey>:<state>,...
```

where `actuatorKey` is typically `actuatorType` or `actuatorType#deviceId`.

**Example**:

```text
DATA|node-01|heater:1,fan:0,window_opener:0
DATA|node-02|heater#heat-01:1,fan#fan-01:1
```

**Fields**:
- `nodeId`: Unique identifier of the sensor node
- `actuatorKey`: The actuator type, optionally with `#deviceId`.
- `state`: 1 for ON, 0 for OFF

**Sender:** Sensor Node (periodically and immediately after changes).

---

#### 7.2.3 COMMAND (Control Panel → Sensor Node)

Sends a control command to change actuator state.

**Format**:

```text
COMMAND|<nodeId>|<actuatorKey>:<action>
```

**Example**:

```text
COMMAND|node-01|heater:1
COMMAND|node-02|fan#fan-01:0
```

**Fields**:
- `nodeId`: Target sensor node id
- `actuatorKey`: Type or type+deviceId of the actuator.  
- `action`: 1 for ON, 0 for OFF

**Sender:** Control Panel (on user request)

---

#### 7.2.4 SUCCESS / FAILURE (Sensor Node → Control Panel)

Indicates whether a COMMAND sent by the control panel was successfully executed by the sensor node.

SUCCESS Message
Sent when the actuator command was received and successfully applied.

**Format:**
```text
SUCCESS|<nodeId>|<actuatorKey>:<newState>
```

**Example**:
```
SUCCESS|node-01|heater:1
SUCCESS|node-02|fan#fan-01:0
```

FAILURE Message
Sent when the actuator command could not be executed (missing actuator, invalid state, etc.).

**Format:**
```text
FAILURE|<nodeId>|<errorMessage>
```

**Example:**
```text
FAILURE|node-01|Unknown actuator: sprinkler
FAILURE|node-02|Invalid command format
```

**Fields**:
- `nodeId`: Sensor node executing (or failing to execute) the command
- `actuatorKey`: {actuatorType} or {actuatorType#deviceId}
- `newState`: 1 for ON, 0 for OFF (SUCCESS only)
- `errorMessage`: Human-readable error explanation (FAILURE only)

**Sender**: Sensor Node (immediately after command processing.

#### 7.2.5 ERROR (Sensor Node → Control Panel)

Reports protocol-level errors, such as malformed frames, unknown message types, or invalid formatting.

**Format:**

```text
ERROR|<nodeId>|<errorMessage>
```

**Example:**

```text
ERROR|node-01|Unknown actuator: sprinkler
ERROR|node-02|Invalid command format
ERROR|node-03|Frame exceeds maximum allowed size
```

**Fields:**

- `nodeId`: Sensor node reporting the error.  
- `errorMessage`: Human-readable error description.  

**Sender:** Sensor Node.

---

## 8. Error Handling

**Scenario**: A node receives a message that doesn't match any known format.

**Action**:
- The receiving node logs the error locally
- Sends an ERROR message back to sender: 
  ```text
  ERROR|<nodeId>|Invalid message format
  ```
- Ignores the invalid message and continues operation

### 8.1 Connection Loss

**Scenario**: TCP connection is broken unexpectedly.

**Sensor Node Action:**
- Detects the broken connection (exception/timeout).  
- Closes the socket and cleans up any state related to that client.  
- Continues listening for new connections from other or the same control panels.

**Control Panel Action:**
- Detects connection loss (exception/timeout).  
- Could attempts reconnect after a delay (e.g. 5 seconds), with a limited number of retries. (Not implemented)

### 8.2 Invalid Node ID

**Scenario**: Control panel sends command with wrong node ID.

**Action**:
- Sensor node ignores commands not addressed to its own node ID
- No response sent (command was not meant for this node)

## 9. Realistic Scenario

### Scenario: Farmer monitors greenhouse and adjusts temperature

**Initial Setup**:
- Sensor Node `node-01` is running in a greenhouse with a temperature sensor and a heater actuator.  
- A Control Panel connects to `node-01`.

**Step-by-Step Message Flow**:

1. **Connection Established**
    - Control Panel opens TCP connection to sensor node `node-01`.  
    - Connection is established

2. **Automatic Sensor Data (every ~5 seconds)**

Sensor Node → Control Panel:

   ```text
   DATA|node-01|temperature:18.5
   ```

- Farmer sees temperature is 18.5°C

3. **Automatic Actuator Status (every ~10 seconds)**

Sensor Node → Control Panel: DATA|1|heater:0

- Farmer sees heater is currently OFF

4. **Next Sensor Reading (5 seconds later)**

Sensor Node → Control Panel:

   ```text
   DATA|node-01|temperature:18.2
   ```

- Temperature dropped to 18.2°C

5. **Farmer Decides to Turn On Heater**

Control Panel → Sensor Node:

   ```text
   COMMAND|node-01|heater:1
   ```

- Control panel sends command to turn on heater

6. **Sensor Node Responds**

Sensor Node → Control Panel:

   ```text
   SUCCESS|node-01|heater:1
   DATA|node-01|heater:1
   ```

- Acknowledgment sent immediately
- Updated actuator status follows

7. **Continued Monitoring (5 seconds later)**

Sensor Node → Control Panel:

   ```text
   DATA|node-01|temperature:19.5
   ```

- Temperature rising due to heater being on

8. **Regular Status Update (10 seconds after step 6)**

Sensor Node → Control Panel:

   ```text
   DATA|node-01|heater:1
   ```

- Periodic status confirmation

**User Perspective**:
- Temperature is continuously visible and updated.  
- Heater status is visible with an ON/OFF indicator.  
- User action (“Turn on heater”) results in immediate UI feedback via SUCCESS and DATA.  

## 10. Error Handling

Error messages use the ERROR type as described in §7.2.5:

```text
ERROR|<nodeId>|<errorMessage>
```

Implementations may extend this as needed, provided they follow the same format.

## 11. Security Mechanisms

**Current Implementation**:
  - No authentication or encryption is implemented in the current version.  
  - The protocol is intended for use in trusted local networks.  

**Potential Future Enhancements**:
- TLS/SSL encryption for TCP connections
- Authentication mechanism for control panels (username/password)
- Authorization (which users can control which actuators)
- Message signing or MACs to prevent tampering.  

## 12. Reliability & Liveness

The protocol builds on TCP’s reliability and adds additional mechanisms to detect silent failures and keep connections healthy.

- **Transport guarantees:**  
  TCP provides ordered, reliable delivery. The protocol adds explicit message boundaries via **length-prefixed framing** (§7.1.1).

### 12.1 Timeout Policy:

- **Timeout policy:**  
  - Connection loss is detected when a read operation fails or fails to establish connection.
    - The sensor nod closes the client socket and continues listening for new connections.
    - The control panel does not reconnect; the user must initiate a new connection manually.

  - Automatic reconnection is considered future work, since we're running on simulated nodes.

The current implementation sends heartbeats but does not auto-reconnect.
