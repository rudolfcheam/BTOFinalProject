# 🏠 BTO Management System (SC2002 Project)

This is a CLI-based Build-To-Order (BTO) housing management system built as part of the SC2002 Object-Oriented Programming module.

It supports three types of users:
- 👤 Applicants
- 🛠️ HDB Officers
- 🧑‍💼 HDB Managers

## 📦 Features

### 👤 Applicants
- View eligible and visible BTO projects
- Apply for a flat
- Request to withdraw an application
- View application status
- View application history
- Submit and manage enquiries

### 🛠️ HDB Officers
- Register interest to manage a project
- View and manage applicants under assigned project
- Book flats for successful applicants
- Reply to applicant enquiries

### 🧑‍💼 HDB Managers
- Create, edit, delete BTO projects
- Toggle project visibility
- View all applications under their project(s)
- Approve or reject withdrawal requests
- Approve officer registrations
- Generate project reports
- Manage enquiries

## 🧱 Tech Stack

- Java (JDK 17)
- Object-Oriented Design (Encapsulation, Inheritance, Polymorphism)
- CLI (Command-Line Interface)
- In-memory data store (`DataStore.java`)
- UML Diagrams

## 📂 Project Structure
BTO_Management_System/ ├── src/ │ ├── boundary/ # CLI Interface │ ├── control/ # Controllers │ ├── entity/ # Domain Models │ ├── utility/ # DataStore, validation, etc. │ └── Main.java ├── data/ # Sample user/project data (if externalized) ├── docs/ # UML diagrams, documentation ├── README.md


## 🚀 Getting Started

1. Clone this repo
2. Open in IntelliJ or your preferred IDE
3. Mark `src/` as the source root
4. Run `Main.java`


## 🖼️ UML Diagrams

📌 Located in `/docs`:
- `BTO_ClassDiagram.png`
- `BTO_SequenceDiagram.png`

## 📌 Authors
This project was developed as part of the NTU SC2002 module.  
Team Members:
Santhiya
QiRui
Rudolf

