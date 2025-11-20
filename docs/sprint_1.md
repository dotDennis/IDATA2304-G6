# Sprint 1 – Core Architecture and Communication Layer

**Duration:** Nov 11 – Nov 15, 2025  
**Focus:** Setting up the base system and achieving stable TCP communication.


## Goals
- Implement all the base entities: `Node`, `Device`, `Sensor`, `Actuator`, `SensorNode`, etc.
- Implement TCP `Connection` with framing and validation
- Implement the communication between ControlPanel and SensorNode.
- Simulate real sensors and actuators.
- Write `protocol.md` for the protocol setup.
- Build a runnable TUI demo (`ControlPanelTuiDemo`) for demonstration.
- Ensure message parsing, sending, and response handling all function correctly.


## Actual Work Done
| Task | Status |
|------|--------|
| Base entities: `Device`, `Sensor`, `Actuator`, `Node`, etc. | ✅ |
| TCP networking: `Connection`, `TcpServer`, `TcpClient` | ✅ |
| Implement `ClientHandler` for server-side connections |  ✅ |
| Implement `ControlPanel` and `SensorNodeClient` | ✅ |
| Simulated sensors + actuators (temperature, humidity, fan, heater) | ⏳ - can improve simulation & actuator effect. |
| Wrote `protocol.md` | ⏳ - yes, but needs wording updates. |
| Create and run `ControlPanelTuiDemo` | ✅ |
| Ensured message communication and responses work correctly | ⏳ - to some degree, will add testing in 2nd sprint.|


## Notes
- The overall architecture worked as planned. The ControlPanel and SensorNode were able to communicate reliably through TCP using the framed message system we established.
- Implementing Connection wrapper early proved quite useful. It made our project stable and easier to plug in both client and server logic faster.
- The ControlPanelTui (Demo) provided an effective way for us to test the communication flow in real time, especially with the LOGGER imported from a previous computer science project.
- Some aspects, like actuator effects and richer sensor simulation were kept simple at this stage, to prioritize functionality over realism.
- Testing was postponed, and LOGGING is slowly making its way into the project. Exstensive testing, logging and protocol updates will arrive in sprint 2.


## Retrospective

### What went well:
- The overall system design came together quickly, and the TCP communication worked on the first full run.
- Good divison of work, one focused on the server side, and other on the client side.
- Pair programming and quick iteration through Git branches made debugging faster than expected.

### What could be improved
- We should have planned clearer testing routines earlier, most validation happened through manual testing in TUI.
- Sensor simulations could be more dynamic and realistic
- We should have documented the protocol and architecture changes more continuously instead of after it was stable.