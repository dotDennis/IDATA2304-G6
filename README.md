# 📦 Project Deliverables – IDATA2304 SMG Project

This document outlines all the **required deliverables** for the Student made gadgets project.

---

## 🧩 1. Protocol Design

You must design and document your own **application-layer protocol** that defines how sensor, control, and server nodes communicate.

### Deliverable
- A file named **`protocol.md`** located in your repository root.

### The protocol documentation must include:
- **Overview and purpose**  
  Explain what the protocol does and why it’s needed.
- **Terminology**  
  Define key terms (sensor, actuator, control panel, message, etc.).
- **Actors**  
  Describe the communicating nodes (sensors, control panels, server if used).
- **Transport type and port**  
  Specify whether the protocol uses TCP or UDP and which port(s).
- **Message flow & architecture**  
  Include message diagrams, node interactions, and data flow.
- **Protocol type**  
  Connection-oriented/less, stateful/stateless, synchronous/asynchronous.
- **Message format**  
  Define syntax, message structure, constants, and marshalling/unmarshalling.
- **Error handling & reliability**  
  Explain how your system manages lost connections or malformed messages.
- **Security**  
  Describe any authentication, encryption, or access control mechanisms.
- **Example scenario**  
  Show a real example of message exchange between nodes.

---

## ⚙️ 2. Programming Requirements

You must implement a **complete distributed system** with:
- **Sensor/Actuator Nodes**
    - Unique IDs
    - Simulated sensor data (e.g., temperature, humidity)
    - Actuators that can be remotely controlled
- **Control Panel Nodes**
    - Lists connected sensors
    - Displays sensor and actuator data
    - Sends control commands
- **Optional Server Node**
    - Acts as a message broker or routing layer

### Technical requirements
- Multiple sensors and control panels can communicate simultaneously.
- Supports **command sending** and **data reception**.
- Handles **errors and disconnections** gracefully.
- Code should be **modular, scalable**, and **cleanly structured**.
- GUI or TUI interface is allowed, but not required.

---

## 🧱 3. Code Quality

Your code must:
- Follow **clean code principles** (naming, structure, cohesion).
- Include **comments** for AI-generated or adapted code.
- Use **Git** throughout development — not just at the end.
- Keep a **clear project structure** (no “v2-final-final” folders).

---

## 🗂️ 4. Work Process Documentation

You must document your **team’s workflow**, including:
- Sprint planning and backlog
- Task assignment and team roles
- Sprint progress and retrospectives
- Clear **Definition of Done** for tasks

### Deliverable
- Markdown files such as:
    - `sprint1.md`, `sprint2.md`, `retrospective.md`, etc.
- Alternatively, screenshots or exports from Jira, Trello, or similar.

---

## 🎥 5. Presentation Video

A **10–15 minute** video presentation in **English** including:
1. Introduction (problem & solution)
2. Research and approach
3. Work process (roles, sprints, tools)
4. System architecture (nodes, data flow)
5. Protocol summary
6. Demonstration
7. Extra features
8. Improvements or future work

---

## 💡 6. Optional Features (for higher grades)

You can earn higher grades by implementing **advanced network features**, such as:
- Connection resilience and reconnection logic
- Secure data transfer or encryption
- Automatic node ID/address assignment (DHCP-like)
- Image or multimedia transmission
- Command broadcast or multicast
- Adjustable data frequency or precision

---

## 📤 7. Final Submission (Inspera)

Submit the following before the deadline:
- ✅ GitHub repository link
- ✅ Candidate-to-GitHub username list
- ✅ `protocol.md` file
- ✅ Sprint documentation
- ✅ Video presentation

---

### 🗓️ Deadline
**Opens:** 16 November 2025 – 09:00  
**Closes:** 21 November 2025 – 12:00

---

### ✅ Summary
| Category | Deliverable | Format |
|-----------|--------------|--------|
| Protocol Design | `protocol.md` | Markdown |
| System Code | Complete Java (Maven) project | Code |
| Workflow | Sprint reports, retrospectives | Markdown / screenshots |
| Video | Presentation and demo | Video file / YouTube link |
| Submission | GitHub link + Inspera form | Link |

---