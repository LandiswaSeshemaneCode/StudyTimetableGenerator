# Study Timetable Generator

## Overview

Study Timetable Generator is a Java-based intelligent study scheduling system designed to help students prioritise what to study based on confidence, difficulty, completion status, and upcoming assessments.

This project began from a personal problem: needing a smarter way to plan studying and realising that instead of waiting for a solution, I could attempt to build one myself.

Rather than generating random schedules, this system aims to analyse a student's academic preparation and produce more meaningful study priorities.

This is my first larger software project and is being developed not only as a useful tool for students, but also as an opportunity to practise software engineering principles, backend logic design, data structures, and professional development workflows.

---

## Project Goals

The long-term vision is to create an intelligent study planning system capable of:

- Tracking modules and chapters
- Monitoring completion and confidence
- Prioritising weak areas
- Handling multiple assessments per module
- Generating intelligent study schedules
- Eventually evolving into a web application

---

## Current Features

### Module System
- Add modules
- Store modules using object-oriented design
- Support multiple assessments per module

### Chapter Management
- Add chapters to modules
- Store chapters using linked lists
- Track chapter completion
- Track chapter difficulty
- Track chapter confidence

### Module Analytics
- Calculate weighted module confidence
- Calculate module difficulty from chapter difficulty
- Calculate module completion percentage
- Separate confidence from completion tracking

---

## Core Logic

The project treats **completion** and **confidence** as separate concepts.

```text
Completion = whether the content has been covered
Confidence = how well the student understands the content
```

This means a chapter can be:

```text
Completed but still low confidence
```

and therefore still require revision.

Module confidence is calculated using a weighted average:

```text
Module Confidence =
Σ(confidence × difficulty)
-------------------------
Σ(difficulty)
```

This allows more difficult chapters to have a greater impact on overall readiness.

---

## Technologies Used

- Java
- Object-Oriented Programming
- Linked Lists
- ArrayLists
- Git
- GitHub
- IntelliJ IDEA

---

## Development Approach

This project is being built incrementally using professional version control practices and iterative design.

Current focus:

```text
Console application + backend logic
```

Future focus:

```text
Web application + intelligent scheduling engine
```

---

## Planned Features

- Assessment creation
- Assessment-specific chapter tracking
- Priority-based scheduling
- Timetable generation
- Study recommendations
- Progress analytics
- Web application version
- Backend API
- Frontend interface

---

## Why This Project Matters

Studying is often difficult not because students are unwilling to work, but because they are unsure **what to study, when to study it, and how to prioritise limited time**.

The goal of this project is to help solve that problem while demonstrating practical software engineering growth and problem-solving through real development.