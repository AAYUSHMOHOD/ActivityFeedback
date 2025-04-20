# ActivityFeedback

An Android application for managing student feedback and activity forms between professors and students.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Setup Instructions](#setup-instructions)
  - [Prerequisites](#prerequisites)
  - [Firebase Setup](#firebase-setup)
  - [Configuration](#configuration)
- [Usage](#usage)
- [Dependencies](#dependencies)
- [Limitations](#limitations)

## Overview

ActivityFeedback is an Android application that facilitates communication between professors and students through customized feedback forms. It allows professors to create forms, assign them to students, and analyze the results. Students can view and submit their assigned forms.

## Features

- **User Authentication**: Separate login for professors and students
- **Form Creation**: Professors can create custom feedback forms
- **Form Assignment**: Professors can assign forms to specific roll number ranges
- **Form Submission**: Students can submit responses to assigned forms
- **Analytics**: Professors can view and analyze form results
- **Notifications**: Push notifications for form assignments and submissions

## Setup Instructions

### Prerequisites

- Android Studio
- Firebase account
- JDK 11 or higher

### Firebase Setup

1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one
   - Register your application with the package name `com.example.ad_pbl_activityfeedback_q5`

2. **Download google-services.json**:
   - Download the `google-services.json` file from Firebase Console
   - Place it in the `app/` directory of your project

3. **Enable Firebase Services**:
   - Firebase Authentication (Email/Password)
   - Realtime Database
   - Firebase Cloud Messaging

### Configuration

1. **Firebase Database URL**:
   - Open `app/src/main/java/com/example/ad_pbl_activityfeedback_q5/utils/FirebaseHelper.java`
   - Replace `YOUR_DATABASE_URL` with your actual Firebase Realtime Database URL:
   ```java
   private static final String DATABASE_URL = "https://your-project-id.firebaseio.com";
   ```

2. **Database Rules**:
   - Configure your Realtime Database rules in Firebase Console:
   ```json
   {
     "rules": {
       ".read": "auth != null",
       ".write": "auth != null"
     }
   }
   ```

3. **Build Configuration**:
   - Ensure Google services plugin is properly configured in your project-level build.gradle

## Usage

1. **Registration**:
   - Students must register with valid roll numbers (42101-42485)
   - Professors must register with valid institutional emails and IDs

2. **Login**:
   - Use registered credentials to log in
   - System will direct users to their respective dashboards based on role

3. **Form Management**:
   - Professors: Create, assign, and view results of forms
   - Students: Submit responses to assigned forms

## Dependencies

- Firebase Authentication
- Firebase Realtime Database
- Firebase Analytics
- Firebase Cloud Messaging
- MPAndroidChart for data visualization
- AndroidX components

## Limitations

- The password reset functionality currently requires admin user privileges. As professors are not admin users, the forget password feature will not work for them.

---

Last updated: 2025-04-20 21:57:33 by AAYUSHMOHOD
```
