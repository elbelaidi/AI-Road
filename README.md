# AI Road Anomaly Detection System

## Table of Contents
- [Project Overview](#project-overview)  
- [Objectives](#objectives)  
- [Features](#features)  
- [Architecture](#architecture)  
- [Technology Stack](#technology-stack)  
- [Backend API Endpoints](#backend-api-endpoints)  
- [Setup Instructions](#setup-instructions)  
- [How to Run](#how-to-run)  
- [Usage](#usage)  
- [Future Improvements](#future-improvements)  
- [License](#license)  
- [Authors](#authors)  

## Project Overview
This project is a comprehensive AI-powered road anomaly detection system designed to collect, analyze, and visualize road surface anomalies such as bumps or potholes detected by vehicles during inspection sessions.

The system consists of two main parts:

- **Android Mobile Application:** Collects sensor data (accelerometer, GPS) during road inspections and visualizes detected anomalies on a Google Map interface.
- **PHP Backend API:** Receives sensor readings, processes anomalies based on acceleration magnitude, stores data in a MySQL database, and provides endpoints to retrieve anomaly details.

## Objectives
- Accurately detect road anomalies by analyzing accelerometer sensor data.
- Visualize anomaly locations on interactive Google Maps.
- Provide detailed information on the severity of each anomaly.
- Enable users to review past inspection sessions and their detected anomalies.
- Build a scalable and maintainable system that can be extended for future features like anomaly classification and report generation.

## Features
- Sensor Data Upload: The Android app sends accelerometer and GPS data to the backend API for anomaly detection.
- Anomaly Detection: Backend calculates acceleration magnitude and flags anomalies exceeding a threshold.
- Session Management: Data grouped by inspection sessions with timestamps.
- Anomaly Visualization: Interactive list of anomalies that opens a popup map showing the exact location.
- Impact Assessment: Classifies anomalies into Low, Moderate, or Hard impacts based on acceleration magnitude.
- User Authentication: Basic login system to associate data with registered users.
- RESTful API: Backend provides endpoints for inserting readings, retrieving anomalies, and user management.

## Architecture
[Android App] <--> [PHP REST API] <--> [MySQL Database]
                        | | |
        Sensor Data Data Processing Data Storage
        (acceleration + GPS) & Anomaly Detection


## Technology Stack
| Component       | Technology / Framework                 |
|-----------------|----------------------------------------|
| Mobile Client   | Android (Java, Google Maps API, Volley)|
| Backend         | PHP 7+, PDO (MySQL database access)    |
| Database        | MySQL / MariaDB                        |
| Data Format     | JSON                                   |
| Version Control | Git (recommended)                      |

## Backend API Endpoints
| Endpoint                       | Method | Description                                                   | Parameters                                                                                              |
|---------------------------     |--------|---------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| `/api/insertReading.php`       | POST   | Insert accelerometer + GPS data, detect anomaly               | JSON body: `username`, `accel_x`, `accel_y`, `accel_z`, `gps_lat`, `gps_lng`, `session_name` (optional) |
| `/api/getAnomalyBySession.php` | GET    | Retrieve anomaly location and impact by session and timestamp | Query: `sessionName`, `timestamp`                                                                       |
| `/api/getUserAnomalies.php`    | GET    | Get all anomalies for a user                                  | Query: `username`                                                                                       |
| `/api/login.php`               | POST   | Authenticate user                                             | JSON body: `username`, `password`                                                                       |                  
| `/api/register.php`            | POST   | Register new user                                             | JSON body: `username`, `email`, `password`                                                              |

## Setup Instructions

### Backend Setup
- Install MySQL and create a database, e.g., `ai_road_db`.
- Import database schema (tables for users, readings, etc.).
- Update database connection details in `../connexion/connexion.php`.
- Deploy PHP files on a web server with PHP and MySQL support (e.g., Apache, Nginx).
- Ensure your PHP environment supports JSON and PDO extensions.

### Android App Setup
- Open the Android project in Android Studio.
- Add your Google Maps API key to the `AndroidManifest.xml` file.
- Modify the base URL for API calls if your backend is hosted on a remote server or different IP.
- Build and run the app on an emulator or physical device.

## How to Run

### Backend
- Start your web server and MySQL database.
- Ensure API endpoints are accessible (test with Postman or browser).

### Android App
- Launch the app on your device/emulator.
- Register or login with a user account.
- Start a new inspection session.
- The app collects accelerometer + GPS data and sends it to the backend.
- View the list of detected anomalies.
- Tap on an anomaly to see its exact location on the map, with impact severity shown.

## Usage
- Use the Android app to perform road inspections while driving.
- Sensor readings are continuously sent to the backend, which detects anomalies in real-time.
- Access detailed anomaly reports by session.
- Review and analyze impact levels to prioritize road maintenance.

## Future Improvements
- Real-time anomaly alerts within the app during inspection.
- Offline data caching with sync when connected.
- Advanced anomaly classification using machine learning on collected data.
- User roles and permissions for admins and inspectors.
- Photo and video attachment for anomaly evidence.
- Dashboard and statistics for administrators on web.

## License

This project is licensed under the MIT License.

MIT License

Copyright (c) 2025
EL-BELAIDI Saad
ELMANSOUR Oualid

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

[... full MIT license text ...]


## Authors
- EL-BELAIDI Saad 
- ELMANSOUR Oualid 
