# Definition of Done (DoD)

A feature or task was considered **Done** when:

1. **Code Criteria**
   - The feature compiles and runs without errors.
   - It is merged into `main` from its feature branch.
   - Functionality verified by running the demo (ControlPanel ↔ SensorNode).

2. **Documentation**
   - Public methods include Javadocs.
   - If a protocol or system change is made, `protocol.md` is updated.

3. **Testing**
   - Manual tests confirmed message exchange and sensor simulation.
   - Console logs (from SLF4J or System.out) verify expected data flow.

4. **Review**
   - The other teammate reviewed changes.
   - Code was demonstrated live or through discord communication.