# Team Workflow Overview

**Project:** Student made gadgets (SMG)  
**Team Members:**  
- dotDennis - Code architecture, TCP networking, GUI styling, Testing  
- Fidjor - Sesnors/Actuators, GUI architecture, Message protcol, Control panel logic. 

**Duration:** 2 sprints (approx. 5 days each)  
**Deadline:** Nov. 21, 2025 - 12:00 


## Workflow

Since this ended up as a two-person project with a short timeline, we used a lightweight workflow based on clear communication and Git branching:

- **Planning:**  
  Each sprint started with a short discussion on Discord where we divided tasks verbally (e.g., *“You take the server side, I’ll handle the control panel”*).
  
- **Task Tracking:**  
  Instead of Trello or Jira, we relied on **GitHub branches** and commit messages to represent progress. 
  Each major feature was implemented on its own branch (e.g., `sensor-node- implement`, `implement-net`, `tui-implement`), then merged into `main` when complete and reviewed by the other person. Sometimes we may have pushed directly to main without a PR, but since we were two on the project, communication was clear between us whenever that happened by accident. Meaning we did not have to rollback and PR push it instead.

- **Communication:**  
  We coordinated continuously on Discord, often sending code snippets and asking for opinions. Throughout this project we reached out to eachother regarding code or proccess atleast once per day. Meaning we had a 

- **Sprint Rhythm:**  
  - Sprint 1 (Nov 9–14): core entities, TCP layer, and protocol design.  
  - Sprint 2 (Nov 16–21): full integration, ControlPanel demo, documentation, and cleanup.

## Tools Used

| Tool | Purpose |
|------|----------|
| **GitHub** | Version control and branching workflow |
| **VSCode & IntelliJ** | IDEs for coding and testing |
| **Discord** | Communication and quick planning |
| **Maven** | Build management and dependencies |
| **Markdown / Docs folder** | Sprint and workflow documentation |