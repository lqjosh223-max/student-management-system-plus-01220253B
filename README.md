# Student Management System 

A professional offline desktop application for managing student records built with JavaFX, SQLite, and JDBC.

## Features

* **Student Management**: Add, view, update, and delete student records
* **Search \& Filter**: Search by ID/name, filter by programme/level/status
* **Reports**: Top performers, at-risk students, GPA distribution, programme summary
* **Import/Export**: CSV import with validation, CSV export for all reports
* **Settings**: Configurable at-risk GPA threshold, database backup
* **Logging**: All operations logged to data/app.log

## Technical Requirements

* **Java**: JDK 25
* **JavaFX**: 21.0.2
* **Database**: SQLite 3.45.1.0
* **Build Tool**: Maven 3.9.0+
* **Platform**: Windows 10/11

## How to Run on Windows

### Step 1: Install JDK 25

Download from: https://www.oracle.com/java/technologies/downloads/

### Step 2: Install JavaFX SDK

Download from: https://gluonhq.com/products/javafx/
Extract file

### Step 3 : Install IDE

You can install any IDE that supports Java but IntelliJ is Recommended
Download from https://www.jetbrains.com/idea/download/

### Step 4: Download file

Download file as zip and extract
open ide and open project from the extracted file
Open MainApp.java
Click on edit configuration and add VM Option from VMOptions.txt
NB : Use the file path which the javafx SDK was extracted to
Run

