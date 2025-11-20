# Sprint 2 – GUI, Testing & Documentation

**Duration:** Nov 16 – Nov 21, 2025  
**Focus:** Polish the project with documentation, GUI prototype work, and testing.

## Goals
- Begin transitioning from TUI to a GUI concept.
- Write comprehensive documentation for classes and workflow.
- Finalize `protocol.md`, `logger.xml`, and report files.
- Conduct manual integration testing and final debugging.

## Actual Work Done
| Task | Status |
|------|--------|
| JavaFX Control Panel prototype | ✅ |
| Documentation for entities + helpers | ✅ |
| `protocol.md` refinements + logger configuration | ✅ |
| Automated testing across nodes/actuators | ⏳ – functional but needs work |
| GUI persistence/config loading features | ✅ |
| Final debugging / backlog fixes | ⏳ – most critical bugs fixed, minor UI polish left |

## Notes
- Establishing the GUI scaffolding early paid off; we can now swap between embedded nodes and remote ones from the UI.
- Documentation is now kept inside the repo `docs/`.
- Tighter protocol wording + logging helped track refresh issues much faster during manual tests.

## Retrospective

### What went well
- Clear split between GUI work and documentation/testing let us work together efficiently.
- Reusing helper builders reduced duplication in NodeTab/ControlPanel views.
- The new history logging (CSV per node) offered real feedback for sensors & actuators, even if network goes down.

### What could be improved
- Automated tests from the get go.
- Still too many “helper” classes doing controller work.
- UI Classes do too much, should have seperated behaviour better.
- Planning for config persistence should happen earlier so we don’t bolt it on at the end.
- Should have set up model classes.
- Should have worked more consistently.